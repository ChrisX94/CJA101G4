$(document).ready(function () {
    const apiBaseUrl = '/api/admin/notifications/templates';
    const templateModal = new bootstrap.Modal(document.getElementById('template-modal'));
    
    // 智能欄位顯示控制
    function toggleHtmlTemplateField(templateType) {
        const htmlGroup = $('#htmlTemplateGroup');
        const htmlField = $('#htmlTemplate');
        
        if (templateType === 'EMAIL') {
            htmlGroup.show();
            htmlField.removeAttr('disabled');
        } else {
            htmlGroup.hide();
            htmlField.attr('disabled', true).val('');
        }
    }
    
    // 監聽模板類型變更
    $('#templateType').on('change', function() {
        toggleHtmlTemplateField($(this).val());
    });
    
    // 字符計數功能
    $('#contentTemplate').on('input', function() {
        const current = $(this).val().length;
        const max = 255;
        const remaining = max - current;
        
        let color = 'text-muted';
        if (remaining < 50) color = 'text-warning';
        if (remaining < 20) color = 'text-danger';
        
        $(this).siblings('.form-text').html(
            `可使用預留位置，例如 {{userName}}、{{actionUrl}}。適用於所有類型的通知 📝 
            <span class="${color}">(${current}/${max} 字符，剩餘 ${remaining})</span>`
        );
    });
    let dt = $('#templates-table').DataTable({
        processing: true,
        serverSide: true, // 啟用伺服器端分頁
        ajax: {
            url: apiBaseUrl,
            data: function(d) {
                // 將DataTables參數轉換為Spring Pageable參數
                return {
                    page: Math.floor(d.start / d.length), // 計算頁碼
                    size: d.length, // 每頁大小
                    sort: d.order.length > 0 ? 
                        d.columns[d.order[0].column].data + ',' + d.order[0].dir : 
                        'templateId,asc' // 排序參數
                };
            },
            // 設置數據來源和分頁信息
            dataSrc: function(json) {
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
            { data: 'templateId' },
            { data: 'templateName' },
            { data: 'templateType' },
            { data: 'titleTemplate' },
            { 
                data: 'updatedAt',
                render: function(data) {
                    if (!data) return '';
                    // 處理 Spring 返回的陣列格式 [2025,6,28,21,39,56]
                    if (Array.isArray(data) && data.length >= 6) {
                        const date = new Date(data[0], data[1] - 1, data[2], data[3], data[4], data[5]);
                        return date.toLocaleString('zh-TW');
                    }
                    // 處理標準日期字符串
                    return new Date(data).toLocaleString('zh-TW');
                }
            },
            {
                data: null,
                orderable: false,
                render: function (data, type, row) {
                    return `
                        <button class="btn btn-warning btn-sm edit-btn" data-id="${row.templateId}"><i class="fas fa-edit"></i> 編輯</button>
                        <button class="btn btn-danger btn-sm delete-btn" data-id="${row.templateId}"><i class="fas fa-trash"></i> 刪除</button>
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

    // Handle Add button click
    $('#add-template-btn').on('click', function () {
        $('#template-form')[0].reset();
        $('#templateId').val('');
        $('#templateModalLabel').text('新增範本');
        
        // 重置HTML欄位顯示狀態
        toggleHtmlTemplateField('');
        
        templateModal.show();
    });

    // Handle Edit button click
    $('#templates-table tbody').on('click', '.edit-btn', function () {
        const id = $(this).data('id');
        const rowData = dt.rows().data().toArray().find(r => r.templateId === id);
        
        console.log('=== Edit button clicked ===');
        console.log('Template ID:', id);
        console.log('Row data:', rowData);
        
        if (rowData) {
            $('#templateId').val(rowData.templateId);
            $('#templateCode').val(rowData.templateCode);
            $('#templateName').val(rowData.templateName);
            $('#templateType').val(rowData.templateType);
            $('#templateCategory').val(rowData.templateCategory || '系統通知');
            $('#titleTemplate').val(rowData.titleTemplate);
            $('#contentTemplate').val(rowData.contentTemplate);
            $('#htmlTemplate').val(rowData.htmlTemplate);
            $('#isActive').val(rowData.isActive ? 'true' : 'false');
            $('#isSystem').val(rowData.isSystem ? 'true' : 'false');
            $('#templateModalLabel').text('編輯範本');
            
            // 根據模板類型顯示/隱藏HTML欄位
            toggleHtmlTemplateField(rowData.templateType);
            
            console.log('Form fields populated:');
            console.log('- templateId:', $('#templateId').val());
            console.log('- templateCode:', $('#templateCode').val());
            console.log('- templateName:', $('#templateName').val());
            console.log('- templateType:', $('#templateType').val());
            console.log('- templateCategory:', $('#templateCategory').val());
            
            templateModal.show();
        } else {
            console.error('Row data not found for ID:', id);
        }
    });

    // Handle Delete button click
    $('#templates-table tbody').on('click', '.delete-btn', function () {
        const id = $(this).data('id');
        Swal.fire({
            title: '確定要刪除嗎？',
            text: "此操作無法復原！",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: '是的，刪除它！',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `${apiBaseUrl}/${id}`,
                    type: 'DELETE',
                    success: function () {
                        Swal.fire('已刪除！', '範本已被成功刪除。', 'success');
                        dt.ajax.reload();
                    },
                    error: function (err) {
                        Swal.fire('錯誤！', '刪除失敗，請稍後再試。', 'error');
                    }
                });
            }
        });
    });

    // 自定義驗證：至少需要一個內容模板
    function validateTemplateContent() {
        const contentTemplate = $('#contentTemplate').val().trim();
        const htmlTemplate = $('#htmlTemplate').val().trim();
        
        if (!contentTemplate && !htmlTemplate) {
            Swal.fire('驗證錯誤！', '至少需要填寫一個內容模板（純文字或HTML）', 'error');
            return false;
        }
        return true;
    }

    // Handle form submission
    $('#template-form').on('submit', function (e) {
        e.preventDefault();
        
        // 自定義驗證
        if (!validateTemplateContent()) {
            return;
        }
        
        const id = $('#templateId').val();
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${apiBaseUrl}/${id}` : apiBaseUrl;

        const formData = {
            templateId: id || null,
            templateCode: $('#templateCode').val(),
            templateName: $('#templateName').val(),
            templateType: $('#templateType').val(),
            titleTemplate: $('#titleTemplate').val(),
            contentTemplate: $('#contentTemplate').val(),
            htmlTemplate: $('#htmlTemplate').val() || null,
            templateCategory: $('#templateCategory').val() || '系統通知',
            variables: '{}', // 提供預設的 JSON 值
            isActive: $('#isActive').val() === 'true',
            isSystem: $('#isSystem').val() === 'true',
        };

        console.log('=== Form submission ===');
        console.log('Method:', method);
        console.log('URL:', url);
        console.log('Form data:', formData);

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                console.log('Success response:', response);
                templateModal.hide();
                Swal.fire('成功！', '範本已成功儲存。', 'success');
                dt.ajax.reload(null, false); // 重新載入但保持當前頁面
            },
            error: function (err) {
                console.error('Save error:', err);
                console.error('Error response:', err.responseText);
                Swal.fire('錯誤！', '儲存失敗：' + (err.responseText || '請檢查欄位並稍後再試'), 'error');
            }
        });
    });
}); 