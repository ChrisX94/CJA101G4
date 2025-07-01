$(document).ready(function () {
    const notificationsApiUrl = '/api/admin/notifications';
    const templatesApiUrl = '/api/admin/notifications/templates';

    const notificationModal = new bootstrap.Modal(document.getElementById('notification-modal'));
    const reportModal = new bootstrap.Modal(document.getElementById('report-modal'));

    const dt = $('#notifications-table').DataTable({
        processing: true,
        serverSide: false,
        ajax: {
            url: notificationsApiUrl + '/',
            dataSrc: 'content',
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
                        // 支援 5 個或 6 個元素的陣列 [年, 月, 日, 時, 分, 秒(可選)]
                        const year = data[0];
                        const month = data[1] - 1; // JavaScript 月份從 0 開始
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
                data: 'scheduledTime', 
                render: function(data) {
                    if (!data) return '';
                    if (Array.isArray(data) && data.length >= 5) {
                        // 支援 5 個或 6 個元素的陣列 [年, 月, 日, 時, 分, 秒(可選)]
                        const year = data[0];
                        const month = data[1] - 1; // JavaScript 月份從 0 開始
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
                data: 'sentTime', 
                render: function(data) {
                    if (!data) return '';
                    if (Array.isArray(data) && data.length >= 5) {
                        // 支援 5 個或 6 個元素的陣列 [年, 月, 日, 時, 分, 秒(可選)]
                        const year = data[0];
                        const month = data[1] - 1; // JavaScript 月份從 0 開始
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
                    let buttons = `<button class="btn btn-info btn-sm report-btn" data-id="${row.notificationId}"><i class="fas fa-chart-bar"></i> 報告</button> `;
                    if (row.status === 'DRAFT' || row.status === 'PENDING') {
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
        if ($(this).val() === 'SPECIFIC_USERS') {
            $('#target-user-ids-group').show();
        } else {
            $('#target-user-ids-group').hide();
        }
    });

    // Handle Add Notification button click
    $('#add-notification-btn').on('click', function () {
        $('#notification-form')[0].reset();
        $('#notificationId').val('');
        $('#target-user-ids-group').hide();
        $('#notificationModalLabel').text('新增通知');
        loadTemplates();
        notificationModal.show();
    });

    // Handle form submission
    $('#notification-form').on('submit', function (e) {
        e.preventDefault();
        
        let targetIds = null;
        if ($('#targetType').val() === 'SPECIFIC_USERS') {
            targetIds = $('#targetUserIds').val().split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));
        }

        let renderParams = null;
        try {
            const paramsRaw = $('#params').val();
            if(paramsRaw) renderParams = JSON.parse(paramsRaw);
        } catch (error) {
            Swal.fire('錯誤！', '自訂參數必須是合法的 JSON 格式。', 'error');
            return;
        }

        const formData = {
            title: '預設標題', // 必需欄位，會被模板覆蓋
            message: '預設內容', // 必需欄位，會被模板覆蓋
            templateId: parseInt($('#templateId').val()),
            targetType: $('#targetType').val(),
            targetIds: targetIds,
            scheduledTime: $('#scheduledTime').val() ? new Date($('#scheduledTime').val()).toISOString() : null,
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
                Swal.fire('錯誤！', '儲存失敗，請檢查欄位並稍後再試。', 'error');
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
}); 