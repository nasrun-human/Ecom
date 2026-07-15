// ===== AUTH =====
function openAuthModal() {
  document.getElementById("authOverlay").classList.add("open");
  document.getElementById("authModal").classList.add("open");
}
function closeAuthModal() {
  document.getElementById("authOverlay").classList.remove("open");
  document.getElementById("authModal").classList.remove("open");
}
function toggleAuthMode(mode) {
  const isLogin = mode === "login";
  document.getElementById("loginForm").style.display = isLogin ? "block" : "none";
  document.getElementById("registerForm").style.display = !isLogin ? "block" : "none";
  document.getElementById("authTitle").textContent = isLogin ? "เข้าสู่ระบบ" : "สมัครสมาชิก";
}
function submitLogin() {
  const u = document.getElementById("loginUsername").value;
  const p = document.getElementById("loginPassword").value;
  if(!u || !p) return showToast("กรุณากรอกข้อมูลให้ครบถ้วน", "error");
  
  const token = btoa(u + ":" + p);
  fetch("/api/auth/me", {
    headers: { "Authorization": "Basic " + token }
  }).then(r => r.json()).then(data => {
    if(data.id) {
      localStorage.setItem("shopverse_auth", token);
      localStorage.setItem("shopverse_user", JSON.stringify(data));
      showToast("เข้าสู่ระบบสำเร็จ!", "success");
      closeAuthModal();
      checkAuth();
    } else {
      showToast("รหัสผ่านไม่ถูกต้อง หรือไม่พบชื่อผู้ใช้", "error");
    }
  }).catch(() => showToast("เกิดข้อผิดพลาดในการเชื่อมต่อเซิร์ฟเวอร์", "error"));
}
async function submitRegister() {
  const body = {
    username: document.getElementById("regUsername").value,
    email: document.getElementById("regEmail").value,
    password: document.getElementById("regPassword").value,
    fullName: document.getElementById("regFullName").value
  };
  
  // Client-side validation to prevent obvious errors before hitting backend
  if(!body.username || body.username.length < 3) return showToast("ชื่อผู้ใช้ต้องมีอย่างน้อย 3 ตัวอักษร", "error");
  if(!body.email || !body.email.includes("@")) return showToast("อีเมลไม่ถูกต้อง", "error");
  if(!body.password || body.password.length < 6) return showToast("รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร", "error");
  
  try {
    const res = await fetch("/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });
    
    // Parse JSON safely
    let data;
    try { data = await res.json(); } catch(e) { data = {}; }
    
    if(res.ok && data.id) {
      showToast("สมัครสมาชิกสำเร็จ! กรุณาเข้าสู่ระบบ", "success");
      toggleAuthMode("login");
      document.getElementById("loginUsername").value = body.username;
      document.getElementById("loginPassword").value = body.password;
    } else {
      // Backend error extraction
      let errorMsg = "เกิดข้อผิดพลาดในการสมัครสมาชิก";
      if (data.errors && data.errors.length > 0) {
        errorMsg = data.errors.map(e => e.defaultMessage).join(", ");
      } else if (data.message) {
        errorMsg = data.message;
      }
      showToast(errorMsg, "error");
    }
  } catch(e) {
    showToast("ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้", "error");
  }
}
function checkAuth() {
  const userStr = localStorage.getItem("shopverse_user");
  const btnLogin = document.getElementById("btnLogin");
  const userInfo = document.getElementById("userInfo");
  
  if(userStr && btnLogin && userInfo) {
    try {
      const user = JSON.parse(userStr);
      btnLogin.style.display = "none";
      userInfo.style.display = "flex";
      document.getElementById("userName").textContent = user.username;
      document.getElementById("userAvatar").textContent = user.username.charAt(0).toUpperCase();
    } catch(e) {
      localStorage.removeItem("shopverse_user");
    }
  } else if (btnLogin && userInfo) {
    btnLogin.style.display = "block";
    userInfo.style.display = "none";
  }
}
function toggleProfileMenu() {
  if(confirm("คุณต้องการออกจากระบบใช่หรือไม่?")) {
    localStorage.removeItem("shopverse_auth");
    localStorage.removeItem("shopverse_user");
    checkAuth();
    showToast("ออกจากระบบเรียบร้อย", "success");
  }
}
document.addEventListener("DOMContentLoaded", checkAuth);
