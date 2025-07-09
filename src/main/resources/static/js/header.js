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
          memberEl.href = "/profile";
          
          // 只有在有頭像時才替換圖標
          if (data.userAvatar) {
            memberEl.innerHTML = `<img src="${data.userAvatar}" alt="會員頭像" class="avatar-img">`;
          }

          // ✅ 移除現有的靜態通知鈴鐺（避免重複顯示）
          const existingNotificationBell = document.querySelector('.notification-bell');
          if (existingNotificationBell) {
            existingNotificationBell.remove();
            console.log('已移除靜態通知鈴鐺');
          }

          // ✅ 在頭像左側添加動態通知鈴鐺
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

        // ✅ 初始化通知功能（改進版：即使部分API失敗也要顯示鈴鐺）
        initNotificationSystemImproved();
      } else {
        console.log('用戶未登錄，不顯示通知鈴鐺');
        // 如果用戶未登錄，隱藏靜態通知鈴鐺
        const existingNotificationBell = document.querySelector('.notification-bell');
        if (existingNotificationBell) {
          existingNotificationBell.style.display = 'none';
        }
      }
    })
    .catch(err => {
      console.error("❌ 無法取得會員狀態", err);
      // 發生錯誤時也隱藏通知鈴鐺
      const existingNotificationBell = document.querySelector('.notification-bell');
      if (existingNotificationBell) {
        existingNotificationBell.style.display = 'none';
      }
    });
	
	
});

// 🔔 通知系統功能
let notificationSocket = null;
let currentUserId = null;

// 改進版通知系統初始化（更穩定，即使部分API失敗也要保持鈴鐺顯示）
function initNotificationSystemImproved() {
  console.log('初始化改進版通知系統...');
  
  // 確保通知鈴鐺顯示
  const bell = document.querySelector('.notification-bell');
  if (bell) {
    bell.style.display = 'inline-block';
    console.log('通知鈴鐺已確保顯示');
  }
  
  // 嘗試獲取用戶ID
  getCurrentUserId().then(userId => {
    if (userId) {
      currentUserId = userId;
      console.log('獲取到用戶ID:', userId);
      
      // 嘗試載入未讀通知數量（失敗也不影響鈴鐺顯示）
      window.loadUnreadCount().catch(error => {
        console.warn('載入未讀通知數量失敗，但不影響鈴鐺顯示:', error);
      });
      
      // 設置事件處理器
      setupNotificationBell();
      
      // 嘗試連接WebSocket（失敗也不影響基本功能）
      try {
        connectNotificationWebSocket(userId);
      } catch (error) {
        console.warn('WebSocket連接失敗，但不影響基本通知功能:', error);
      }
    } else {
      console.log('未獲取到用戶ID，但保持鈴鐺顯示');
      // 即使沒有用戶ID，也設置基本的事件處理器
      setupNotificationBell();
    }
  }).catch(error => {
    console.warn('獲取用戶ID失敗，但保持鈴鐺顯示:', error);
    // 即使獲取用戶ID失敗，也設置基本功能
    setupNotificationBell();
  });
}

function initNotificationSystem() {
  console.log('初始化通知系統...');
  
  // 獲取當前用戶ID
  getCurrentUserId().then(userId => {
    if (userId) {
      currentUserId = userId;
      console.log('獲取到用戶ID:', userId);
      
      // 顯示通知鈴鐺
      showNotificationBell();
      
              // 載入未讀通知數量
        window.loadUnreadCount();
      
      // 設置事件處理器
      setupNotificationBell();
      
      // 連接WebSocket
      connectNotificationWebSocket(userId);
    } else {
      console.log('未獲取到用戶ID，隱藏通知鈴鐺');
      hideNotificationBell();
    }
  }).catch(error => {
    console.error('初始化通知系統失敗:', error);
    hideNotificationBell();
  });
}

function showNotificationBell() {
  const bell = document.querySelector('.notification-bell');
  if (bell) {
    bell.style.display = 'inline-block';
    console.log('通知鈴鐺已顯示');
  }
}

function getCurrentUserId() {
  console.log('開始獲取當前用戶ID...');
  return fetch('/user/api/current-id')
    .then(res => {
      console.log('用戶ID API響應狀態:', res.status);
      console.log('用戶ID API響應URL:', res.url);
      
      if (res.status === 302 || res.url.includes('/login')) {
        console.log('用戶未登入，被重定向到登入頁面');
        return null;
      }
      
      if (res.ok) return res.json();
      throw new Error('獲取用戶ID失敗，狀態碼: ' + res.status);
    })
    .then(data => {
      if (data && data.userId) {
        console.log('成功獲取用戶ID:', data.userId);
        return data.userId;
      } else {
        console.log('API響應數據無效:', data);
        return null;
      }
    })
    .catch(err => {
      console.error('獲取用戶ID錯誤:', err);
      return null;
    });
}

// 全局函数，供其他页面调用
window.loadUnreadCount = function loadUnreadCount() {
  return fetch('/notifications/api/unread-count')
    .then(response => {
      console.log('未讀通知API響應狀態:', response.status);
      console.log('未讀通知API響應URL:', response.url);
      
      // 檢查是否被重定向到登入頁面
      if (response.url.includes('/login') || response.status === 302) {
        console.log('用戶未登入，被重定向到登入頁面');
        throw new Error('用戶未登入，被重定向到登入頁面');
      }
      
      if (response.status === 401) {
        // 未登入或登入過期
        console.log('用戶未登入，返回401');
        throw new Error('用戶未登入，返回401');
      }
      if (!response.ok) {
        throw new Error('API 錯誤: ' + response.status);
      }
      return response.json();
    })
    .then(data => {
      if (data) {
        updateNotificationBadge(data.unreadCount);
        return data.unreadCount;
      }
      return 0;
    })
    .catch(error => {
      console.warn('獲取未讀通知數量失敗:', error);
      // 不隱藏通知鈴鐺，只是無法顯示數量
      throw error; // 重新拋出錯誤以便上層處理
    });
}; // window.loadUnreadCount 结束

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

function hideNotificationBell() {
  const bell = document.querySelector('.notification-bell');
  if (bell) {
    bell.style.display = 'none';
    console.log('通知鈴鐺已隱藏');
  }
}

function setupNotificationBell() {
  const bell = document.querySelector('.notification-bell');
  if (!bell) {
    console.warn('找不到通知鈴鐺元素');
    return;
  }

  // 點擊鈴鐺切換下拉選單
  bell.addEventListener('click', function(e) {
    e.preventDefault();
    e.stopPropagation();
    
    const dropdown = bell.querySelector('.notification-dropdown');
    if (!dropdown) {
      console.warn('找不到通知下拉選單元素');
      return;
    }
    
    if (dropdown.classList.contains('show')) {
      dropdown.classList.remove('show');
    } else {
      dropdown.classList.add('show');
      // 嘗試載入通知，失敗時顯示友好訊息
      loadRecentNotifications().catch(error => {
        console.warn('載入通知失敗:', error);
        dropdown.innerHTML = `
          <div class="error">暫時無法載入通知</div>
          <div class="view-all-btn" onclick="window.location.href='/notifications'">查看通知中心</div>
        `;
      });
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
    return Promise.reject(new Error('找不到通知下拉選單元素'));
  }

  console.log('開始載入最近通知...');
  dropdown.innerHTML = '<div class="loading">正在載入通知...<br><small>請稍候片刻</small></div>';

  return fetch('/notifications/api/recent')
    .then(response => {
      console.log('通知API響應狀態:', response.status);
      console.log('通知API響應URL:', response.url);
      
      // 檢查是否被重定向到登入頁面
      if (response.url.includes('/login') || response.status === 302) {
        console.log('用戶未登入，被重定向到登入頁面');
        dropdown.innerHTML = '<div class="error">請先登入以查看通知<br><small>點擊前往登入頁面</small></div>';
        throw new Error('用戶未登入');
      }
      
      if (response.status === 401) {
        // 未登入或登入過期
        dropdown.innerHTML = '<div class="error">登入已過期<br><small>請重新登入後再試</small></div>';
        throw new Error('用戶未登入或登入過期');
      }
      if (!response.ok) {
        throw new Error('API 錯誤: ' + response.status);
      }
      return response.json();
    })
    .then(data => {
      if (data) {
        console.log('通知API數據:', data);
        renderNotificationDropdown(data);
        return data;
      }
      throw new Error('無通知數據');
    })
    .catch(error => {
      console.error('載入通知失敗:', error);
      
      // 根據錯誤類型顯示不同的錯誤訊息
      if (error.message.includes('用戶未登入')) {
        dropdown.innerHTML = '<div class="error">需要登入才能查看通知<br><small>請先登入您的帳戶</small></div>';
      } else if (error.message.includes('網絡')) {
        dropdown.innerHTML = '<div class="error">網絡連接失敗<br><small>請檢查網絡連接後再試</small></div>';
      } else {
        dropdown.innerHTML = '<div class="error">載入通知時發生錯誤<br><small>請稍後再試或聯繫客服</small></div>';
      }
      
      // 即使載入失敗也添加查看更多按鈕
      const viewMoreBtn = document.createElement('div');
      viewMoreBtn.className = 'view-all-btn';
      viewMoreBtn.innerHTML = '前往通知中心';
      viewMoreBtn.addEventListener('click', function() {
        window.location.href = '/notifications';
      });
      dropdown.appendChild(viewMoreBtn);
      throw error; // 重新拋出錯誤
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
    dropdown.innerHTML = '<div class="no-notifications">目前沒有新通知<br><small>我們會在有新消息時通知您</small></div>';
    // 即使沒有通知也添加查看更多按鈕
    const viewMoreBtn = document.createElement('div');
    viewMoreBtn.className = 'view-all-btn';
    viewMoreBtn.textContent = '前往通知中心';
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
    
    // 根據通知類型設置圖標
    const typeIcon = getNotificationTypeIcon(notification);
    const formattedTime = formatNotificationTime(notification.createdAt || notification.sentTime);
    const timeAgo = getTimeAgo(notification.createdAt || notification.sentTime);
    
    item.innerHTML = `
      <div class="notification-title">
        <span class="notification-icon">${typeIcon}</span>
        <span class="title-text">${escapeHtml(truncateText(notification.title || '無標題', 25))}</span>
      </div>
      <div class="notification-content">${escapeHtml(truncateText(notification.content || notification.message || '無內容', 80))}</div>
      <div class="notification-time">
        <span class="time-text">${timeAgo}</span>
        <span class="exact-time">${formattedTime}</span>
      </div>
    `;
    
    // 添加hover效果和點擊事件
    item.addEventListener('click', function(e) {
      e.preventDefault();
      e.stopPropagation();
      
      // 標記為已讀
      if (!notification.isRead) {
        window.markNotificationAsRead(notification.notificationId || notification.id);
      }
      
      // 如果有連結，導向相關頁面
      if (notification.link) {
        window.location.href = notification.link;
      } else {
        // 沒有連結時，直接前往通知中心
        window.location.href = '/notifications';
      }
    });
    
    // 添加滑鼠懸停效果
    item.addEventListener('mouseenter', function() {
      this.style.transform = 'translateX(0.5rem) scale(1.01)';
    });
    
    item.addEventListener('mouseleave', function() {
      this.style.transform = 'translateX(0) scale(1)';
    });
    
    dropdown.appendChild(item);
  });

  // 添加查看更多按鈕
  const viewMoreBtn = document.createElement('div');
  viewMoreBtn.className = 'view-all-btn';
  
  // 根據是否有更多通知來設置按鈕文字
  const totalNotifications = notifications.length;
  const unreadCount = unreadNotifications.length;
  
  if (totalNotifications > 5) {
    viewMoreBtn.innerHTML = `查看全部 ${totalNotifications} 則通知`;
  } else if (unreadCount > 0) {
    viewMoreBtn.innerHTML = `查看通知中心 (${unreadCount} 則未讀)`;
  } else {
    viewMoreBtn.innerHTML = '前往通知中心';
  }
  
  viewMoreBtn.addEventListener('click', function() {
    window.location.href = '/notifications';
  });
  dropdown.appendChild(viewMoreBtn);
  console.log('已添加查看更多按鈕（有通知情況）');
}

// 根據通知類型返回對應的圖標
function getNotificationTypeIcon(notification) {
  const type = notification.type || notification.notificationType || '';
  const title = (notification.title || '').toLowerCase();
  const content = (notification.content || notification.message || '').toLowerCase();
  
  // 根據類型或關鍵字判斷圖標
  if (type.includes('SYSTEM') || title.includes('系統') || content.includes('系統')) {
    return '⚙️';
  } else if (type.includes('MATCH') || title.includes('配對') || content.includes('配對') || title.includes('媒合')) {
    return '💕';
  } else if (type.includes('ACTIVITY') || title.includes('活動') || content.includes('活動')) {
    return '🎯';
  } else if (type.includes('ORDER') || title.includes('訂單') || content.includes('訂單') || title.includes('購買')) {
    return '🛒';
  } else if (type.includes('MESSAGE') || title.includes('訊息') || content.includes('訊息') || title.includes('聊天')) {
    return '💬';
  } else if (type.includes('FRIEND') || title.includes('好友') || content.includes('好友')) {
    return '👥';
  } else if (type.includes('CASE') || title.includes('案件') || content.includes('案件')) {
    return '📋';
  } else if (type.includes('PAYMENT') || title.includes('付款') || content.includes('付款') || title.includes('金額')) {
    return '💰';
  } else if (type.includes('WARNING') || type.includes('ERROR') || title.includes('警告') || title.includes('錯誤')) {
    return '⚠️';
  } else if (type.includes('SUCCESS') || title.includes('成功') || content.includes('成功')) {
    return '✅';
  } else {
    return '📢'; // 預設圖標
  }
}

// 獲取相對時間描述
function getTimeAgo(dateString) {
  if (!dateString) return '時間未知';
  
  const date = new Date(dateString);
  const now = new Date();
  const diffInSeconds = Math.floor((now - date) / 1000);
  
  if (diffInSeconds < 60) {
    return '剛剛';
  } else if (diffInSeconds < 3600) {
    const minutes = Math.floor(diffInSeconds / 60);
    return `${minutes} 分鐘前`;
  } else if (diffInSeconds < 86400) {
    const hours = Math.floor(diffInSeconds / 3600);
    return `${hours} 小時前`;
  } else if (diffInSeconds < 604800) {
    const days = Math.floor(diffInSeconds / 86400);
    return `${days} 天前`;
  } else {
    return formatNotificationTime(dateString);
  }
}

// HTML轉義函數，防止XSS攻擊
function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

// 全局函数，供通知页面调用
window.markNotificationAsRead = function markNotificationAsRead(notificationId) {
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
      window.loadUnreadCount();
      
      // 如果下拉選單是開啟的，重新載入通知列表
      const dropdown = document.querySelector('.notification-dropdown');
      if (dropdown && dropdown.classList.contains('show')) {
        setTimeout(() => {
          loadRecentNotifications();
        }, 500); // 延遲一點載入，確保後端已更新
      }
      
      // 通知其他页面刷新（如通知中心页面）
      if (window.notificationStateChanged) {
        window.notificationStateChanged(notificationId);
      }
      
    } else {
      console.error('標記已讀失敗，狀態碼:', response.status);
    }
  })
  .catch(error => {
    console.error('標記已讀失敗:', error);
  });
}; // window.markNotificationAsRead 结束

function updateUnreadCountWithRetry(retryCount = 0) {
  const maxRetries = 3;
  const retryInterval = 1000; // 1秒間隔
  
  console.log('嘗試更新未讀數量... (第' + (retryCount + 1) + '次)');
  window.loadUnreadCount()
    .then(count => {
      console.log('未讀數量更新成功:', count);
    })
    .catch(error => {
      console.warn('更新未讀數量失敗:', error);
      if (retryCount < maxRetries) {
        console.log(`將在 ${retryInterval}ms 後進行第 ${retryCount + 2} 次重試...`);
        setTimeout(() => {
          updateUnreadCountWithRetry(retryCount + 1);
        }, retryInterval);
      } else {
        console.error('未讀數量更新失敗，已達最大重試次數 (' + maxRetries + ')');
      }
    });
}

function connectNotificationWebSocket(userId) {
  if (notificationSocket) {
    console.log('關閉現有WebSocket連接');
    notificationSocket.close();
  }

  const wsUrl = `ws://${location.host}/notificationSocket/${userId}`;
  console.log('正在連接通知WebSocket:', wsUrl);
  console.log('用戶ID:', userId);

  notificationSocket = new WebSocket(wsUrl);
  
  // 設置為全局變量供測試使用
  window.notificationSocket = notificationSocket;

  notificationSocket.onopen = function() {
    console.log('✅ 通知WebSocket連接成功');
    console.log('WebSocket狀態:', notificationSocket.readyState);
  };

  notificationSocket.onmessage = function(event) {
    console.log('📨 收到新通知:', event.data);
    
    try {
      const notification = JSON.parse(event.data);
      console.log('解析後的通知對象:', notification);
      
      // 觸發鈴鐺動畫
      triggerBellAnimation();
      
      // 立即更新未讀數量（添加重試機制）
      console.log('準備更新未讀數量...');
      updateUnreadCountWithRetry();
      
      // 如果下拉選單是開啟的，重新載入
      const dropdown = document.querySelector('.notification-dropdown');
      if (dropdown && dropdown.classList.contains('show')) {
        console.log('下拉選單開啟中，重新載入通知列表');
        setTimeout(() => {
          loadRecentNotifications();
        }, 200); // 稍微延遲以確保資料庫更新完成
      }
      
      // 顯示浮動通知
      showFloatingNotification(notification);
      
    } catch (error) {
      console.error('❌ 解析通知訊息失敗:', error);
      console.error('原始訊息:', event.data);
    }
  };

  notificationSocket.onclose = function(event) {
    console.log('❌ 通知WebSocket連接關閉');
    console.log('關閉原因:', event.reason);
    console.log('關閉代碼:', event.code);
    
    // 嘗試重新連接
    if (currentUserId && event.code !== 1000) { // 1000 = 正常關閉
      console.log('5秒後嘗試重新連接...');
      setTimeout(() => {
        if (currentUserId) {
          connectNotificationWebSocket(currentUserId);
        }
      }, 5000);
    }
  };

  notificationSocket.onerror = function(error) {
    console.error('❌ 通知WebSocket錯誤:', error);
    console.error('WebSocket狀態:', notificationSocket.readyState);
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

// 測試通知功能 - 全局函數，可在瀏覽器控制台調用
window.testNotificationUpdate = function() {
  console.log('發送測試通知...');
  fetch('/notifications/api/test-notification', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => response.json())
  .then(data => {
    console.log('測試通知響應:', data);
    if (data.success) {
      console.log('✅ 測試通知已發送，請觀察通知鈴鐺數字是否立即更新');
    } else {
      console.log('❌ 測試通知發送失敗:', data.message);
    }
  })
  .catch(error => {
    console.error('❌ 測試通知發送錯誤:', error);
  });
};

// WebSocket連接狀態檢查 - 全局函數
window.checkNotificationWebSocket = function() {
  console.log('=== 通知WebSocket狀態檢查 ===');
  console.log('當前用戶ID:', currentUserId);
  
  if (notificationSocket) {
    console.log('WebSocket實例存在');
    console.log('WebSocket狀態:', getWebSocketStateText(notificationSocket.readyState));
    console.log('WebSocket URL:', notificationSocket.url);
    
    // 測試發送ping消息
    if (notificationSocket.readyState === WebSocket.OPEN) {
      console.log('WebSocket連接正常，可以接收通知');
      return true;
    } else {
      console.log('WebSocket連接異常，可能影響通知數字更新');
      return false;
    }
  } else {
    console.log('WebSocket實例不存在');
    return false;
  }
};

function getWebSocketStateText(state) {
  switch(state) {
    case WebSocket.CONNECTING: return 'CONNECTING (0)';
    case WebSocket.OPEN: return 'OPEN (1)';
    case WebSocket.CLOSING: return 'CLOSING (2)';
    case WebSocket.CLOSED: return 'CLOSED (3)';
    default: return 'UNKNOWN (' + state + ')';
  }
}

