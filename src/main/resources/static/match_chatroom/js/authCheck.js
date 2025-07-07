
// 如果會員權限尚未開通，則顯示透明遮罩+導去填寫會員資料的按鈕
fetch("/api/match/getUserStatus")
	.then(res => res.json())
	.then(data => {
		if (data.status !== 1 && data.status !== 2) {
			showLockOverlay(); // 顯示遮罩提示
		}
	})
	.catch(err => {
		console.error("⚠️ 無法取得會員狀態", err);
	});

function showLockOverlay() {
	if (document.getElementById("lockOverlay")) return;

	const overlay = document.createElement("div");
	overlay.id = "lockOverlay";
	overlay.className = "lock-overlay";

	overlay.innerHTML = `
  		<div class="lock-content">
  			<i class="fa-solid fa-lock"></i>
  			<button id="goToEditBtn">請先登入</button>
  		</div>
  	`;

	document.body.appendChild(overlay);

	document.getElementById("goToEditBtn").addEventListener("click", () => {
		window.location.href = "/login"; // 👉 換成你的資料頁面
	});
}