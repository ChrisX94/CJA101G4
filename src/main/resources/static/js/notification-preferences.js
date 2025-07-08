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
        
        if (!preferences || preferences.length === 0) {
            container.html(`
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i> 
                    目前沒有通知偏好設定，系統將使用預設設定。
                    <br><br>
                    <button type="button" class="btn btn-primary mt-2" onclick="createDefaultPreferences()">
                        <i class="fas fa-plus"></i> 建立預設通知設定
                    </button>
                </div>
            `);
            return;
        }

        let html = '';
        
        // 通知類型設定（移到最上方）
        html += `
            <div class="preferences-section">
                <h2>通知類型</h2>
        `;
        preferences.forEach(function(pref) {
            const categoryDisplayName = categoryNames[pref.notificationCategory] || pref.notificationCategory;
            html += `
                <div class="preference-item category-row" data-category="${pref.notificationCategory}" data-preference-id="${pref.preferenceId}">
                    <div class="preference-title">
                        <span>${categoryDisplayName}</span>
                    </div>
                    <div class="preference-description">
                        ${getCategoryDescription(pref.notificationCategory)}
                    </div>
                </div>
            `;
        });
        html += `</div>`;

        // 通知方式設定
        html += `
            <div class="preferences-section">
                <h2>通知方式</h2>
        `;
        
        // 為每個偏好設定生成對應的通知方式選項
        preferences.forEach(function(pref, index) {
            // 只在第一次迭代時生成通知方式控制項
            if (index === 0) {
                html += `
                    <div class="preference-item" data-category="${pref.notificationCategory}" data-preference-id="${pref.preferenceId}">
                        <div class="preference-title">
                            <span>電子郵件通知</span>
                            <label class="toggle-switch">
                                <input type="checkbox" name="emailEnabled" data-type="email" ${pref.emailEnabled ? 'checked' : ''}>
                                <span class="toggle-slider"></span>
                            </label>
                        </div>
                        <div class="preference-description">
                            接收重要更新、訂單狀態變更等通知到您的電子郵件
                        </div>
                    </div>

                    <div class="preference-item" data-category="${pref.notificationCategory}" data-preference-id="${pref.preferenceId}">
                        <div class="preference-title">
                            <span>站內通知</span>
                            <label class="toggle-switch">
                                <input type="checkbox" name="inAppEnabled" data-type="inapp" ${pref.inAppEnabled ? 'checked' : ''}>
                                <span class="toggle-slider"></span>
                            </label>
                        </div>
                        <div class="preference-description">
                            在網站內即時接收通知和更新
                        </div>
                    </div>

                    <div class="preference-item" data-category="${pref.notificationCategory}" data-preference-id="${pref.preferenceId}">
                        <div class="preference-title">
                            <span>APP推播通知</span>
                            <label class="toggle-switch">
                                <input type="checkbox" name="pushEnabled" data-type="push" ${pref.pushEnabled ? 'checked' : ''}>
                                <span class="toggle-slider"></span>
                            </label>
                        </div>
                        <div class="preference-description">
                            是否接收APP推播通知（PUSH）
                        </div>
                    </div>

                    <div class="preference-item" data-category="${pref.notificationCategory}" data-preference-id="${pref.preferenceId}">
                        <div class="preference-title">
                            <span>簡訊通知</span>
                            <label class="toggle-switch">
                                <input type="checkbox" name="smsEnabled" data-type="sms" ${pref.smsEnabled ? 'checked' : ''}>
                                <span class="toggle-slider"></span>
                            </label>
                        </div>
                        <div class="preference-description">
                            接收重要提醒和驗證碼到您的手機
                        </div>
                    </div>
                `;
            }
        });
        
        html += `</div>`;
        
        // 勿擾設定
        const firstPref = preferences[0];
        html += `
            <div class="preferences-section">
                <h2>勿擾設定</h2>
                <div class="preference-item">
                    <div class="preference-title">
                        <span>啟用勿擾模式</span>
                        <label class="toggle-switch">
                            <input type="checkbox" name="quietHoursEnabled" data-type="quiet" ${firstPref.quietHoursEnabled ? 'checked' : ''}>
                            <span class="toggle-slider"></span>
                        </label>
                    </div>
                    <div class="preference-title">
                        <span>勿擾開始時間</span>
                        <input type="time" name="quietHoursStart" class="quiet-hours-start" value="${firstPref.quietHoursStart || ''}">
                    </div>
                    <div class="preference-title">
                        <span>勿擾結束時間</span>
                        <input type="time" name="quietHoursEnd" class="quiet-hours-end" value="${firstPref.quietHoursEnd || ''}">
                    </div>
                </div>
            </div>
        `;

        container.html(html);
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
        
        // 收集所有通知類型偏好設定
        $('.category-row').each(function() {
            const row = $(this);
            const preferenceId = row.data('preference-id');
            const category = row.data('category');
            
            console.log('處理偏好設定:', { preferenceId, category });
            
            // 從通知方式區塊收集設定（使用第一個偏好設定的ID）
            const emailEnabled = $('[name="emailEnabled"]').is(':checked');
            const smsEnabled = $('[name="smsEnabled"]').is(':checked');
            const inAppEnabled = $('[name="inAppEnabled"]').is(':checked');
            const pushEnabled = inAppEnabled; // 推播和站內通知使用相同設定
            
            console.log('通知方式設定:', { emailEnabled, smsEnabled, inAppEnabled, pushEnabled });
            
            // 從勿擾設定區塊收集設定
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
            console.log('添加偏好設定:', preference);
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
}); 