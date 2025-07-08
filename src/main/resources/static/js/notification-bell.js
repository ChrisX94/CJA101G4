// 通知鈴鐺相關功能
document.addEventListener('DOMContentLoaded', function() {
    initNotificationBell();
    setupNotificationSendConfirmation();
});

// 初始化通知鈴鐺
function initNotificationBell() {
    const notificationBell = document.querySelector('.notification-bell');
    if (!notificationBell) return;

    // 獲取未讀通知數量
    fetch('/api/notifications/unread-count')
        .then(response => response.json())
        .then(data => {
            if (data.count > 0) {
                const badge = document.createElement('span');
                badge.className = 'notification-badge';
                badge.textContent = data.count > 99 ? '99+' : data.count;
                notificationBell.appendChild(badge);
            }
        })
        .catch(error => console.error('獲取未讀通知數量失敗:', error));

    // 點擊鈴鐺顯示通知列表
    notificationBell.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        
        const dropdown = document.querySelector('.notification-dropdown');
        if (dropdown) {
            dropdown.classList.toggle('show');
            if (dropdown.classList.contains('show')) {
                loadNotifications();
            }
        }
    });

    // 點擊其他區域關閉通知列表
    document.addEventListener('click', function(e) {
        const dropdown = document.querySelector('.notification-dropdown');
        if (dropdown && dropdown.classList.contains('show') && !dropdown.contains(e.target)) {
            dropdown.classList.remove('show');
        }
    });
}

// 載入通知列表
    function loadNotifications() {
    const dropdown = document.querySelector('.notification-dropdown');
    if (!dropdown) return;

    dropdown.innerHTML = '<div class="loading">載入中...</div>';

    fetch('/api/notifications/recent')
        .then(response => response.json())
        .then(data => {
            dropdown.innerHTML = '';
            
            if (data.length === 0) {
                dropdown.innerHTML = '<div class="no-notifications">沒有新通知</div>';
                    return;
                }

            data.forEach(notification => {
                const item = document.createElement('div');
                item.className = `notification-item ${notification.isRead ? '' : 'unread'}`;
                item.dataset.id = notification.id;
                
                const title = document.createElement('div');
                title.className = 'notification-title';
                title.textContent = notification.title;
                
                const content = document.createElement('div');
                content.className = 'notification-content';
                content.textContent = notification.content;
                
                const time = document.createElement('div');
                time.className = 'notification-time';
                time.textContent = formatDate(notification.createdAt);
                
                item.appendChild(title);
                item.appendChild(content);
                item.appendChild(time);
                
                item.addEventListener('click', function() {
                    markAsRead(notification.id);
                    if (notification.link) {
                        window.location.href = notification.link;
                    }
                });
                
                dropdown.appendChild(item);
            });
            
            // 添加查看全部按鈕
            const viewAll = document.createElement('div');
            viewAll.className = 'view-all-notifications';
            viewAll.textContent = '查看全部';
            viewAll.addEventListener('click', function() {
                window.location.href = '/notifications';
            });
            dropdown.appendChild(viewAll);
        })
        .catch(error => {
            console.error('載入通知失敗:', error);
            dropdown.innerHTML = '<div class="error">載入通知失敗</div>';
        });
}

// 標記通知為已讀
function markAsRead(notificationId) {
    fetch(`/api/notifications/${notificationId}/read`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            const item = document.querySelector(`.notification-item[data-id="${notificationId}"]`);
            if (item) {
                item.classList.remove('unread');
            }
            
            // 更新未讀數量
            updateUnreadCount();
        }
    })
    .catch(error => console.error('標記已讀失敗:', error));
}

// 更新未讀數量
function updateUnreadCount() {
    fetch('/api/notifications/unread-count')
        .then(response => response.json())
        .then(data => {
            const badge = document.querySelector('.notification-badge');
            if (badge) {
                if (data.count > 0) {
                    badge.textContent = data.count > 99 ? '99+' : data.count;
                } else {
                    badge.remove();
                }
            }
        })
        .catch(error => console.error('更新未讀數量失敗:', error));
}

// 日期格式化
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    
    // 小於一天，顯示相對時間
    if (diff < 86400000) {
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
    return date.toLocaleDateString('zh-TW');
}

// 設置通知發送確認對話框
function setupNotificationSendConfirmation() {
    // 監聽發送按鈕點擊事件
    const sendButtons = document.querySelectorAll('.notification-send-btn');
    if (!sendButtons.length) return;
    
    sendButtons.forEach(button => {
        button.addEventListener('click', function(e) {
        e.preventDefault();
        
            const notificationId = this.dataset.id;
        if (!notificationId) return;

            // 獲取通知預覽信息
            fetch(`/api/admin/notifications/${notificationId}/preview`)
                .then(response => response.json())
                .then(data => {
                    showEnhancedConfirmDialog(data, notificationId);
                })
                .catch(error => {
                    console.error('獲取通知預覽失敗:', error);
                    alert('獲取通知預覽失敗，請稍後再試');
                });
            });
    });
}

// 顯示增強版確認對話框
function showEnhancedConfirmDialog(previewData, notificationId) {
    // 創建對話框元素
    const dialogOverlay = document.createElement('div');
    dialogOverlay.className = 'dialog-overlay';
    
    const dialog = document.createElement('div');
    dialog.className = 'enhanced-confirm-dialog';
    
    // 標題
    const title = document.createElement('h3');
    title.textContent = '確認發送通知';
    dialog.appendChild(title);
    
    // 通知詳情
    const details = document.createElement('div');
    details.className = 'notification-preview';
    
    // 通知標題
    const notificationTitle = document.createElement('div');
    notificationTitle.className = 'preview-item';
    notificationTitle.innerHTML = `<strong>標題：</strong> ${previewData.title}`;
    details.appendChild(notificationTitle);
    
    // 通知內容
    const notificationContent = document.createElement('div');
    notificationContent.className = 'preview-item';
    notificationContent.innerHTML = `<strong>內容：</strong> ${previewData.content}`;
    details.appendChild(notificationContent);
    
    // 通知類型
    const notificationType = document.createElement('div');
    notificationType.className = 'preview-item';
    notificationType.innerHTML = `<strong>類型：</strong> ${previewData.type}`;
    details.appendChild(notificationType);
    
    // 目標類型
    const targetType = document.createElement('div');
    targetType.className = 'preview-item';
    let targetTypeText = '未知';
    switch (previewData.targetType) {
        case 'ALL':
            targetTypeText = '全體用戶';
            break;
        case 'SPECIFIC':
            targetTypeText = '指定用戶';
            break;
        case 'TAG':
            targetTypeText = '標籤篩選';
            break;
    }
    targetType.innerHTML = `<strong>發送範圍：</strong> ${targetTypeText}`;
    details.appendChild(targetType);
    
    // 預估接收人數
    const estimatedRecipients = document.createElement('div');
    estimatedRecipients.className = 'preview-item highlight';
    estimatedRecipients.innerHTML = `<strong>預估接收人數：</strong> ${previewData.estimatedRecipients || 0} 人`;
    details.appendChild(estimatedRecipients);
    
    // 排程發送時間
    if (previewData.scheduledTime) {
        const scheduledTime = document.createElement('div');
        scheduledTime.className = 'preview-item';
        scheduledTime.innerHTML = `<strong>排程發送時間：</strong> ${new Date(previewData.scheduledTime).toLocaleString('zh-TW')}`;
        details.appendChild(scheduledTime);
    }
    
    dialog.appendChild(details);
    
    // 警告訊息
    const warning = document.createElement('div');
    warning.className = 'warning-message';
    warning.textContent = '注意：發送後無法撤回，請確認以上內容無誤。';
    dialog.appendChild(warning);
    
    // 按鈕區域
    const buttons = document.createElement('div');
    buttons.className = 'dialog-buttons';
    
    // 取消按鈕
    const cancelButton = document.createElement('button');
    cancelButton.className = 'btn btn-secondary';
    cancelButton.textContent = '取消';
    cancelButton.addEventListener('click', function() {
        document.body.removeChild(dialogOverlay);
    });
    buttons.appendChild(cancelButton);
    
    // 確認按鈕
    const confirmButton = document.createElement('button');
    confirmButton.className = 'btn btn-primary';
    confirmButton.textContent = '確認發送';
    confirmButton.addEventListener('click', function() {
        // 發送通知
        sendNotification(notificationId, dialogOverlay);
    });
    buttons.appendChild(confirmButton);
    
    dialog.appendChild(buttons);
    dialogOverlay.appendChild(dialog);
    document.body.appendChild(dialogOverlay);
}

// 發送通知
function sendNotification(notificationId, dialogOverlay) {
    // 顯示發送中狀態
    const dialog = dialogOverlay.querySelector('.enhanced-confirm-dialog');
    dialog.classList.add('sending');
    dialog.innerHTML = '<div class="loading">發送中，請稍候...</div>';
    
    fetch(`/api/admin/notifications/${notificationId}/send`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            dialog.innerHTML = '<div class="success">通知發送成功！</div>';
            setTimeout(() => {
                document.body.removeChild(dialogOverlay);
                // 重新載入頁面或更新通知列表
                window.location.reload();
            }, 1500);
        } else {
            throw new Error('發送失敗');
        }
    })
    .catch(error => {
        console.error('發送通知失敗:', error);
        dialog.innerHTML = '<div class="error">發送失敗，請稍後再試</div>';
        setTimeout(() => {
            document.body.removeChild(dialogOverlay);
        }, 1500);
    });
} 