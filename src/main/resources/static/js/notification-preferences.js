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
        let dndSettings = null;

        preferences.forEach(function(pref) {
            // 處理勿擾時段設定
            if (pref.quietHoursEnabled !== null) {
                dndSettings = pref;
            }

            html += `
                <div class="row category-row py-3" data-preference-id="${pref.preferenceId}">
                    <div class="col-md-3">
                        <strong>${categoryNames[pref.notificationCategory] || pref.notificationCategory}</strong>
                    </div>
                    <div class="col-md-2">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" 
                                   id="email-${pref.preferenceId}" 
                                   data-type="email" 
                                   ${pref.emailEnabled ? 'checked' : ''}>
                            <label class="form-check-label" for="email-${pref.preferenceId}">
                                <i class="fas fa-envelope"></i> Email
                            </label>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" 
                                   id="sms-${pref.preferenceId}" 
                                   data-type="sms" 
                                   ${pref.smsEnabled ? 'checked' : ''}>
                            <label class="form-check-label" for="sms-${pref.preferenceId}">
                                <i class="fas fa-sms"></i> SMS
                            </label>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" 
                                   id="push-${pref.preferenceId}" 
                                   data-type="push" 
                                   ${pref.pushEnabled ? 'checked' : ''}>
                            <label class="form-check-label" for="push-${pref.preferenceId}">
                                <i class="fas fa-bell"></i> Push
                            </label>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" 
                                   id="inapp-${pref.preferenceId}" 
                                   data-type="inapp" 
                                   ${pref.inAppEnabled ? 'checked' : ''}>
                            <label class="form-check-label" for="inapp-${pref.preferenceId}">
                                <i class="fas fa-mobile-alt"></i> 應用程式內
                            </label>
                        </div>
                    </div>
                </div>
            `;
        });

        // 添加表頭
        const headerHtml = `
            <div class="row py-2 bg-light rounded mb-3">
                <div class="col-md-3"><strong>通知類別</strong></div>
                <div class="col-md-2"><strong>Email</strong></div>
                <div class="col-md-2"><strong>SMS</strong></div>
                <div class="col-md-2"><strong>推播</strong></div>
                <div class="col-md-3"><strong>應用程式內</strong></div>
            </div>
        `;

        container.html(headerHtml + html);

        // 設定勿擾時段
        if (dndSettings) {
            $('#dnd-enabled').prop('checked', dndSettings.quietHoursEnabled || false);
            if (dndSettings.quietHoursEnabled) {
                $('#dnd-time-range').show();
                if (dndSettings.quietHoursStart) {
                    $('#dnd-start').val(dndSettings.quietHoursStart);
                }
                if (dndSettings.quietHoursEnd) {
                    $('#dnd-end').val(dndSettings.quietHoursEnd);
                }
            }
        }
    }

    function savePreferences() {
        const preferences = [];
        
        $('.category-row').each(function() {
            const row = $(this);
            const preferenceId = row.data('preference-id');
            
            const preference = {
                preferenceId: preferenceId,
                emailEnabled: row.find('[data-type="email"]').is(':checked'),
                smsEnabled: row.find('[data-type="sms"]').is(':checked'),
                pushEnabled: row.find('[data-type="push"]').is(':checked'),
                inAppEnabled: row.find('[data-type="inapp"]').is(':checked'),
                quietHoursEnabled: $('#dnd-enabled').is(':checked'),
                quietHoursStart: $('#dnd-enabled').is(':checked') ? $('#dnd-start').val() || null : null,
                quietHoursEnd: $('#dnd-enabled').is(':checked') ? $('#dnd-end').val() || null : null
            };
            
            preferences.push(preference);
        });

        console.log('準備儲存的偏好設定:', preferences);

        $.ajax({
            url: '/notifications/api/preferences',
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(preferences),
            success: function(data) {
                Swal.fire({
                    icon: 'success',
                    title: '儲存成功',
                    text: '通知偏好設定已更新',
                    timer: 2000,
                    showConfirmButton: false
                });
            },
            error: function(xhr, status, error) {
                console.error('儲存偏好設定失敗:', error);
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