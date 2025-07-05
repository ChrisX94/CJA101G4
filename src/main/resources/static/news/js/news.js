window.addEventListener('DOMContentLoaded', async () => {
  const tbody = document.querySelector('#news-table tbody');

  try {
    // 注意：这里改成 /admin/news/latest
    const resp = await fetch('/admin/news/latest');
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
    const list = await resp.json();

    if (list.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5">目前沒有新聞</td></tr>';
      return;
    }

    list.forEach(item => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${item.newsId}</td>
        <td>${item.title}</td>
        <td>${item.categoryName}</td>
        <td>${new Date(item.publishTime).toLocaleString()}</td>
        <td>${item.adminName}</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (e) {
    console.error(e);
    tbody.innerHTML = `<tr><td colspan="5">讀取失敗：${e.message}</td></tr>`;
  }
});
