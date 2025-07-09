$(document).ready(function() {
    // 通知類別的中文對應 - 修正為資料庫實際使用的中文名稱
    const categoryNames = {
        '訂單狀態': '訂單狀態',
        '促銷活動': '促銷活動',
        '更新公告': '更新公告',
        '安全提醒': '安全提醒',
        '新功能介紹': '新功能介紹',
        '政策更新': '政策更新',
        '系統通知': '系統通知',
        '配對通知': '配對通知',
        '訊息通知': '訊息通知',
        '會員專享': '會員專享',
        '評價提醒': '評價提醒',
        '付款提醒': '付款提醒'
    };

    // 載入通知偏好設定
    loadPreferences();

    // 勿擾時段開關事件
    $('#dnd-enabled').change(function() {
        if ($(this).is(':checked')) {
            $('#dnd-time-range').show();
        } else {
            $('#dnd-time-range').hide();
        }
    });

    // 表單提交事件
    $('#preferences-form').submit(function(e) {
        e.preventDefault();
        savePreferences();
    });

    function loadPreferences() {
        $.ajax({
            url: '/notifications/api/preferences',
            method: 'GET',
            success: function(data) {
                console.log('載入的偏好設定:', data);
                displayPreferences(data);
            },
            error: function(xhr, status, error) {
                console.error('載入偏好設定失敗:', error);
                if (xhr.status === 401) {
                    Swal.fire({
                        icon: 'error',
                        title: '認證失敗',
                        text: '請重新登入',
                        confirmButtonText: '確定'
                    }).then(() => {
                        window.location.href = '/login';
                    });
                } else {
                    showErrorMessage('載入偏好設定失敗，請稍後再試');
                }
            }
        });
    }

    function displayPreferences(preferences) {
        const container = $('#preferences-container');
        
        // 🎨 檢測當前頁面類型（前端或後端）
        const isBackend = window.location.pathname.includes('/back-end/') || 
                         document.title.includes('後台管理') ||
                         $('.admin-card').length > 0;
        
        // 🎨 處理空狀態
        if (!preferences || preferences.length === 0) {
            const emptyStateHtml = isBackend ? `
                <div class="preferences-section">
                    <div class="empty-state">
                        <span class="empty-state-icon">🔔</span>
                        <h3>尚未設定通知偏好</h3>
                        <p>系統將使用預設設定為您提供通知服務</p>
                        <button type="button" class="btn-admin btn-info-admin" onclick="createDefaultPreferences()">
                            <i class="fas fa-magic"></i> 建立預設通知設定
                        </button>
                    </div>
                </div>
            ` : `
                <div class="preferences-section">
                    <div style="text-align: center; padding: 4rem 2rem; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 1.5rem; border: 2px dashed #dee2e6;">
                        <div style="font-size: 4rem; margin-bottom: 1.5rem;">🔔</div>
                        <h3 style="color: #6c757d; margin-bottom: 1rem; font-size: 2rem;">尚未設定通知偏好</h3>
                        <p style="color: #6c757d; margin-bottom: 2rem; font-size: 1.4rem;">系統將使用預設設定為您提供通知服務</p>
                        <button type="button" class="save-button info-button" onclick="createDefaultPreferences()" style="margin: 0;">
                            <i class="fas fa-magic"></i> 建立預設通知設定
                        </button>
                    </div>
                </div>
            `;
            container.html(emptyStateHtml);
            return;
        }
        
        let html = '';
        
        // 🎨 通知類別設定區段
        if (isBackend) {
            html += `
                <div class="preferences-section">
                    <h2 class="section-title" data-section="categories">通知類別設定</h2>
                    <div class="section-description">為每個通知類別分別設定您的接收偏好，讓您只收到真正重要的通知</div>
            `;
        } else {
            html += `
                <div class="preferences-section">
                    <h2 data-icon="🔔">通知類別設定</h2>
                    <div class="section-description">為每個通知類別分別設定您的接收偏好，讓您只收到真正重要的通知</div>
            `;
        }
        
        preferences.forEach(function(pref) {
            const categoryDisplayName = categoryNames[pref.notificationCategory] || pref.notificationCategory;
            html += `
                <div class="category-preference-item category-row" data-category="${pref.notificationCategory}" data-preference-id="${pref.preferenceId}">
                    <div class="category-header">
                        <h4>${categoryDisplayName}</h4>
                        <p class="category-description">${getCategoryDescription(pref.notificationCategory)}</p>
                    </div>
                    
                    <div class="notification-methods">
                        <div class="method-row">
                            <div class="method-info">
                                <strong data-method="email">電子郵件通知</strong>
                                <span class="method-desc">接收重要更新到您的信箱</span>
                            </div>
                            ${isBackend ? `
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="emailEnabled_${pref.preferenceId}" data-category="${pref.notificationCategory}" ${pref.emailEnabled ? 'checked' : ''}>
                                </div>
                            ` : `
                                <label class="toggle-switch">
                                    <input type="checkbox" name="emailEnabled_${pref.preferenceId}" data-category="${pref.notificationCategory}" ${pref.emailEnabled ? 'checked' : ''}>
                                    <span class="toggle-slider"></span>
                                </label>
                            `}
                        </div>
                        
                        <div class="method-row">
                            <div class="method-info">
                                <strong data-method="inapp">站內通知</strong>
                                <span class="method-desc">在網站內即時接收通知</span>
                            </div>
                            ${isBackend ? `
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="inAppEnabled_${pref.preferenceId}" data-category="${pref.notificationCategory}" ${pref.inAppEnabled ? 'checked' : ''}>
                                </div>
                            ` : `
                                <label class="toggle-switch">
                                    <input type="checkbox" name="inAppEnabled_${pref.preferenceId}" data-category="${pref.notificationCategory}" ${pref.inAppEnabled ? 'checked' : ''}>
                                    <span class="toggle-slider"></span>
                                </label>
                            `}
                        </div>
                        
                        <div class="method-row">
                            <div class="method-info">
                                <strong data-method="push">APP推播通知</strong>
                                <span class="method-desc">手機APP推播提醒</span>
                            </div>
                            ${isBackend ? `
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="pushEnabled_${pref.preferenceId}" data-category="${pref.notificationCategory}" ${pref.pushEnabled ? 'checked' : ''}>
                                </div>
                            ` : `
                                <label class="toggle-switch">
                                    <input type="checkbox" name="pushEnabled_${pref.preferenceId}" data-category="${pref.notificationCategory}" ${pref.pushEnabled ? 'checked' : ''}>
                                    <span class="toggle-slider"></span>
                                </label>
                            `}
                        </div>
                        
                        <div class="method-row">
                            <div class="method-info">
                                <strong data-method="sms">簡訊通知</strong>
                                <span class="method-desc">重要提醒發送到手機</span>
                            </div>
                            ${isBackend ? `
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="smsEnabled_${pref.preferenceId}" data-category="${pref.notificationCategory}" ${pref.smsEnabled ? 'checked' : ''}>
                                </div>
                            ` : `
                                <label class="toggle-switch">
                                    <input type="checkbox" name="smsEnabled_${pref.preferenceId}" data-category="${pref.notificationCategory}" ${pref.smsEnabled ? 'checked' : ''}>
                                    <span class="toggle-slider"></span>
                                </label>
                            `}
                        </div>
                    </div>
                </div>
            `;
        });
        
        html += `</div>`;
        
        // 🎨 勿擾時段設定區段
        const firstPref = preferences[0];
        if (isBackend) {
            html += `
                <div class="preferences-section">
                    <h2 class="section-title" data-section="quiet">勿擾時段設定</h2>
                    <div class="section-description">在指定時段內暫停推送通知，讓您享受不被打擾的休息時光</div>
                    
                    <div class="quiet-hours-card">
                        <div class="quiet-hours-header">
                            <h3 class="quiet-hours-title">啟用勿擾時段</h3>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="quietHoursEnabled" ${firstPref.quietHoursEnabled ? 'checked' : ''}>
                            </div>
                        </div>
                        <div class="quiet-hours-description">
                            在指定時段內暫停推送通知，避免干擾休息
                        </div>
                        
                        <div class="time-range-container" ${!firstPref.quietHoursEnabled ? 'style="display: none;"' : ''}>
                            <div class="time-input-group">
                                <label for="quietHoursStart">開始時間</label>
                                <input type="time" class="quiet-hours-start" name="quietHoursStart" 
                                       value="${firstPref.quietHoursStart || '22:00'}">
                            </div>
                            <div class="time-input-group">
                                <label for="quietHoursEnd">結束時間</label>
                                <input type="time" class="quiet-hours-end" name="quietHoursEnd" 
                                       value="${firstPref.quietHoursEnd || '08:00'}">
                            </div>
                        </div>
                    </div>
                </div>
            `;
        } else {
            html += `
                <div class="preferences-section">
                    <h2 data-icon="🌙">勿擾時段設定</h2>
                    <div class="section-description">在指定時段內暫停推送通知，讓您享受不被打擾的休息時光</div>
                    
                    <div class="preference-item">
                        <div class="preference-title">
                            <span>啟用勿擾時段</span>
                            <label class="toggle-switch">
                                <input type="checkbox" name="quietHoursEnabled" ${firstPref.quietHoursEnabled ? 'checked' : ''}>
                                <span class="toggle-slider"></span>
                            </label>
                        </div>
                        <div class="preference-description">
                            在指定時段內暫停推送通知，避免干擾休息
                        </div>
                    </div>
                    
                    <div class="time-range-container" ${!firstPref.quietHoursEnabled ? 'style="display: none;"' : ''}>
                        <div class="time-input-group">
                            <label for="quietHoursStart">開始時間</label>
                            <input type="time" class="quiet-hours-start" name="quietHoursStart" 
                                   value="${firstPref.quietHoursStart || '22:00'}">
                        </div>
                        <div class="time-input-group">
                            <label for="quietHoursEnd">結束時間</label>
                            <input type="time" class="quiet-hours-end" name="quietHoursEnd" 
                                   value="${firstPref.quietHoursEnd || '08:00'}">
                        </div>
                    </div>
                </div>
            `;
        }
        
        container.html(html);
        
        // 🎨 添加勿擾時段開關事件
        $('[name="quietHoursEnabled"]').change(function() {
            const timeRangeContainer = $('.time-range-container');
            if ($(this).is(':checked')) {
                timeRangeContainer.slideDown(300);
            } else {
                timeRangeContainer.slideUp(300);
            }
        });
        
        // 🎨 添加方法行的交互效果（僅前端）
        if (!isBackend) {
            $('.method-row').on('click', function(e) {
                if (e.target.type !== 'checkbox') {
                    const checkbox = $(this).find('input[type="checkbox"]');
                    checkbox.prop('checked', !checkbox.prop('checked'));
                }
            });
        }
    }

    function getCategoryDescription(category) {
        const descriptions = {
            '訂單狀態': '訂單狀態變更、配送進度等相關通知',
            '促銷活動': '最新優惠、特價商品和限時活動通知',
            '系統通知': '帳戶安全、系統維護等重要通知',
            '安全提醒': '帳戶安全、登入異常等安全相關通知',
            '更新公告': '系統更新、新功能發布等公告',
            '配對通知': '新配對、訊息提醒等交友相關通知',
            '付款提醒': '付款到期、扣款失敗等付款相關提醒'
        };
        return descriptions[category] || '相關通知和更新';
    }

    function savePreferences() {
        console.log('=== 開始儲存偏好設定流程 ===');
        console.log('當前頁面URL:', window.location.href);
        console.log('表單元素:', $('#preferences-form').length);
        console.log('category-row 元素數量:', $('.category-row').length);
        
        const preferences = [];
        
        // 🔧 修復：為每個通知類別分別收集設定
        $('.category-row').each(function() {
            const row = $(this);
            const preferenceId = row.data('preference-id');
            const category = row.data('category');
            
            console.log('處理偏好設定:', { preferenceId, category });
            
            // 為每個類別分別收集設定
            const emailEnabled = $('[name="emailEnabled_'+preferenceId+'"]').is(':checked');
            const smsEnabled = $('[name="smsEnabled_'+preferenceId+'"]').is(':checked');
            const inAppEnabled = $('[name="inAppEnabled_'+preferenceId+'"]').is(':checked');
            const pushEnabled = $('[name="pushEnabled_'+preferenceId+'"]').is(':checked');
            
            console.log('通知方式設定 (類別:' + category + '):', { emailEnabled, smsEnabled, inAppEnabled, pushEnabled });
            
            // 從勿擾設定區塊收集設定（所有類別共用）
            const quietHoursEnabled = $('[name="quietHoursEnabled"]').is(':checked');
            const quietHoursStart = $('.quiet-hours-start').val();
            const quietHoursEnd = $('.quiet-hours-end').val();
            
            console.log('勿擾設定:', { quietHoursEnabled, quietHoursStart, quietHoursEnd });
            
            const preference = {
                preferenceId: preferenceId,
                notificationCategory: category,
                emailEnabled: emailEnabled,
                smsEnabled: smsEnabled,
                pushEnabled: pushEnabled,
                inAppEnabled: inAppEnabled,
                quietHoursEnabled: quietHoursEnabled,
                quietHoursStart: quietHoursStart || null,
                quietHoursEnd: quietHoursEnd || null
            };
            
            preferences.push(preference);
            console.log('添加偏好設定 (類別:' + category + '):', preference);
        });

        console.log('=== 最終收集到的偏好設定 ===');
        console.log('偏好設定數量:', preferences.length);
        console.log('詳細內容:', JSON.stringify(preferences, null, 2));

        if (preferences.length === 0) {
            console.error('沒有找到任何偏好設定資料！');
            console.log('DOM調試資訊:');
            console.log('- .category-row 元素:', $('.category-row'));
            console.log('- #preferences-container:', $('#preferences-container'));
            console.log('- 所有data-preference-id元素:', $('[data-preference-id]'));
            
            Swal.fire({
                icon: 'error',
                title: '儲存失敗',
                text: '沒有找到可儲存的偏好設定',
                confirmButtonText: '確定'
            });
            return;
        }

        console.log('=== 開始發送AJAX請求 ===');
        console.log('請求URL: /notifications/api/preferences');
        console.log('請求方法: PUT');
        console.log('請求資料:', preferences);

        $.ajax({
            url: '/notifications/api/preferences',
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(preferences),
            beforeSend: function(xhr) {
                console.log('=== AJAX請求即將發送 ===');
                console.log('請求標頭:', xhr.getAllResponseHeaders());
            },
            success: function(data) {
                console.log('=== 儲存成功 ===');
                console.log('後端回應:', data);
                Swal.fire({
                    icon: 'success',
                    title: '儲存成功',
                    text: '通知偏好設定已更新',
                    timer: 2000,
                    showConfirmButton: false
                });
            },
            error: function(xhr, status, error) {
                console.error('=== 儲存失敗 ===');
                console.error('HTTP狀態碼:', xhr.status);
                console.error('狀態文字:', status);
                console.error('錯誤訊息:', error);
                console.error('回應內容:', xhr.responseText);
                console.error('回應標頭:', xhr.getAllResponseHeaders());
                
                if (xhr.status === 401) {
                    Swal.fire({
                        icon: 'error',
                        title: '認證失敗',
                        text: '請重新登入',
                        confirmButtonText: '確定'
                    }).then(() => {
                        window.location.href = '/login';
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '儲存失敗',
                        text: '儲存偏好設定時發生錯誤，請稍後再試',
                        confirmButtonText: '確定'
                    });
                }
            }
        });
    }

    function showErrorMessage(message) {
        $('#preferences-container').html(`
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> ${message}
            </div>
        `);
    }

    // 全域函數：建立預設通知設定
    window.createDefaultPreferences = function() {
        Swal.fire({
            title: '建立預設通知設定',
            text: '系統將為您建立基本的通知偏好設定',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '確定建立',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/notifications/api/preferences/create-defaults',
                    method: 'POST',
                    success: function(data) {
                        Swal.fire({
                            icon: 'success',
                            title: '建立成功',
                            text: '已為您建立預設的通知偏好設定',
                            timer: 2000,
                            showConfirmButton: false
                        }).then(() => {
                            // 重新載入偏好設定
                            loadPreferences();
                        });
                    },
                    error: function(xhr, status, error) {
                        console.error('建立預設設定失敗:', error);
                        if (xhr.status === 401) {
                            Swal.fire({
                                icon: 'error',
                                title: '認證失敗',
                                text: '請重新登入',
                                confirmButtonText: '確定'
                            }).then(() => {
                                window.location.href = '/login';
                            });
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: '建立失敗',
                                text: '建立預設設定時發生錯誤，請稍後再試',
                                confirmButtonText: '確定'
                            });
                        }
                    }
                });
            }
        });
    };
    
    // 全域函數：測試通知設定
    window.testNotificationSettings = function() {
        console.log('測試通知設定被點擊');
        
        $.ajax({
            url: '/notifications/api/test-notification',
            method: 'POST',
            success: function(response) {
                console.log('測試通知回應:', response);
                if (response.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '測試通知已發送',
                        text: response.message,
                        confirmButtonText: '確定'
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '測試通知發送失敗',
                        text: response.message,
                        confirmButtonText: '確定'
                    });
                }
            },
            error: function(xhr, status, error) {
                console.error('測試通知失敗:', error);
                Swal.fire({
                    icon: 'error',
                    title: '測試通知發送失敗',
                    text: '請檢查網路連接或稍後再試',
                    confirmButtonText: '確定'
                });
            }
        });
    };
    
    // 🔧 新增：創建預設偏好設定的函數
    window.createDefaultPreferences = function() {
        console.log('創建預設偏好設定被點擊');
        
        Swal.fire({
            title: '創建預設偏好設定',
            text: '是否要為您創建預設的通知偏好設定？',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '確定',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/notifications/api/preferences/create-defaults',
                    method: 'POST',
                    success: function(response) {
                        console.log('創建預設偏好設定回應:', response);
                        Swal.fire({
                            icon: 'success',
                            title: '預設偏好設定已創建',
                            text: '已為您創建預設的通知偏好設定',
                            confirmButtonText: '確定'
                        }).then(() => {
                            // 重新載入偏好設定
                            loadPreferences();
                        });
                    },
                    error: function(xhr, status, error) {
                        console.error('創建預設偏好設定失敗:', error);
                        Swal.fire({
                            icon: 'error',
                            title: '創建預設偏好設定失敗',
                            text: '請檢查網路連接或稍後再試',
                            confirmButtonText: '確定'
                        });
                    }
                });
            }
        });
    };
    
}); 