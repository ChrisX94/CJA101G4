function includeHTML(callback) {
  const z = document.getElementsByTagName("*");
  for (let i = 0; i < z.length; i++) {
    const elmnt = z[i];
    const file = elmnt.getAttribute("w3-include-html");
    if (file) {
      const xhttp = new XMLHttpRequest();
      xhttp.onreadystatechange = function () {
        if (this.readyState == 4) {
          if (this.status == 200) elmnt.innerHTML = this.responseText;
          if (this.status == 404) elmnt.innerHTML = "Page not found.";
          elmnt.removeAttribute("w3-include-html");
          includeHTML(callback); // 遞迴
        }
      };
      xhttp.open("GET", file, true);
      xhttp.send();
      return;
    }
  }
  if (callback) callback(); // ✅ header 載入完，才執行回呼
}

includeHTML(() => {
  // ✅ hamburger 功能
  const hamburgerBtn = document.querySelector(".hamburger");
  const menuBody = document.querySelector(".header__nav");
  if (hamburgerBtn && menuBody) {
    hamburgerBtn.addEventListener("click", () => {
      menuBody.classList.toggle("open");
    });
  }

  // ✅ 登入後替換會員 icon
  fetch('/avatar/userAvatar')
    .then(res => res.json())
    .then(data => {
      if (data && data.userAvatar) {
        // ✅ 桌機版：用大頭貼取代原本的會員 icon
        const iconEl = document.getElementById('userIcon');
        const memberEl = iconEl?.parentNode;
        if (iconEl && memberEl) {
          memberEl.href = "/user/profile";
          memberEl.innerHTML = `<img src="${data.userAvatar}" alt="會員頭像" class="avatar-img">`;


          // ✅ 桌機版：新增下拉箭頭區塊
          const dropdownItem = document.createElement('li');
          dropdownItem.className = "header__item dropdown";
          dropdownItem.innerHTML = `
            <div class="dropdown-toggle">
              <i class="fa-solid fa-chevron-down"></i>
            </div>
            <ul class="dropdown-menu dropdown-menu1">
              <li><a href="/notifications">通知中心</a></li>
              <li><a href="/login/logout">登出</a></li>
            </ul>
          `;
          memberEl.parentNode.insertBefore(dropdownItem, memberEl.nextSibling);
        }


        // ✅ 手機版：設定 .mobile-avatar 的 src
        const mobileAvatar = document.querySelector('.mobile-avatar img');
        if (mobileAvatar) {
          mobileAvatar.src = data.userAvatar;
        }
      }
    })
    .catch(err => {
      console.error("❌ 無法取得會員頭像", err);
    });
	
	
});

