$(document).ready(function() {
    const unreadCountApi = '/api/notifications/unread-count';
    const notificationsApi = '/api/notifications';
    const markAsReadApi = (id) => `/api/notifications/${id}/read`;

    const $badge = $('#unread-count-badge');
    const $dropdown = $('#notificationDropdown');
    const $container = $('#notification-list-container');

    // 1. Fetch and display unread count on page load
    function updateUnreadCount() {
        $.get(unreadCountApi)
            .done(function(data) {
                if (data.unreadCount > 0) {
                    $badge.text(data.unreadCount).show();
                } else {
                    $badge.hide();
                }
            })
            .fail(function() {
                // Silently fail, don't bother the user
                $badge.hide();
            });
    }

    // 2. Load notifications when dropdown is opened
    function loadNotifications() {
        $container.html(`
            <div class="text-center p-3">
                <div class="spinner-border spinner-border-sm" role="status">
                    <span class="visually-hidden">載入中...</span>
                </div>
            </div>
        `);

        $.get(notificationsApi, { page: 0, size: 5 }) // Get latest 5
            .done(function(page) {
                $container.empty();
                if (!page.content || page.content.length === 0) {
                    $container.html('<a href="#" class="list-group-item">目前沒有任何通知</a>');
                    return;
                }

                page.content.forEach(function(noti) {
                    const itemClass = noti.read ? '' : 'notification-item-unread';
                    const notificationHtml = `
                        <a href="${noti.linkUrl || '#'}" class="list-group-item list-group-item-action ${itemClass}" data-notification-id="${noti.notificationId}">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1">${noti.title}</h6>
                                <small>${new Date(noti.createdAt).toLocaleString()}</small>
                            </div>
                            <p class="mb-1 small">${noti.content}</p>
                        </a>
                    `;
                    $container.append(notificationHtml);
                });
            })
            .fail(function() {
                $container.html('<a href="#" class="list-group-item text-danger">讀取通知失敗</a>');
            });
    }

    // 3. Bind events
    $dropdown.on('show.bs.dropdown', loadNotifications);

    // 4. Handle click on a single notification item (using event delegation)
    $container.on('click', 'a.list-group-item', function(e) {
        e.preventDefault();
        
        const $item = $(this);
        const notificationId = $item.data('notification-id');
        const linkUrl = $item.attr('href');

        if (!notificationId) return;

        // Mark as read API call
        $.post(markAsReadApi(notificationId))
            .done(function() {
                // Visually mark as read
                $item.removeClass('notification-item-unread');
                // Update the main counter after a short delay
                setTimeout(updateUnreadCount, 500);
            })
            .always(function() {
                // Navigate after API call completes, regardless of success
                if (linkUrl && linkUrl !== '#') {
                    window.location.href = linkUrl;
                }
            });
    });

    // Initial load
    updateUnreadCount();
}); 