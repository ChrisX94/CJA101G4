$(document).ready(function() {
    const params = new URLSearchParams(window.location.search);
    const notificationId = params.get('notificationId');

    // 全局變量保存圖表實例
    let charts = {
        send: null,
        success: null,
        interaction: null
    };

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
                    <div class="col-lg-10">
                        <div class="card shadow">
                            <div class="card-header" style="background: linear-gradient(45deg, #2EC4B6, #DCFF61); color: #000;">
                                <h5 class="mb-0" style="font-size: 1.2rem; font-weight: 500;"><i class="fas fa-chart-line me-2"></i>選擇要查看的通知報告</h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table id="notification-selector-table" class="table table-hover" style="width:100%">
                                        <thead class="table-light">
                                            <tr>
                                                <th><i class="fas fa-hashtag me-1"></i>ID</th>
                                                <th><i class="fas fa-heading me-1"></i>標題</th>
                                                <th><i class="fas fa-traffic-light me-1"></i>狀態</th>
                                                <th><i class="fas fa-calendar-plus me-1"></i>創建時間</th>
                                                <th><i class="fas fa-cogs me-1"></i>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <!-- Data will be populated by DataTables -->
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
        
        // 使用DataTables初始化表格
        const dt = $('#notification-selector-table').DataTable({
            processing: true,
            serverSide: true,
            pageLength: 10, // 🔧 預設每頁10筆
            lengthMenu: [10, 20, 50, 100], // 可選每頁條數
            ajax: {
                url: '/api/admin/notifications/',
                data: function(d) {
                    // DataTables參數轉Spring Pageable參數
                    return {
                        page: Math.floor(d.start / d.length),
                        size: d.length,
                        sort: d.order && d.order.length > 0 ?
                            d.columns[d.order[0].column].data + ',' + d.order[0].dir :
                            'notificationId,desc'
                    };
                },
                dataSrc: function(json) {
                    // 兼容Spring Page對象
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
                { 
                    data: 'status',
                    render: function(data) {
                        return getStatusBadge(data);
                    }
                },
                { 
                    data: 'createdTime',
                    render: function(data) {
                        return formatDateTime(data);
                    }
                },
                {
                    data: null,
                    orderable: false,
                    render: function(data, type, row) {
                        return `
                            <button class="btn btn-sm view-report-btn" 
                                    style="background: linear-gradient(45deg, #2EC4B6, #4A90E2); border: none; color: #fff; padding: 0.5rem 1rem; border-radius: 0.8rem;"
                                    data-id="${row.notificationId}">
                                <i class="fas fa-chart-bar me-1"></i>查看報告
                            </button>
                        `;
                    }
                }
            ],
            language: {
                "processing": "處理中...",
                "loadingRecords": "載入中...",
                "lengthMenu": "顯示 _MENU_ 筆結果",
                "zeroRecords": "沒有符合的結果",
                "info": "顯示第 _START_ 至 _END_ 筆結果，共 _TOTAL_ 筆",
                "infoEmpty": "顯示第 0 至 0 筆結果，共 0 筆",
                "infoFiltered": "(從 _MAX_ 筆結果中過濾)",
                "search": "搜尋:",
                "paginate": {
                    "first": "第一頁",
                    "previous": "上一頁",
                    "next": "下一頁",
                    "last": "最後一頁"
                }
            }
        });
        
        // 綁定查看報告按鈕事件
        $('#notification-selector-table tbody').on('click', '.view-report-btn', function() {
            const id = $(this).data('id');
            window.location.href = `/api/admin/notifications/report/page?notificationId=${id}`;
        });
    }

    function loadNotificationReport(notificationId) {
        $.ajax({
            url: `/api/admin/notifications/${notificationId}/report`,
            method: 'GET',
            success: function(response) {
                console.log('Report data:', response);
                updateKPICards(response);
                renderCharts(response);
            },
            error: function(xhr, status, error) {
                console.error('Error fetching report:', error);
                Swal.fire('錯誤', '獲取報表數據失敗', 'error');
            }
        });
    }

    function updateKPICards(data) {
        $('#total-sent').text(data.totalSent);
        $('#success-count').text(data.successCount);
        $('#failure-count').text(data.failureCount);
        $('#read-count').text(data.readCount);
        $('#click-count').text(data.clickCount);
        $('#success-rate').text((data.successRate * 100).toFixed(1) + '%');
        $('#read-rate').text((data.readRate * 100).toFixed(1) + '%');
        $('#click-rate').text((data.clickRate * 100).toFixed(1) + '%');
    }

    function renderCharts(data) {
        // 發送量折線圖
        if (data.sendTrend && data.sendTrend.labels && data.sendTrend.data) {
            createLineChart('sendChart', data.sendTrend.labels, data.sendTrend.data, '發送量');
        }

        // 成功/失敗分布圓餅圖
        const successFailureLabels = ['成功', '失敗'];
        const successFailureData = [data.successCount, data.failureCount];
        const successFailureColors = ['#38ef7d', '#ff6b6b'];
        createPieChart('successChart', successFailureLabels, successFailureData, successFailureColors);

        // 已讀/未讀/點擊分布圓餅圖
        const interactionLabels = ['已讀', '未讀', '已點擊'];
        const interactionData = [
            data.readCount,
            data.totalSent - data.readCount,
            data.clickCount
        ];
        const interactionColors = ['#4facfe', '#f093fb', '#f5576c'];
        createPieChart('interactionChart', interactionLabels, interactionData, interactionColors);
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

    function destroyChart(chart) {
        if (chart && typeof chart.destroy === 'function') {
            chart.destroy();
        }
    }

    function createPieChart(canvasId, labels, data, colors) {
        const canvas = document.getElementById(canvasId);
        if (!canvas) {
            console.error(`Canvas ${canvasId} not found`);
            return;
        }

        // 先清理已存在的圖表
        if (canvasId === 'successChart') {
            destroyChart(charts.success);
            charts.success = null;
        } else if (canvasId === 'interactionChart') {
            destroyChart(charts.interaction);
            charts.interaction = null;
        }

        // 檢查數據有效性
        const totalValue = data.reduce((a, b) => a + b, 0);
        if (!labels || !data || totalValue === 0) {
            canvas.style.display = 'none';
            const noDataDiv = document.createElement('div');
            noDataDiv.className = 'text-center text-muted py-3';
            noDataDiv.textContent = '暫無資料';
            canvas.parentNode.insertBefore(noDataDiv, canvas.nextSibling);
            return;
        }

        canvas.style.display = 'block';
        const existingNoData = canvas.parentNode.querySelector('.text-muted');
        if (existingNoData) {
            existingNoData.remove();
        }

        const ctx = canvas.getContext('2d');
        const chartConfig = {
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
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const value = context.raw;
                                const percentage = ((value / totalValue) * 100).toFixed(1);
                                return `${context.label}: ${value} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        };

        const newChart = new Chart(ctx, chartConfig);
        if (canvasId === 'successChart') {
            charts.success = newChart;
        } else if (canvasId === 'interactionChart') {
            charts.interaction = newChart;
        }
    }

    function createLineChart(canvasId, labels, data, labelName) {
        const canvas = document.getElementById(canvasId);
        if (!canvas) {
            console.error(`Canvas ${canvasId} not found`);
            return;
        }

        // 先清理已存在的圖表
        destroyChart(charts.send);
        charts.send = null;

        // 檢查數據有效性
        if (!labels || !data || labels.length === 0 || data.length === 0) {
            canvas.style.display = 'none';
            const noDataDiv = document.createElement('div');
            noDataDiv.className = 'text-center text-muted py-3';
            noDataDiv.textContent = '暫無資料';
            canvas.parentNode.insertBefore(noDataDiv, canvas.nextSibling);
            return;
        }

        canvas.style.display = 'block';
        const existingNoData = canvas.parentNode.querySelector('.text-muted');
        if (existingNoData) {
            existingNoData.remove();
        }

        const ctx = canvas.getContext('2d');
        const chartConfig = {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: labelName,
                    data: data,
                    borderColor: '#2EC4B6',
                    backgroundColor: 'rgba(46, 196, 182, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    }
                }
            }
        };

        charts.send = new Chart(ctx, chartConfig);
    }
}); 