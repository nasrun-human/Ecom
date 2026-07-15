// ===== CONFIG =====
const API = '/api/public';  // ใช้ relative path (same origin)
const ICONS = ['📦','🎮','👟','👗','📱','💻','🎧','⌚','📸','🛋️','🍕','🌸'];

// ===== STATE =====
let cart = JSON.parse(localStorage.getItem('shopverse_cart') || '[]');
let currentPage = 0;
let totalPages = 0;
let selectedCategory = null;
let searchKeyword = '';

// ===== INIT =====
document.addEventListener('DOMContentLoaded', () => {
  checkConnection();
  loadStats();
  loadCategories();
  loadProducts();
  renderCart();
  document.getElementById('searchInput').addEventListener('keypress', e => {
    if (e.key === 'Enter') searchProducts();
  });
});

// ===== API CONNECTION =====
async function checkConnection() {
  try {
    const res = await fetch(`${API}/ping`);
    const data = await res.json();
    document.getElementById('apiStatus').textContent = data.status === 'OK' ? 'เชื่อมต่อแล้ว' : 'ขาดการเชื่อมต่อ';
  } catch {
    document.getElementById('apiStatus').textContent = 'ขาดการเชื่อมต่อ';
    document.querySelector('.status-dot').style.background = '#ef4444';
    document.querySelector('.status-dot').style.boxShadow = '0 0 8px #ef4444';
  }
}

async function loadStats() {
  try {
    const stats = await fetch(`${API}/stats`).then(r => r.json());
    document.getElementById('totalProducts').textContent = stats.totalProducts || 0;
    document.getElementById('totalCategories').textContent = stats.totalCategories || 0;
  } catch { /* ไม่แสดง error ถ้า stats โหลดไม่ได้ */ }
}

async function checkApiHealth() {
  const modal = document.getElementById('apiModal');
  const overlay = document.getElementById('modalOverlay');
  const body = document.getElementById('modalBody');
  modal.classList.add('open');
  overlay.classList.add('open');
  body.innerHTML = '<p style="color:var(--text-2);text-align:center;padding:20px">กำลังตรวจสอบ...</p>';

  try {
    const [ping, health] = await Promise.all([
      fetch(`${API}/ping`).then(r => r.json()),
      fetch('/actuator/health').then(r => r.json()).catch(() => ({ status: 'N/A' }))
    ]);
    body.innerHTML = `
      <div class="api-row"><span class="api-label">API Status</span><span class="badge ok">${ping.status}</span></div>
      <div class="api-row"><span class="api-label">Message</span><span class="api-value" style="font-family:inherit">${ping.message}</span></div>
      <div class="api-row"><span class="api-label">Version</span><span class="api-value">${ping.version}</span></div>
      <div class="api-row"><span class="api-label">Actuator Health</span><span class="badge ${health.status === 'UP' ? 'ok' : 'error'}">${health.status}</span></div>
      <div class="api-row"><span class="api-label">Base URL</span><span class="api-value">${API}</span></div>
      <div class="api-row"><span class="api-label">Port</span><span class="api-value">8080</span></div>
    `;
  } catch (e) {
    body.innerHTML = `<div class="api-row"><span class="api-label">Status</span><span class="badge error">ไม่สามารถเชื่อมต่อได้</span></div>`;
  }
}

function closeModal() {
  document.getElementById('apiModal').classList.remove('open');
  document.getElementById('modalOverlay').classList.remove('open');
}

// ===== CATEGORIES =====
async function loadCategories() {
  try {
    const data = await fetch(`${API}/categories`).then(r => r.json());
    document.getElementById('totalCategories').textContent = data.length || 0;
    const grid = document.getElementById('categoriesGrid');

    if (!data.length) {
      grid.innerHTML = `
        <div class="empty-state" style="grid-column:1/-1">
          <div class="empty-icon">🏷️</div>
          <h3>ยังไม่มีหมวดหมู่</h3>
          <p>เพิ่มข้อมูลผ่าน API ได้เลย</p>
        </div>`;
      return;
    }

    grid.innerHTML = `
      <div class="category-card ${!selectedCategory ? 'active' : ''}" onclick="filterCategory(null)">
        <span class="cat-icon">🛍️</span>
        <div class="cat-name">ทั้งหมด</div>
        <div class="cat-count">${data.length} หมวดหมู่</div>
      </div>
      ${data.map((c, i) => `
        <div class="category-card ${selectedCategory === c.id ? 'active' : ''}" onclick="filterCategory(${c.id})">
          <span class="cat-icon">${ICONS[i % ICONS.length]}</span>
          <div class="cat-name">${c.name}</div>
          <div class="cat-count">${c.description || ''}</div>
        </div>
      `).join('')}
    `;
  } catch {
    document.getElementById('categoriesGrid').innerHTML =
      '<p style="color:var(--text-3);padding:20px">ไม่สามารถโหลดหมวดหมู่ได้</p>';
  }
}

function filterCategory(id) {
  selectedCategory = id;
  currentPage = 0;
  loadCategories();
  loadProducts();
  document.getElementById('productsSection').scrollIntoView({ behavior: 'smooth' });
}

// ===== PRODUCTS =====
async function loadProducts() {
  const sort = document.getElementById('sortSelect').value;
  let url = `${API}/products?page=${currentPage}&size=12${sort ? '&sort=' + sort : ''}`;
  if (searchKeyword) url = `${API}/search?keyword=${encodeURIComponent(searchKeyword)}&page=${currentPage}`;

  const grid = document.getElementById('productsGrid');
  grid.innerHTML = Array(6).fill('<div class="skeleton-card tall"></div>').join('');

  try {
    const data = await fetch(url).then(r => r.json());
    const products = data.content || data || [];
    totalPages = data.totalPages || 1;

    document.getElementById('totalProducts').textContent = data.totalElements || products.length;

    if (!products.length) {
      grid.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">📦</div>
          <h3>ไม่พบสินค้า</h3>
          <p>ลองค้นหาด้วยคำอื่น หรือเพิ่มสินค้าผ่าน API</p>
          <button class="btn-primary small" onclick="addSampleData()">+ เพิ่มข้อมูลตัวอย่าง</button>
        </div>`;
      document.getElementById('pagination').innerHTML = '';
      return;
    }

    grid.innerHTML = products.map((p, i) => `
      <div class="product-card" onclick="viewProduct(${p.id})">
        <div class="product-img">
          ${p.imageUrl ? `<img src="${p.imageUrl}" alt="${p.name}" style="width:100%;height:100%;object-fit:cover"/>` : ICONS[i % ICONS.length]}
          ${p.salePrice ? '<span class="sale-badge">SALE</span>' : ''}
          <span class="stock-badge">คงเหลือ ${p.stock}</span>
        </div>
        <div class="product-info">
          <div class="product-category">${p.categoryName || 'ไม่มีหมวดหมู่'}</div>
          <div class="product-name">${p.name}</div>
          <div class="product-sku">SKU: ${p.sku}</div>
          <div class="product-price">
            ${p.salePrice
              ? `<span class="price-sale">฿${Number(p.salePrice).toLocaleString()}</span><span class="price-original">฿${Number(p.price).toLocaleString()}</span>`
              : `<span class="price-main">฿${Number(p.price).toLocaleString()}</span>`
            }
          </div>
          <div class="product-actions">
            <button class="btn-cart" onclick="event.stopPropagation(); addToCart(${p.id}, '${p.name}', '${p.sku}', ${p.salePrice || p.price}, ${p.stock})"
              ${p.stock === 0 ? 'disabled' : ''}>
              ${p.stock === 0 ? 'สินค้าหมด' : '🛒 เพิ่มในตะกร้า'}
            </button>
          </div>
        </div>
      </div>
    `).join('');

    renderPagination();
  } catch (e) {
    grid.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">⚠️</div>
        <h3>ไม่สามารถโหลดสินค้าได้</h3>
        <p>กรุณาตรวจสอบว่า Spring Boot กำลังรันอยู่ที่ port 8080</p>
        <button class="btn-outline" onclick="loadProducts()">ลองใหม่</button>
      </div>`;
  }
}

function renderPagination() {
  const el = document.getElementById('pagination');
  if (totalPages <= 1) { el.innerHTML = ''; return; }
  let html = '';
  if (currentPage > 0) html += `<button class="page-btn" onclick="goPage(${currentPage-1})">← ก่อนหน้า</button>`;
  for (let i = Math.max(0, currentPage-2); i <= Math.min(totalPages-1, currentPage+2); i++) {
    html += `<button class="page-btn ${i===currentPage?'active':''}" onclick="goPage(${i})">${i+1}</button>`;
  }
  if (currentPage < totalPages-1) html += `<button class="page-btn" onclick="goPage(${currentPage+1})">ถัดไป →</button>`;
  el.innerHTML = html;
}

function goPage(page) {
  currentPage = page;
  loadProducts();
  document.getElementById('productsSection').scrollIntoView({ behavior: 'smooth' });
}

function searchProducts() {
  searchKeyword = document.getElementById('searchInput').value.trim();
  currentPage = 0;
  loadProducts();
}

function scrollToProducts() {
  document.getElementById('productsSection').scrollIntoView({ behavior: 'smooth' });
}

function viewProduct(id) {
  showToast(`ℹ️ Product ID: ${id}`, 'success');
}

// ===== CART =====
function addToCart(id, name, sku, price, stock) {
  const existing = cart.find(i => i.id === id);
  if (existing) {
    if (existing.qty >= stock) { showToast('⚠️ สินค้าไม่พอ', 'error'); return; }
    existing.qty++;
  } else {
    cart.push({ id, name, sku, price, qty: 1, stock });
  }
  saveCart();
  renderCart();
  showToast(`✅ เพิ่ม "${name}" แล้ว!`, 'success');
}

function removeFromCart(id) {
  cart = cart.filter(i => i.id !== id);
  saveCart(); renderCart();
}

function updateQty(id, delta) {
  const item = cart.find(i => i.id === id);
  if (!item) return;
  item.qty = Math.max(1, Math.min(item.stock, item.qty + delta));
  if (item.qty === 0) { removeFromCart(id); return; }
  saveCart(); renderCart();
}

function saveCart() {
  localStorage.setItem('shopverse_cart', JSON.stringify(cart));
  document.getElementById('cartCount').textContent = cart.reduce((s, i) => s + i.qty, 0);
}

function renderCart() {
  const count = cart.reduce((s, i) => s + i.qty, 0);
  document.getElementById('cartCount').textContent = count;
  const el = document.getElementById('cartItems');
  const footer = document.getElementById('cartFooter');

  if (!cart.length) {
    el.innerHTML = `
      <div class="cart-empty">
        <div class="empty-icon">🛒</div>
        <p>ยังไม่มีสินค้าในตะกร้า</p>
        <button class="btn-primary small" onclick="toggleCart()">เลือกสินค้า</button>
      </div>`;
    footer.style.display = 'none';
    return;
  }

  const total = cart.reduce((s, i) => s + i.price * i.qty, 0);
  el.innerHTML = cart.map(item => `
    <div class="cart-item">
      <div class="cart-item-icon">📦</div>
      <div class="cart-item-info">
        <div class="cart-item-name">${item.name}</div>
        <div class="cart-item-sku">SKU: ${item.sku}</div>
        <div class="cart-item-controls">
          <button class="qty-btn" onclick="updateQty(${item.id},-1)">−</button>
          <span class="qty-display">${item.qty}</span>
          <button class="qty-btn" onclick="updateQty(${item.id},1)">+</button>
          <button class="btn-remove" onclick="removeFromCart(${item.id})">🗑️</button>
        </div>
      </div>
      <div class="cart-item-price">฿${(item.price * item.qty).toLocaleString()}</div>
    </div>
  `).join('');

  document.getElementById('cartTotal').textContent = `฿${total.toLocaleString()}`;
  footer.style.display = 'block';
}

function toggleCart() {
  document.getElementById('cartDrawer').classList.toggle('open');
  document.getElementById('cartOverlay').classList.toggle('open');
}

function checkout() {
  window.location.href = '/checkout.html';
}

// ===== SAMPLE DATA =====
async function addSampleData() {
  showToast('💡 เพิ่มข้อมูลผ่าน API POST /api/categories และ /api/products', 'success');
}

// ===== TOAST =====
function showToast(msg, type = 'success') {
  const toast = document.getElementById('toast');
  toast.textContent = msg;
  toast.className = `toast ${type} show`;
  setTimeout(() => toast.classList.remove('show'), 3000);
}

// ===== NAVBAR SCROLL =====
window.addEventListener('scroll', () => {
  const nav = document.getElementById('navbar');
  if (window.scrollY > 50) nav.style.boxShadow = '0 4px 30px rgba(0,0,0,0.5)';
  else nav.style.boxShadow = 'none';
});




