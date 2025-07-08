$(document).ready(function () {
    console.log('=== 通知管理頁面載入開始 ===');
    
    // 檢查登入狀態
    $.get('/api/admin/notifications/check-auth')
        .fail(function(xhr) {
            if (xhr.status === 401) {
                Swal.fire({
                    title: '未登入',
                    text: '請先登入系統',
                    icon: 'warning',
                    confirmButtonText: '前往登入'
                }).then(() => {
                    window.location.href = '/adm/admLogin';
                });
            }
        });
    
    const notificationsApiUrl = '/api/admin/notifications';
    const templatesApiUrl = '/api/admin/notifications/templates';

    const notificationModal = new bootstrap.Modal(document.getElementById('notificationModal'));
    const reportModal = new bootstrap.Modal(document.getElementById('reportModal'));

    const dt = $('#notifications-table').DataTable({
        processing: true,
        serverSide: true,
        pageLength: 50, // 默认每页50条
        lengthMenu: [10, 20, 50, 100, 200], // 可选每页条数
        ajax: {
            url: notificationsApiUrl + '/',
            data: function(d) {
                // DataTables参数转Spring Pageable参数
                return {
                    page: Math.floor(d.start / d.length),
                    size: d.length,
                    sort: d.order && d.order.length > 0 ?
                        d.columns[d.order[0].column].data + ',' + d.order[0].dir :
                        'notificationId,desc'
                };
            },
            dataSrc: function(json) {
                // 兼容Spring Page对象
                json.recordsTotal = json.totalElements;
                json.recordsFiltered = json.totalElements;
                return json.content;
            },
            error: function(xhr, error, thrown) {
                console.error('DataTables Ajax error:', error);
                console.error('XHR status:', xhr.status);
                console.error('XHR response:', xhr.responseText);
                console.error('Thrown error:', thrown);
            }
        },
        columns: [
            { data: 'notificationId' },
            { data: 'title' },
            { data: 'targetType' },
            { data: 'status' },
            { 
                data: 'createdTime', 
                render: function(data) {
                    if (!data) return '';
                    if (Array.isArray(data) && data.length >= 5) {
                        const year = data[0];
                        const month = data[1] - 1;
                        const day = data[2];
                        const hour = data[3] || 0;
                        const minute = data[4] || 0;
                        const second = data[5] || 0;
                        const date = new Date(year, month, day, hour, minute, second);
                        return date.toLocaleString('zh-TW');
                    }
                    return new Date(data).toLocaleString('zh-TW');
                }
            },
            { 
                data: 'validFrom', 
                title: '通知有效起始時間',
                render: function(data) {
                    if (!data) return '';
                    if (Array.isArray(data) && data.length >= 5) {
                        const year = data[0];
                        const month = data[1] - 1;
                        const day = data[2];
                        const hour = data[3] || 0;
                        const minute = data[4] || 0;
                        const second = data[5] || 0;
                        const date = new Date(year, month, day, hour, minute, second);
                        return date.toLocaleString('zh-TW');
                    }
                    return new Date(data).toLocaleString('zh-TW');
                }
            },
            { 
                data: 'validUntil', 
                title: '通知有效結束時間',
                render: function(data) {
                    if (!data) return '';
                    if (Array.isArray(data) && data.length >= 5) {
                        const year = data[0];
                        const month = data[1] - 1;
                        const day = data[2];
                        const hour = data[3] || 0;
                        const minute = data[4] || 0;
                        const second = data[5] || 0;
                        const date = new Date(year, month, day, hour, minute, second);
                        return date.toLocaleString('zh-TW');
                    }
                    return new Date(data).toLocaleString('zh-TW');
                }
            },
            {
                data: null,
                orderable: false,
                render: function (data, type, row) {
                    let buttons = `<button class="btn btn-primary btn-sm detail-btn" data-id="${row.notificationId}"><i class="fas fa-info-circle"></i> 詳情</button> `;
                    buttons += `<button class="btn btn-info btn-sm report-btn" data-id="${row.notificationId}"><i class="fas fa-chart-bar"></i> 報告</button> `;
                    if (row.status === '草稿') {
                        buttons += `<button class="btn btn-success btn-sm send-btn" data-id="${row.notificationId}"><i class="fas fa-paper-plane"></i> 發送</button> `;
                    }
                    buttons += `<button class="btn btn-danger btn-sm delete-btn" data-id="${row.notificationId}"><i class="fas fa-trash"></i> 刪除</button>`;
                    return buttons;
                }
            }
        ],
        language: { "processing": "處理中...", "loadingRecords": "載入中...", "lengthMenu": "顯示 _MENU_ 筆結果", "zeroRecords": "沒有符合的結果", "info": "顯示第 _START_ 至 _END_ 筆結果，共 _TOTAL_ 筆", "infoEmpty": "顯示第 0 至 0 筆結果，共 0 筆", "infoFiltered": "(從 _MAX_ 筆結果中過濾)", "search": "搜尋:", "paginate": { "first": "第一頁", "previous": "上一頁", "next": "下一頁", "last": "最後一頁" } }
    });

    // Load templates into select dropdown
    function loadTemplates() {
        $.get(templatesApiUrl, { size: 100 }, function (data) {
            const select = $('#templateId');
            select.empty().append('<option selected disabled value="">請選擇一個範本...</option>');
            data.content.forEach(template => {
                select.append(`<option value="${template.templateId}">${template.templateName} (ID: ${template.templateId})</option>`);
            });
        });
    }
    
    // Show/hide user IDs textarea based on target type
    $('#targetType').on('change', function() {
        if ($(this).val() === 'SPECIFIC') {
            $('#targetUsersContainer').show();
        } else {
            $('#targetUsersContainer').hide();
        }
    });

    // Handle Add Notification button click
    $('#add-notification-btn').on('click', function () {
        $('#notification-form')[0].reset();
        $('#notificationId').val('');
        $('#targetUsersContainer').hide();
        $('#modalTitle').text('新增通知');
        loadTemplates();
        notificationModal.show();
    });

    // Handle form submission
    $('#notification-form').on('submit', function (e) {
        e.preventDefault();
        
        let targetIds = null;
        if ($('#targetType').val() === 'SPECIFIC') {
            targetIds = $('#targetUsers').val().split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));
        }

        let renderParams = null;

        const formData = {
            title: $('#title').val(),
            content: $('#content').val(),
            type: $('#type').val(),
            templateId: parseInt($('#templateId').val()),
            targetType: $('#targetType').val(),
            targetIds: targetIds,
            startTime: $('#startTime').val() ? new Date($('#startTime').val()).toISOString() : null,
            endTime: $('#endTime').val() ? new Date($('#endTime').val()).toISOString() : null,
            notificationCategory: $('#notificationCategory').val(),
            notificationLevel: parseInt($('#notificationLevel').val()),
            renderParams: renderParams,
        };

        console.log('Submitting form data:', formData); // 調試日誌

        $.ajax({
            url: notificationsApiUrl + '/',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                console.log('Success response:', response);
                notificationModal.hide();
                Swal.fire('成功！', '通知已成功建立。', 'success');
                dt.ajax.reload();
            },
            error: function (xhr, status, error) {
                console.error('Error response:', xhr.responseText);
                console.error('Status:', status);
                console.error('Error:', error);
                
                if (xhr.status === 401) {
                    Swal.fire('錯誤！', '您的登入狀態已過期，請重新登入。', 'error')
                    .then(() => {
                        window.location.href = '/adm/admLogin';
                    });
                } else {
                Swal.fire('錯誤！', '儲存失敗，請檢查欄位並稍後再試。', 'error');
                }
            }
        });
    });

    // Handle Send button click
    $('#notifications-table tbody').on('click', '.send-btn', function () {
        const id = $(this).data('id');
        Swal.fire({
            title: '確定要立即發送嗎？',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '是的，發送！',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`${notificationsApiUrl}/${id}/send`)
                    .done(() => {
                        Swal.fire('已發送！', '通知已加入發送佇列。', 'success');
                        dt.ajax.reload();
                    })
                    .fail(() => Swal.fire('錯誤！', '發送失敗。', 'error'));
            }
        });
    });

    // Handle Delete button click
    $('#notifications-table tbody').on('click', '.delete-btn', function () {
        const id = $(this).data('id');
        // Similar to template deletion logic
        Swal.fire({
            title: '確定要刪除嗎？',
            text: "此操作無法復原！",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            confirmButtonText: '是的，刪除它！',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `${notificationsApiUrl}/${id}`,
                    type: 'DELETE',
                    success: () => {
                        Swal.fire('已刪除！', '通知已被成功刪除。', 'success');
                        dt.ajax.reload();
                    },
                    error: () => Swal.fire('錯誤！', '刪除失敗。', 'error')
                });
            }
        });
    });

    // Handle Report button click
    $('#notifications-table tbody').on('click', '.report-btn', function () {
        const id = $(this).data('id');
        
        // 顯示選項：簡單報告或詳細報告
        Swal.fire({
            title: '選擇報告類型',
            text: '您想查看哪種報告？',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '詳細報告 (新頁面)',
            cancelButtonText: '簡單報告 (模態框)',
            showDenyButton: true,
            denyButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                // 打開詳細報告頁面
                window.open(`/api/admin/notifications/report/page?notificationId=${id}`, '_blank');
            } else if (result.dismiss === Swal.DismissReason.cancel) {
                // 顯示簡單報告在模態框中
                $.get(`${notificationsApiUrl}/${id}/report`, function (data) {
                    console.log('Report data received:', data); // 調試日誌
                    const content = `
                        <div class="row text-center">
                            <div class="col-md-6 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title text-primary">${data.totalSent}</h5>
                                        <p class="card-text">總發送數</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title text-success">${data.successCount}</h5>
                                        <p class="card-text">成功發送數</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title text-info">${data.readCount}</h5>
                                        <p class="card-text">已讀數</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title text-warning">${(data.readRate * 100).toFixed(1)}%</h5>
                                        <p class="card-text">已讀率</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title text-danger">${data.failureCount}</h5>
                                        <p class="card-text">發送失敗數</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title text-secondary">${data.clickCount}</h5>
                                        <p class="card-text">點擊數</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                    $('#report-content').html(content);
                    reportModal.show();
                }).fail(() => Swal.fire('錯誤！', '無法獲取報告。', 'error'));
            }
        });
    });

    // Event listener for view report button
    $('#notifications-table tbody').on('click', '.view-report-btn', function() {
        const id = $(this).data('id');
        window.open(`/api/admin/notifications/report/page?notificationId=${id}`, '_blank');
    });

    // Event listener for delete button
    $('#notifications-table tbody').on('click', '.delete-notification-btn', function() {
        const id = $(this).data('id');
        // Similar to template deletion logic
        Swal.fire({
            title: '確定要刪除嗎？',
            text: "此操作無法復原！",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            confirmButtonText: '是的，刪除它！',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `${notificationsApiUrl}/${id}`,
                    type: 'DELETE',
                    success: () => {
                        Swal.fire('已刪除！', '通知已被成功刪除。', 'success');
                        dt.ajax.reload();
                    },
                    error: () => Swal.fire('錯誤！', '刪除失敗。', 'error')
                });
            }
        });
    });

    // 在js底部新增詳情按鈕事件
    $(document).on('click', '.detail-btn', function () {
        const id = $(this).data('id');
        $.get(`/api/admin/notifications/${id}`, function (data) {
            function formatDate(val) {
                if (!val) return '';
                try {
                    return new Date(val).toLocaleString('zh-TW');
                } catch { return val; }
            }
            function formatObj(val) {
                if (val == null) return '';
                if (typeof val === 'object') return `<pre style="white-space:pre-wrap;">${JSON.stringify(val, null, 2)}</pre>`;
                return val;
            }
            function formatLevel(val) {
                if (val === 1 || val === '1') return '一般';
                if (val === 2 || val === '2') return '重要';
                if (val === 3 || val === '3') return '緊急';
                return val || '';
            }
            let html = `<div style='text-align:left;'>` +
                `<b>通知編號：</b> ${data.notificationId}<br>` +
                `<b>通知類型：</b> ${data.notificationType || ''}<br>` +
                `<b>通知細分類別：</b> ${data.notificationCategory || ''}<br>` +
                `<b>通知重要程度：</b> ${formatLevel(data.notificationLevel)}<br>` +
                `<b>通知標題：</b> ${data.title || ''}<br>` +
                `<b>通知內容：</b> ${data.message || ''}<br>` +
                `<b>是否為廣播：</b> ${data.isBroadcast === true ? '是' : (data.isBroadcast === false ? '否' : '')}<br>` +
                `<b>目標受眾篩選條件：</b> ${formatObj(data.targetCriteria)}<br>` +
                `<b>通知有效起始時間：</b> ${formatDate(data.validFrom)}<br>` +
                `<b>通知有效結束時間：</b> ${formatDate(data.validUntil)}<br>` +
                `<b>建立時間：</b> ${formatDate(data.createdTime)}<br>` +
                `<b>更新時間：</b> ${data.updatedTime ? formatDate(data.updatedTime) : '-'}<br>` +
                `<b>系統管理員ID：</b> ${data.createdBy || ''}<br>` +
                `<b>通知狀態：</b> ${data.status || ''}<br>` +
                `</div>`;
            Swal.fire({
                title: '通知詳情',
                html: html,
                width: 700,
                confirmButtonText: '關閉',
            });
        }).fail(() => Swal.fire('錯誤！', '無法獲取通知詳情。', 'error'));
    });
});
