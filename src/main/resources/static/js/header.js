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

  // ✅ 登入後替換會員 icon 並添加通知鈴鐺
  fetch('/avatar/userAvatar')
    .then(res => res.json())
    .then(data => {
              console.log('用戶狀態:', data);
        console.log('登錄狀態:', data?.login);
        console.log('用戶頭像:', data?.userAvatar);
        
        // 檢查用戶是否已登錄（根據login字段判斷）
        if (data && data.login === true) {
          console.log('用戶已登錄，開始設置通知鈴鐺...');
        // ✅ 桌機版：用大頭貼取代原本的會員 icon（僅在有頭像時）
        const iconEl = document.getElementById('userIcon');
        const memberEl = iconEl?.parentNode;
        if (iconEl && memberEl) {
          memberEl.href = "/user/profile";
          memberEl.innerHTML = `<img src="${data.userAvatar}" alt="會員頭像" class="avatar-img">`;

          // ✅ 在頭像左側添加通知鈴鐺
          const notificationBell = document.createElement('a');
          notificationBell.href = "#";
          notificationBell.className = "notification-bell";
          notificationBell.innerHTML = `
            <i class="fa-solid fa-bell"></i>
            <div class="notification-dropdown">
              <div class="loading">載入中...</div>
            </div>
          `;
          memberEl.parentNode.insertBefore(notificationBell, memberEl);

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

        // ✅ 初始化通知功能
        initNotificationSystem();
      } else {
        console.log('用戶未登錄，不顯示通知鈴鐺');
      }
    })
    .catch(err => {
      console.error("❌ 無法取得會員狀態", err);
    });
	
	
});

// 🔔 通知系統功能
let notificationSocket = null;
let currentUserId = null;

function initNotificationSystem() {
  console.log('初始化通知系統...');
  
  // 獲取當前用戶ID
  getCurrentUserId().then(userId => {
    if (userId) {
      currentUserId = userId;
      loadUnreadCount();
      setupNotificationBell();
      connectNotificationWebSocket(userId);
    }
  });
}

function getCurrentUserId() {
  return fetch('/api/user/current-id')
    .then(res => {
      if (res.ok) return res.json();
      throw new Error('獲取用戶ID失敗');
    })
    .then(data => data.userId)
    .catch(err => {
      console.error('獲取用戶ID錯誤:', err);
      return null;
    });
}

function loadUnreadCount() {
  fetch('/notifications/api/unread-count')
    .then(response => response.json())
    .then(data => {
      updateNotificationBadge(data.unreadCount);
    })
    .catch(error => console.error('獲取未讀通知數量失敗:', error));
}

function updateNotificationBadge(count) {
  const bell = document.querySelector('.notification-bell');
  if (!bell) return;

  // 移除現有徽章
  const existingBadge = bell.querySelector('.notification-badge');
  if (existingBadge) {
    existingBadge.remove();
  }

  // 如果有未讀通知，添加紅點徽章
  if (count > 0) {
    const badge = document.createElement('span');
    badge.className = 'notification-badge';
    badge.textContent = count > 99 ? '99+' : count;
    bell.appendChild(badge);
  }
}

function setupNotificationBell() {
  const bell = document.querySelector('.notification-bell');
  if (!bell) return;

  // 點擊鈴鐺切換下拉選單
  bell.addEventListener('click', function(e) {
    e.preventDefault();
    e.stopPropagation();
    
    const dropdown = bell.querySelector('.notification-dropdown');
    if (dropdown.classList.contains('show')) {
      dropdown.classList.remove('show');
    } else {
      dropdown.classList.add('show');
      loadRecentNotifications();
    }
  });

  // 點擊其他地方關閉下拉選單
  document.addEventListener('click', function(e) {
    const dropdown = document.querySelector('.notification-dropdown');
    if (dropdown && dropdown.classList.contains('show') && !bell.contains(e.target)) {
      dropdown.classList.remove('show');
    }
  });
}

function loadRecentNotifications() {
  const dropdown = document.querySelector('.notification-dropdown');
  if (!dropdown) {
    console.warn('找不到notification-dropdown元素');
    return;
  }

  console.log('開始載入最近通知...');
  dropdown.innerHTML = '<div class="loading">載入中...</div>';

  fetch('/notifications/api/recent')
    .then(response => {
      console.log('通知API響應狀態:', response.status);
      return response.json();
    })
    .then(data => {
      console.log('通知API數據:', data);
      renderNotificationDropdown(data);
    })
    .catch(error => {
      console.error('載入通知失敗:', error);
      dropdown.innerHTML = '<div class="error">載入通知失敗</div>';
      // 即使載入失敗也添加查看更多按鈕
      const viewMoreBtn = document.createElement('div');
      viewMoreBtn.className = 'view-all-btn';
      viewMoreBtn.textContent = '查看更多';
      viewMoreBtn.addEventListener('click', function() {
        window.location.href = '/notifications';
      });
      dropdown.appendChild(viewMoreBtn);
    });
}

function renderNotificationDropdown(notifications) {
  console.log('開始渲染通知下拉選單，通知數量:', notifications ? notifications.length : 0);
  const dropdown = document.querySelector('.notification-dropdown');
  if (!dropdown) {
    console.warn('找不到notification-dropdown元素');
    return;
  }

  if (!notifications || notifications.length === 0) {
    console.log('沒有通知數據，顯示預設訊息');
    dropdown.innerHTML = '<div class="no-notifications">沒有新通知</div>';
    // 即使沒有通知也添加查看更多按鈕
    const viewMoreBtn = document.createElement('div');
    viewMoreBtn.className = 'view-all-btn';
    viewMoreBtn.textContent = '查看更多';
    viewMoreBtn.addEventListener('click', function() {
      window.location.href = '/notifications';
    });
    dropdown.appendChild(viewMoreBtn);
    console.log('已添加查看更多按鈕（無通知情況）');
    return;
  }

  dropdown.innerHTML = '';

  // 優先顯示未讀通知，最多5個
  const unreadNotifications = notifications.filter(n => !n.isRead);
  const readNotifications = notifications.filter(n => n.isRead);
  
  // 取最多5個未讀通知，如果未讀不足5個，用已讀通知補足
  const displayNotifications = [...unreadNotifications.slice(0, 5)];
  if (displayNotifications.length < 5) {
    displayNotifications.push(...readNotifications.slice(0, 5 - displayNotifications.length));
  }

  displayNotifications.forEach(notification => {
    const item = document.createElement('div');
    item.className = `notification-item ${notification.isRead ? '' : 'unread'}`;
    item.dataset.id = notification.notificationId || notification.id;
    
    item.innerHTML = `
      <div class="notification-title">${truncateText(notification.title || '無標題', 25)}</div>
      <div class="notification-content">${truncateText(notification.content || notification.message || '無內容', 30)}</div>
      <div class="notification-time">${formatNotificationTime(notification.createdAt || notification.sentTime)}</div>
    `;
    
    item.addEventListener('click', function() {
      markNotificationAsRead(notification.notificationId || notification.id);
      // 如果有連結，導向相關頁面
      if (notification.link) {
        window.location.href = notification.link;
      }
    });
    
    dropdown.appendChild(item);
  });

  // 添加查看更多按鈕
  const viewMoreBtn = document.createElement('div');
  viewMoreBtn.className = 'view-all-btn';
  viewMoreBtn.textContent = '查看更多';
  viewMoreBtn.addEventListener('click', function() {
    window.location.href = '/notifications';
  });
  dropdown.appendChild(viewMoreBtn);
  console.log('已添加查看更多按鈕（有通知情況）');
}

function markNotificationAsRead(notificationId) {
  console.log('標記通知為已讀:', notificationId);
  
  fetch(`/notifications/api/${notificationId}/mark-read`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    console.log('標記已讀響應狀態:', response.status);
    if (response.ok) {
      // 更新UI
      const item = document.querySelector(`.notification-item[data-id="${notificationId}"]`);
      if (item) {
        console.log('移除未讀標記');
        item.classList.remove('unread');
      }
      // 重新載入未讀數量
      console.log('重新載入未讀數量');
      loadUnreadCount();
      
      // 如果下拉選單是開啟的，重新載入通知列表
      const dropdown = document.querySelector('.notification-dropdown');
      if (dropdown && dropdown.classList.contains('show')) {
        setTimeout(() => {
          loadRecentNotifications();
        }, 500); // 延遲一點載入，確保後端已更新
      }
    } else {
      console.error('標記已讀失敗，狀態碼:', response.status);
    }
  })
  .catch(error => {
    console.error('標記已讀失敗:', error);
  });
}

function connectNotificationWebSocket(userId) {
  if (notificationSocket) {
    notificationSocket.close();
  }

  notificationSocket = new WebSocket(`ws://${location.host}/notificationSocket/${userId}`);

  notificationSocket.onopen = function() {
    console.log('通知WebSocket連接成功');
  };

  notificationSocket.onmessage = function(event) {
    console.log('收到新通知:', event.data);
    
    try {
      const notification = JSON.parse(event.data);
      
      // 觸發鈴鐺動畫
      triggerBellAnimation();
      
      // 更新未讀數量
      loadUnreadCount();
      
      // 如果下拉選單是開啟的，重新載入
      const dropdown = document.querySelector('.notification-dropdown');
      if (dropdown && dropdown.classList.contains('show')) {
        loadRecentNotifications();
      }
      
      // 顯示浮動通知
      showFloatingNotification(notification);
      
    } catch (error) {
      console.error('解析通知訊息失敗:', error);
    }
  };

  notificationSocket.onclose = function() {
    console.log('通知WebSocket連接關閉');
    // 嘗試重新連接
    setTimeout(() => {
      if (currentUserId) {
        connectNotificationWebSocket(currentUserId);
      }
    }, 5000);
  };

  notificationSocket.onerror = function(error) {
    console.error('通知WebSocket錯誤:', error);
  };
}

function triggerBellAnimation() {
  const bell = document.querySelector('.notification-bell i');
  if (bell) {
    bell.classList.add('shake');
    setTimeout(() => {
      bell.classList.remove('shake');
    }, 1000);
  }
}

function showFloatingNotification(notification) {
  // 創建浮動通知
  const floatingNotif = document.createElement('div');
  floatingNotif.className = 'floating-notification';
  floatingNotif.innerHTML = `
    <div class="floating-notification-content">
      <div class="floating-notification-title">${notification.title}</div>
      <div class="floating-notification-text">${notification.content}</div>
    </div>
    <button class="floating-notification-close">&times;</button>
  `;

  document.body.appendChild(floatingNotif);

  // 關閉按鈕事件
  floatingNotif.querySelector('.floating-notification-close').addEventListener('click', function() {
    floatingNotif.remove();
  });

  // 3秒後自動消失
  setTimeout(() => {
    if (floatingNotif.parentNode) {
      floatingNotif.remove();
    }
  }, 5000);

  // 點擊通知體可跳轉
  floatingNotif.addEventListener('click', function() {
    if (notification.link) {
      window.location.href = notification.link;
    }
    floatingNotif.remove();
  });
}

function truncateText(text, maxLength) {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
}

function formatNotificationTime(dateString) {
  if (!dateString) {
    return '時間未知';
  }
  
  const date = new Date(dateString);
  
  // 檢查日期是否有效
  if (isNaN(date.getTime())) {
    console.warn('無效的日期格式:', dateString);
    return '時間未知';
  }
  
  const now = new Date();
  const diff = now - date;
  
  // 小於一天，顯示相對時間
  if (diff < 86400000 && diff > 0) {
    const hours = Math.floor(diff / 3600000);
    if (hours > 0) {
      return `${hours}小時前`;
    }
    
    const minutes = Math.floor(diff / 60000);
    if (minutes > 0) {
      return `${minutes}分鐘前`;
    }
    
    return '剛剛';
  }
  
  // 大於一天，顯示日期
  try {
    return date.toLocaleDateString('zh-TW');
  } catch (error) {
    console.error('日期格式化錯誤:', error);
    return '時間未知';
  }
}

