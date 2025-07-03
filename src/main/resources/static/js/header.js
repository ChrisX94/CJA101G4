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
  // ✅ header 載入完，這時候 hamburger 才真的存在
  const hamburgerBtn = document.querySelector(".hamburger");
  const menuBody = document.querySelector(".header__nav");
  if (hamburgerBtn && menuBody) {
    hamburgerBtn.addEventListener("click", () => {
      menuBody.classList.toggle("open");
    });
  }
  
  // ✅ 檢查登入狀態並換頭像
  fetch('/avatar/userAvatar')
    .then(res => res.json())
    .then(data => {
      if (data && data.userAvatar) {
        const iconEl = document.getElementById('userIcon');
        if (iconEl) {
          const avatarImg = document.createElement('img');
          avatarImg.src = data.userAvatar;
          avatarImg.alt = '會員頭像';
          avatarImg.className = 'avatar-img';

          // 用頭像圖片取代原本的 icon
          iconEl.parentNode.replaceChild(avatarImg, iconEl);
        }
      }
    })
    .catch(err => {
      console.error("❌ 無法取得會員頭像", err);
    });

});
