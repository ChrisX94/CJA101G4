let roomId = null;
let currentUserId = null;
let currentTargetId = null;
let matchList = [];
let currentIndex = 0;
const MATCH_API_BASE = "/api/match";

// ✅ 頁面一開始先從後端拿 currentUserId
fetch(`${MATCH_API_BASE}/currentUserId`)
	.then(res => res.json())
	.then(data => {
		currentUserId = data.currentUserId;
		console.log(data);
		console.log("✅ currentUserId 抓到了：", currentUserId);
		
		const urlParams = new URLSearchParams(window.location.search);
		const fromSuccess = urlParams.get("fromSuccess") === "1";

		// ✅ 如果不是從成功頁面跳回來（沒有參數 fromSuccess=1）→ 再確認是否需要清除
		if (!fromSuccess) {
		  localStorage.removeItem(`matchFilters_${currentUserId}`);
		  localStorage.removeItem(`matchedList_${currentUserId}`);
		}


		// ⏬ 把你原本的初始化邏輯、篩選綁定、按鈕綁定都放進來 ⏬
		startMatchPage(); // ← 你原本的主邏輯全寫這裡
	})


function startMatchPage() {
	console.log(localStorage);
	// nav 漢堡選單收合
	const hamburgerBtn = document.querySelector(".hamburger");
	const menuBody = document.querySelector(".header__nav");
	hamburgerBtn.addEventListener("click", () => {
		menuBody.classList.toggle('open');
	});

	// ✅ 頁面一開始，檢查是否有 localStorage 暫存的 matchList（來自 matchSuccess.html）
	const savedList = localStorage.getItem(`matchedList_${currentUserId}`);
	const savedFilters = localStorage.getItem(`matchFilters_${currentUserId}`);
	// 條件篩選按鈕
	const filterBtn = document.querySelector('.filterBtn');
	// 條件篩選彈出視窗
	const filterModal = document.getElementById('filterModal');
	// 套用篩選按鈕
	const applyFilterBtn = document.querySelector('.btn-primary');

	if (savedList && savedFilters) {
		matchList = JSON.parse(savedList);
		currentIndex = 0;

		if (matchList.length > 0) {
			renderMatchCard(matchList[currentIndex]);
		} else {
			// 預設隨機推薦（沒有條件篩選）
			fetch(`${MATCH_API_BASE}/getNext`)
				.then(res => res.json())
				.then(profile => {
					renderMatchCard(profile);
				})
				.catch(err => {
					console.error("初始化會員資料失敗", err);
				});
		}

		// ❌ 不清除 localStorage，這次要留下來繼續滑！
	} else {
		// 預設隨機推薦（沒有條件篩選）
		fetch(`${MATCH_API_BASE}/getNext`)
			.then(res => res.json())
			.then(profile => {
				renderMatchCard(profile);
			})
			.catch(err => {
				console.error("初始化會員資料失敗", err);
			});
	}
	// 點擊「條件篩選」打開視窗
	filterBtn.addEventListener('click', () => {
		filterModal.style.display = 'flex';  // 原本 CSS 應為 display: none
	});
	// 條件篩選視窗 按下確認套用的按鈕
	applyFilterBtn.addEventListener('click', () => {
		// 取得性別
		const gender = document.getElementById('genderSelect').value;

		// 取得勾選的興趣 (class: interest)
		const interests = Array.from(document.querySelectorAll('.interest:checked'))
			.map(input => input.value);

		// 取得勾選的人格特質 (class: personality)
		const personality = Array.from(document.querySelectorAll('.personality:checked'))
			.map(input => input.value);

		console.log('✅ 篩選資料送出：', { gender, interests, personality });
		// 送出後關掉視窗
		filterModal.style.display = 'none';


		// 👉 準備 payload（送出的資料）
		const payload = {
			action: "getFiltered",
			currentUserId: currentUserId, // 從你的 URL 或變數取得
			gender: gender,
			interests: interests,
			personality: personality
		};
		
		// 儲存篩選資料
		localStorage.setItem(`matchFilters_${currentUserId}`, JSON.stringify({
			gender,
			interests,
			personality
		}));

		// 👉 發送 POST 請求
		fetch(`${MATCH_API_BASE}/getFiltered`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(payload),
		})
			.then(res => {
				if (!res.ok) throw new Error("伺服器回應失敗");
				return res.json();
			})
			.then(data => {
				console.log("🎯 篩選結果：", data);
				matchList = data;
				currentIndex = 0;
				renderMatchCard(matchList[currentIndex]);

				// ✅ 儲存篩選資料與結果
				localStorage.setItem(`matchedList_${currentUserId}`, JSON.stringify(matchList));

			})
			.catch(err => {
				console.error("❌ 發送失敗：", err);
			});


	});

	// 點擊視窗外區域或取消按鈕會關閉篩選彈窗
	filterModal.addEventListener("click", function(event) {
		const isInsideModal = event.target.closest(".modal-content");
		const isCancelButton = event.target.classList.contains("btn-secondary");

		if (!isInsideModal || isCancelButton) {
			filterModal.style.display = "none";
		}
	});


	// 渲染會員卡片畫面
	function renderMatchCard(profile) {
		currentTargetId = profile.userId;

		const card = document.querySelector(".match__card");
		const swiperWrapper = card.querySelector(".swiper-wrapper");

		// ✅ 銷毀舊 Swiper（如有）
		const oldSwiper = card.querySelector(".avatar-swiper")?.swiper;
		if (oldSwiper) {
			oldSwiper.destroy(true, true);
		}

		// ✅ 清空 wrapper
		while (swiperWrapper.firstChild) {
			swiperWrapper.removeChild(swiperWrapper.firstChild);
		}

		// ✅ 建立內層卡片 DOM
		const slide = document.createElement("div");
		slide.className = "swiper-slide";

		const body = document.createElement("div");
		body.className = "match__body";

		const avatarBox = document.createElement("div");
		avatarBox.className = "match__avatar-box";

		const swiperContainer = document.createElement("div");
		swiperContainer.className = "swiper avatar-swiper";

		const avatarWrapper = document.createElement("div");
		avatarWrapper.className = "swiper-wrapper";

		// ✅ 建立頭貼輪播
		profile.avatarList.forEach(url => {
			const avatarSlide = document.createElement("div");
			avatarSlide.className = "swiper-slide";

			const img = document.createElement("img");
			img.className = "match__avatar";
			img.src = url;

			avatarSlide.appendChild(img);
			avatarWrapper.appendChild(avatarSlide);
		});

		const avatarPagination = document.createElement("div");
		avatarPagination.className = "swiper-pagination";

		swiperContainer.appendChild(avatarWrapper);
		swiperContainer.appendChild(avatarPagination);
		avatarBox.appendChild(swiperContainer);

		// ✅ 建立個人資料欄位
		const detailBox = document.createElement("div");
		detailBox.className = "match__details";

		const fields = [
			{ title: "人格特質", content: profile.personality },
			{ title: "興趣專長", content: profile.interests },
			{ title: "個人簡介", content: profile.intro },
		];

		fields.forEach(f => {
			const field = document.createElement("div");
			field.className = "match__field";

			const h3 = document.createElement("h3");
			h3.className = "match__field-title";
			h3.textContent = f.title;

			const p = document.createElement("p");
			p.className = "match__field-content";
			
			// 👇 加入 highlight 判斷
			if (f.title === "人格特質" || f.title === "興趣專長") {
				p.innerHTML = getHighlightedText(f.title, f.content);
			} else {
				p.textContent = f.content;
			}

			field.appendChild(h3);
			field.appendChild(p);
			detailBox.appendChild(field);
		});

		body.appendChild(avatarBox);
		body.appendChild(detailBox);
		slide.appendChild(body);
		swiperWrapper.appendChild(slide);

		// ✅ 名稱、年齡星座
		document.querySelector(".match__name").textContent = profile.username;
		document.querySelector(".match__info").textContent = `${profile.age}歲・${profile.zodiac}`;

		// ✅ 初始化 Swiper
		new Swiper(".avatar-swiper", {
			pagination: {
				el: ".avatar-swiper .swiper-pagination",
				clickable: true,
			},
			loop: true,
		});
	}

	// 這段 JS 專門處理按下 like 或 dislike 按鈕後的行為
	const likeBtn = document.querySelector(".match__button--like");
	const dislikeBtn = document.querySelector(".match__button--dislike");

	// 綁定按鈕點擊事件
	likeBtn.addEventListener("click", () => sendMatch("like"));
	dislikeBtn.addEventListener("click", () => sendMatch("dislike"));

	// 這個函式會由前端頁面在 renderMatchCard() 時設定 currentTargetId
	function setCurrentTargetId(id) {
		currentTargetId = id;
	}

	function sendMatch(action) {
		fetch(`${MATCH_API_BASE}/${action}`, {
			method: "POST",
			headers: {
				"Content-Type": "application/x-www-form-urlencoded",
			},
			body: `action=${action}&targetId=${currentTargetId}`,
		})
			.then((res) => res.json())
			.then((data) => {
				const wrap = document.querySelector(".match__wrap");
				wrap.classList.remove("fade-in", "fade-out");
				void wrap.offsetWidth;
				wrap.classList.add("fade-out");

				if (action === "like") {
					if (data.alreadyActed) {
						alert("⚠️ 你已經按過這個人囉");
					} else if (data.matched) {
						// ✅ 如果是配對成功 → 把目前這筆從 matchList 移除
						matchList.splice(currentIndex, 1);
						localStorage.setItem(`matchedList_${currentUserId}`, JSON.stringify(matchList));
						// 對方也按過你：跳轉成功配對頁面
						window.location.href = `matchSuccess.html?fromSuccess=1&roomId=${data.roomId}`;
						return; // 不切換下一位，直接跳頁
					}
				}

				// 👉 like 或 dislike：都要換下一張（除非成功配對已跳頁）
				// ✅ 移除當前這位使用者
				matchList.splice(currentIndex, 1);
				localStorage.setItem(`matchedList_${currentUserId}`, JSON.stringify(matchList));

				if (matchList.length > 0) {
					// ✅ 有剩下的卡片：從 matchList 顯示下一張
					renderMatchCard(matchList[currentIndex]);
					wrap.classList.remove("fade-out");
					void wrap.offsetWidth;
					wrap.classList.add("fade-in");
				} else {
					// 從後端抓下一位（若是隨機推薦的情況）
					fetch(`${MATCH_API_BASE}/getNext`)
						.then(res => res.json())
						.then(profile => {
							if (!profile || !profile.userId) {
								// 🧼 補充：避免殘影，清空畫面
								document.querySelector(".match__wrap").style.visibility = "hidden";
								throw new Error("後端沒資料");
							}
							// 正常情況
							renderMatchCard(profile);
							wrap.classList.remove("fade-out");
							void wrap.offsetWidth;
							wrap.classList.add("fade-in");
						})
						.catch(err => {
							console.error("❌ 沒有更多會員了", err);
							alert("你已經看完所有人啦！");
						});
				}
			})
	}
}

function goToChat() {
	const urlParams = new URLSearchParams(window.location.search);
	const roomId = urlParams.get("roomId");

	if (!roomId) {
		alert("❌ 無法取得 roomId");
		return;
	}

	fetch(`${MATCH_API_BASE}/currentUserId`)
		.then(res => res.json())
		.then(data => {
//			const currentUserId = data.currentUserId;
			window.location.href = `chatroom.html?roomId=${roomId}`;
		})
		.catch(() => alert("⚠️ 無法取得使用者 ID"));
}

function goToMatch() {
	fetch(`${MATCH_API_BASE}/currentUserId`)
		.then(res => res.json())
		.then(data => {
//			const currentUserId = data.currentUserId;
			window.location.href = `match.html?fromSuccess=1`;
		})
		.catch(() => alert("⚠️ 無法取得使用者 ID"));
}

// 符合條件篩選的條件加上highlight樣式
function getHighlightedText(title, content) {
	const filters = JSON.parse(localStorage.getItem(`matchFilters_${currentUserId}`));
	
	// 沒有篩選條件：一樣顯示、分隔，但不標記
	if (!filters) {
		return content.split(/[、,，]/).map(item => item.trim()).join('、');
	}

	let selected = [];

	// 根據欄位標題來決定要比對哪一個勾選條件
	if (title === "人格特質") {
		selected = filters.personality || [];
	} else if (title === "興趣專長") {
		selected = filters.interests || [];
	} else {
		return content; // 其他欄位直接原樣顯示
	}

	const items = content.split(/[、,，]/); // 處理不同分隔符
	return items.map(item => {
		return selected.includes(item.trim())
			? `<span class="highlight">${item.trim()}</span>`
			: item.trim();
	}).join('、'); // ✅ 改用「、」更自然
}

