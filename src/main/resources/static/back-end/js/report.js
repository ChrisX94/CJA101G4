$(document).ready(function() {
    const params = new URLSearchParams(window.location.search);
    const notificationId = params.get('notificationId');

    if (!notificationId) {
        // 如果沒有提供 notificationId，顯示通知選擇列表
        showNotificationSelector();
        return;
    }

    loadNotificationReport(notificationId);

    function showNotificationSelector() {
        const selectorHtml = `
            <div class="container py-4">
                <div class="row justify-content-center">
                    <div class="col-lg-8">
                        <div class="card shadow">
                            <div class="card-header bg-primary text-white">
                                <h4 class="mb-0"><i class="fas fa-chart-line"></i> 選擇要查看的通知報告</h4>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table id="notification-selector-table" class="table table-hover">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID</th>
                                                <th>標題</th>
                                                <th>狀態</th>
                                                <th>創建時間</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td colspan="5" class="text-center">
                                                    <div class="spinner-border text-primary" role="status">
                                                        <span class="visually-hidden">載入中...</span>
                                                    </div>
                                                    <div class="mt-2">載入通知列表中...</div>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('body').html(selectorHtml);
        
        // 載入通知列表
        $.get('/api/admin/notifications/', { size: 50 })
            .done(function(data) {
                const tbody = $('#notification-selector-table tbody');
                tbody.empty();
                
                if (data.content && data.content.length > 0) {
                    data.content.forEach(notification => {
                        const createdTime = formatDateTime(notification.createdTime);
                        const statusBadge = getStatusBadge(notification.status);
                        
                        const row = `
                            <tr>
                                <td>${notification.notificationId}</td>
                                <td>${notification.title}</td>
                                <td>${statusBadge}</td>
                                <td>${createdTime}</td>
                                <td>
                                    <button class="btn btn-primary btn-sm view-report-btn" 
                                            data-id="${notification.notificationId}">
                                        <i class="fas fa-chart-bar"></i> 查看報告
                                    </button>
                                </td>
                            </tr>
                        `;
                        tbody.append(row);
                    });
                    
                    // 綁定查看報告按鈕事件
                    $('.view-report-btn').on('click', function() {
                        const id = $(this).data('id');
                        window.location.href = `/api/admin/notifications/report/page?notificationId=${id}`;
                    });
                } else {
                    tbody.html('<tr><td colspan="5" class="text-center text-muted">目前沒有通知記錄</td></tr>');
                }
            })
            .fail(function() {
                $('#notification-selector-table tbody').html(
                    '<tr><td colspan="5" class="text-center text-danger">載入失敗，請稍後再試</td></tr>'
                );
            });
    }

    function loadNotificationReport(notificationId) {
        const apiUrl = `/api/admin/notifications/${notificationId}/report`;

        $.get(apiUrl)
            .done(function(stats) {
                // Fill headers
                $('#notification-title').text(stats.notificationTitle);
                $('#notification-id').text(stats.notificationId);

                // Fill KPI cards
                $('#total-sent').text(stats.totalSent);
                const successRate = stats.totalSent > 0 ? ((stats.successCount / stats.totalSent) * 100).toFixed(1) + '%' : 'N/A';
                $('#success-rate').text(successRate);
                $('#read-rate').text((stats.readRate * 100).toFixed(1) + '%');
                $('#click-rate').text((stats.clickRate * 100).toFixed(1) + '%');

                // --- Create Charts ---

                // Delivery Status Chart
                const deliveryData = [stats.successCount, stats.failureCount, stats.totalSent - stats.successCount - stats.failureCount];
                createPieChart('deliveryStatusChart', ['成功', '失敗', '其他'], deliveryData, ['#28a745', '#dc3545', '#6c757d']);

                // User Interaction Chart
                const interactionData = [stats.clickCount, stats.readCount - stats.clickCount, stats.totalSent - stats.readCount];
                createPieChart('userInteractionChart', ['已點擊', '僅已讀', '未互動'], interactionData, ['#ffc107', '#17a2b8', '#e9ecef']);
            })
            .fail(function() {
                $('body').html('<div class="container py-4"><div class="alert alert-danger">無法載入報告數據，請稍後再試。</div></div>');
            });
    }

    function formatDateTime(data) {
        if (!data) return '';
        if (Array.isArray(data) && data.length >= 5) {
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

    function getStatusBadge(status) {
        const statusMap = {
            'PENDING': '<span class="badge bg-warning">待發送</span>',
            'SENT': '<span class="badge bg-success">已發送</span>',
            'FAILED': '<span class="badge bg-danger">發送失敗</span>',
            'DRAFT': '<span class="badge bg-secondary">草稿</span>'
        };
        return statusMap[status] || `<span class="badge bg-light text-dark">${status}</span>`;
    }

    function createPieChart(canvasId, labels, data, colors) {
        const ctx = document.getElementById(canvasId).getContext('2d');
        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: colors,
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let label = context.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.raw !== null) {
                                    label += context.raw;
                                }
                                return label;
                            }
                        }
                    }
                }
            },
        });
    }
}); 