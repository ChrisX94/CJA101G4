
document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/ShShop/allTypes")
        .then(response => {
            if (!response.ok) throw new Error("分類載入失敗");
            return response.json();
        })
        .then(res => {
            if (res.status === 200 && Array.isArray(res.data)) {
                const $select = document.querySelector('select[name="prodTypeId"]');
                $select.innerHTML = '<option value="">Product Type</option>';

                res.data.forEach(type => {
                    const option = document.createElement("option");
                    option.value = type.prodTypeId;
                    option.textContent = type.prodTypeName;
                    $select.appendChild(option);
                });
            } else {
                console.error("後端格式錯誤或無資料");
            }
        })
        .catch(err => {
            console.error("載入分類失敗：", err);
        });
});
