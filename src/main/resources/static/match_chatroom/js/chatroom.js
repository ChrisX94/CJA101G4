//const urlParams = new URLSearchParams(window.location.search);
//const currentUserId = parseInt(urlParams.get("currentUserId")) || null;
//
//if (!currentUserId) {
//	alert("⚠️ 無法取得 currentUserId，請確認網址格式是否正確！");
//}

// ✅ 改用 Spring Boot API 路徑
const API_BASE = "/api/chat";

let currentUserId = null;
let currentRoomId = null;
let receiveId = null;
let selectedImageFile = null;

// 側邊選單
//function toggleSidebar() {
//	const wrapper = document.querySelector('.sidebar-wrapper');
//	const toggleBtn = document.getElementById('menuToggle');
//	wrapper.classList.toggle('active');
//	toggleBtn.classList.toggle('active');
//}



// 取得currentUserId
fetch(`${API_BASE}/currentUserId`)
  .then(res => {
    if (!res.ok) throw new Error("尚未登入");
    return res.json();
  })
  .then(data => {
    currentUserId = data.currentUserId;
	console.log(currentUserId);
	connectWebSocket(currentUserId);
  })
  .catch(err => {
    alert("⚠️ 請先登入");
    window.location.href = "login.html"; // 或其他導回首頁
  });

// 取得聊天室清單
function loadChatRooms() {
	fetch(`${API_BASE}/list`)
		.then(res => res.json())
		.then(data => {
			const ul = document.querySelector(".chat-list");
			ul.innerHTML = "";
			data.forEach(room => {
				console.log("room是啥呢",room);
				const li = document.createElement("li");
				li.classList.add("chat-item");
				li.dataset.roomid = room.roomId;
				li.dataset.peerid = room.peerId;
				li.dataset.name = room.peerName;
				li.dataset.avatar = room.peerAvatar;
				li.onclick = () => handleRoomClick(room.roomId, room.peerName, room.peerId, room.peerAvatar);

				let previewText;
				if (!room.lastMessage) {
					previewText = "開始跟抖友聊天吧！==";
				} else if (room.lastMessage.startsWith("image:")) {
					previewText = "[圖圖]";
				} else {
					previewText = room.lastMessage.slice(0, 10) + (room.lastMessage.length > 10 ? "..." : "");
				}
				// 判斷用最後訊息時間 or 建立時間
				const lastTime = room.lastSentTime || room.createdTime;
				const timeStr = lastTime ? formatTime(lastTime) : "";

				li.innerHTML = `
					  <img src="${room.peerAvatar}" onerror="this.src='img/default.jpg'" />
					  <div class="chat-list-txt">
					    <h3>${room.peerName}</h3>
					    <p class="chat-preview">${previewText}</p>
					  </div>
					  <div class="chat-time">${timeStr}</div>
					  <span class="unread-dot"></span>
					`;
				const dot = li.querySelector(".unread-dot");
				
				console.log("抓到沒",room.hasUnread);
				if (room.hasUnread && dot) {
					dot.classList.add("show");
				}
				ul.appendChild(li);
			});
		})
		.catch(error => {
			console.error("載入聊天室時出錯:", error);
		});
}
// 點擊聊天室後的處理
function handleRoomClick(roomId, peerName, peerId, peerAvatar) {
	receiveId = peerId;
	console.log("點到聊天室了，roomId:", roomId);
	// 告訴後端我點進聊天室了 → 把這間聊天室未讀訊息改成已讀
	fetch(`${API_BASE}/markAsRead`, {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		},
		body: `action=markAsRead&roomId=${roomId}`
	})
		.then(res => res.text())
		.then(data => {
			if (data === "success") {
				console.log("已讀狀態已更新！");
			} else {
				console.log("已讀狀態未更新！");
			}
		});
	switchChat(roomId);

	fetch(`${API_BASE}/messages?roomId=${roomId}`)
		.then(res => res.json())
		.then(data => {
			console.log("後端回應:", data);
			// ✅ 更新聊天室 bar 上的名字與頭貼
			document.querySelector(".chatroom__bar-avatar").src = peerAvatar || "img/default.jpg";
			document.querySelector(".chatroom__bar-name").textContent = peerName || "未知用戶";

			renderHistoryMessages(data, peerName, peerAvatar, peerId);
			
			document.querySelector(".chat-input").style.display = "flex";
			document.querySelector(".chatroom__bar").style.display = "block";
			const li = document.querySelector(`.chat-list li[data-roomid="${roomId}"]`);
			if (li) {
				const dot = li.querySelector(".unread-dot");
				if (dot) dot.classList.remove("show"); // ✅ 改用 class 移除
			}
			setTimeout(() => {
				const container = document.getElementById("chatContent");
				container.scrollTop = container.scrollHeight;
			}, 0); // 0毫秒即可，等 call stack 清空＋DOM 更新
	
			document.querySelector(".chatroom__bar-userInfo").onclick = () => {
							openProfile(peerId, peerName, peerAvatar);
						};
		})
		.catch(err => {
			console.error("取得訊息時出錯:", err);
		});
	
			
	if (socket && socket.readyState === WebSocket.OPEN) {
		socket.send(JSON.stringify({
		  type: "read",
		  roomId: currentRoomId,
		  senderId: currentUserId,
		  receiveId: receiveId
		}));
	}
		
}
// 切換聊天室選擇狀態，並將對應聊天室項目標記為 active（用於 UI 顯示目前選中的聊天室）
function switchChat(roomId) {
	currentRoomId = roomId;

	document.querySelectorAll('.chat-list li').forEach(li => {
		li.classList.remove('active');
		if (li.dataset.roomid == roomId) {
			li.classList.add('active');
		}
	});
}
// 傳送文字訊息
function sendMessage() {
	const input = document.getElementById("msgInput");
	const content = input.value.trim();
	// 沒輸入文字、也沒選圖片 → 不送出
	if (!content && !selectedImageFile) return;
	// 準備送出資料
	const formData = new FormData();
	formData.append("roomId", currentRoomId);
	formData.append("senderId", currentUserId);
	// 短路運算（Logical OR）如果 content 有值，就用它；
	// 如果是 null、undefined、空字串，就用 "" 代替。
	formData.append("content", content || "");

	// 寫入資料庫
	fetch(`${API_BASE}/send`,{ method: "POST", body: formData });
	// 即時顯示在畫面上
	const container = document.getElementById("chatContent");
	const timeStr = getTimeString(); // ex: 上午11:28

	// 純文字訊息，直接渲染
	const el = createMessageElement({
		isMe: true,
		content,
		timeStr
	});
	container.appendChild(el);
	container.scrollTop = container.scrollHeight;

	// 即時傳送文字訊息給對方
	if (socket && socket.readyState === WebSocket.OPEN) {
//		socket.send(`${currentRoomId}|${currentUserId}|${content}|${receiveId}`);
		socket.send(JSON.stringify({
			type: "text",
			roomId: currentRoomId,
			senderId: currentUserId,
			receiveId: receiveId,
			content
		}));
	}
	// 主動更新自己聊天室列表（preview + 時間）
	const li = document.querySelector(`.chat-list li[data-roomid="${currentRoomId}"]`);
	if (li) {
		li.querySelector(".chat-preview").textContent = content;
		li.querySelector(".chat-time").textContent = getTimeString(); // 使用你自己寫好的時間函式
		document.querySelector(".chat-list").prepend(li);

		li.classList.remove("fade-in");
		void li.offsetWidth;
		li.classList.add("fade-in");
	}
	// 清空輸入框與圖片
	input.value = "";
	selectedImageFile = null;
}
// 接收到即時訊息時渲染
function renderIncomingMessage(senderId, content = "", imageBase64 = null) {
	const isMe = senderId === currentUserId; // 判斷是否為自己發送的訊息
	const container = document.getElementById("chatContent");
	const timeStr = getTimeString(); // 當前時間字串（例如 上午11:30）
	// 🔍 若為對方發送 → 從聊天室清單找出對方資料
	let peerId = "";
	let peerName = "對方";
	let avatarUrl = "";

	if (!isMe) {
		// 從聊天室清單中，找出目前正開啟聊天室的 li
		const li = document.querySelector(`.chat-list li[data-roomid="${currentRoomId}"]`);
		if (li) {
			peerId = li.dataset.peerid;
			peerName = li.dataset.name || "對方";
			avatarUrl = li.querySelector("img")?.src || "";
		}
	}
	// ✅ 用共用函式建立 DOM 元素
	const el = createMessageElement({
		isMe,
		content,
		imageBase64,
		timeStr,
		avatarUrl,
		peerId,
		peerName
	});
	container.appendChild(el);
	container.scrollTop = container.scrollHeight;
}

// ✅ 顯示歷史訊息（進入聊天室後從資料庫撈出的訊息）
function renderHistoryMessages(data, peerName, peerAvatar, peerId) {
	const container = document.getElementById("chatContent");
	container.innerHTML = ''; // 清空原本訊息區

	data.forEach(msg => {
		const isMe = msg.senderId == currentUserId;
		// ✅ 避免後端送來 null 或空字串
		const content = (msg.content === "null" || !msg.content) ? "" : msg.content;
		const imageBase64 = msg.imgBase64 || null;
		const timeStr = formatTime(msg.sentTime);
		// ✅ 建立訊息 DOM 元素（共用函式）
		const el = createMessageElement({
			isMe,
			content,
			imageBase64,
			timeStr,
			avatarUrl: peerAvatar,
			peerId,
			peerName,
			isRead: msg.read
		});
		container.appendChild(el);
	});
	container.scrollTop = container.scrollHeight; // ✅ 滾到最底部
}

// 彈出會員介紹視窗：顯示對方資訊
function openProfile(peerId, peerName, avatarSrc) {
	console.log('openProfile 參數：',peerId, peerName);
	// 1. 取得所有要更新的 DOM 元素
	const popup = document.getElementById("profilePopup");
	const popupAvatar = document.getElementById("popupAvatar");
	const popupName = document.getElementById("popupName");
	const popupAge = document.getElementById("popupAge");
	const popupJob = document.getElementById("popupJob");
	const popupPersonality = document.getElementById("popupPersonality");
	const popupInterests = document.getElementById("popupInterests");
	const popupIntro = document.getElementById("popupIntro");
	// 3. 向後端抓更多使用者資訊
	fetch(`${API_BASE}/userProfile?peerId=${peerId}`)
		.then(res => res.json())
		.then(profile => {
			// ✅ 如果已經是 base64 或完整路徑，直接用
			// ✅ 名稱、年齡星座
//			popupAvatar.src = profile.avatarList[0];
			popupName.textContent = `${profile.username || '--'} `;
			popupAge.textContent = `${profile.age}歲・${profile.zodiac}`;
			popupPersonality.textContent = `${profile.personality || '--'}`;
			popupInterests.textContent = `${profile.interests || '--'}`;
			popupIntro.textContent = `${profile.intro || '--'}`;
			
			console.log("🎯 avatarList 是：", profile.avatarList);

			// 頭貼輪播渲染
			renderPopupAvatarSwiper(profile);
			// 4. 顯示彈窗
			popup.style.display = "flex";
		})
		.catch(err => {
			console.error("取得使用者資訊失敗：", err);
			// 即使失敗，也顯示彈窗，保留名字與先前頭像
			popup.style.display = "flex";
		});
	document.getElementById("profilePopup").addEventListener("click", function(event) {
		// 點到卡片本體 → 不關
		if (event.target.closest(".profile-card")) return;
		// 其它區域都關掉
		closeProfilePopup();
	});
}
// 關閉會員介紹視窗
function closeProfilePopup() {
	document.getElementById("profilePopup").style.display = "none";
}
// WebSocket
let socket = null;

function connectWebSocket(userId) {
	// ws: 等於 http 的 WebSocket
	socket = new WebSocket(`ws://${location.host}/chatSocket/${userId}`);

	socket.onopen = () => {
		console.log("✅ WebSocket 已連線");
	};
	socket.onmessage = (event) => {
		const data = JSON.parse(event.data);
		const { roomId, senderId, type, content, imgBase64 } = data;
		const li = document.querySelector(`.chat-list li[data-roomid="${roomId}"]`);
		setTimeout(() => {
			if (type === "read") {
				console.log("對方已讀了我的訊息");
				console.log("message.right 數量", document.querySelectorAll('.message.right').length);
				document.querySelectorAll('.message.right').forEach(msg => {
					const meta = msg.querySelector('.meta-info');
					if (meta && !meta.querySelector('.read-label')) {
						const readDiv = document.createElement('div');
						readDiv.className = 'read-label';
						readDiv.textContent = '已讀';
						const timeLabel = meta.querySelector('.time-label');
						meta.insertBefore(readDiv, timeLabel);
					}
				});
				return;
			}
		}, 300); 
		
		if (li) {
			// 更新 preview 文本
			const preview = li.querySelector("p");
//			preview.textContent = contentRaw.startsWith("image:") ? "[圖圖]" : contentRaw.slice(0, 10) + (contentRaw.length > 10 ? "..." : "");
			preview.textContent = type === "image" ? "[圖圖]" : content.slice(0, 10) + (content.length > 10 ? "..." : "");
			// 更新訊息傳送時間			
			const time = li.querySelector(".chat-time");
			if (time) {
				time.textContent = getTimeString(); // 或 formatTime(new Date())
			}
			// ✅ 如果這不是目前開啟的聊天室 → 顯示紅點
			if (parseInt(roomId) !== currentRoomId) {
				const dot = li.querySelector(".unread-dot");
				if (dot) {
					dot.classList.add("show");
					dot.style.display = ""; // ✅ 清除任何舊的 inline style
				}
			}
			// 移動 + 動畫
			document.querySelector(".chat-list").prepend(li); // 插到列表最上方
			li.classList.remove("fade-in"); // 移除舊動畫 class，準備重新播放動畫
			void li.offsetWidth; // 強制觸發瀏覽器 reflow（重新計算 layout）
			li.classList.add("fade-in"); // 重新加上動畫 class，達到「聊天室列表往上跳」的效果			
		}
		// ✅ 如果剛好正在看這個聊天室，就顯示訊息氣泡
		console.log(senderId);
		if (parseInt(roomId) === currentRoomId) {
			console.log("對方正在這間聊天室!");
			if (type === "image") {
				renderIncomingMessage(senderId, "", content);
			} else {
				renderIncomingMessage(senderId, content);
			}

			if (socket && socket.readyState === WebSocket.OPEN) {
				socket.send(JSON.stringify({
					type: "read",
					roomId: currentRoomId,
					senderId: currentUserId,
					receiveId: receiveId
				}));
			}
		}
	};
	socket.onclose = () => {
		console.log("🔴 WebSocket 已關閉");
	};
	socket.onerror = (error) => {
		console.error("❌ WebSocket 發生錯誤：", error);
	};
}

// 初始事件綁定
document.addEventListener('DOMContentLoaded', () => {
	document.querySelector(".chat-input").style.display = "None";
	document.querySelector(".chatroom__bar").style.display = "None";
	loadChatRooms();

	const input = document.getElementById("msgInput");
	input.addEventListener('keydown', (event) => {
		if (event.key === 'Enter') {
			event.preventDefault();
			sendMessage();
		}
	});
});
// 綁定點擊事件，點圖示時觸發檔案選擇
document.getElementById("uploadImageBtn").addEventListener("click", function() {
	document.getElementById("imageInput").click();
});
// 當選擇圖片後
document.getElementById("imageInput").addEventListener("change", function(event) {
	const file = event.target.files[0];
	if (!file) return;
	// 1. 顯示圖片（reader） ✅
	const reader = new FileReader();
	reader.onload = function(e) {
		const imageBase64 = e.target.result;

		const timeStr = getTimeString();

		// ✅ 統一用 createMessageElement，避免樣式不一致
		const el = createMessageElement({
			isMe: true,
			imageBase64: imageBase64,
			timeStr: timeStr
		});

		const container = document.getElementById("chatContent");
		container.appendChild(el);
		container.scrollTop = container.scrollHeight;

		// ✅ 傳 FormData 給後端儲存
		const formData = new FormData();
		formData.append("roomId", currentRoomId);
		formData.append("senderId", currentUserId);
		formData.append("content", "");
		formData.append("img", file);
		fetch(`${API_BASE}/send`, {
		  method: "POST",
		  body: formData
		});

		// ✅ 傳 WebSocket
		if (socket && socket.readyState === WebSocket.OPEN) {
			const msg = {
			  type: "image", // ✅ 訊息類型
			  roomId: currentRoomId,
			  senderId: currentUserId,
			  receiveId: receiveId,
			  content: imageBase64 // ✅ 直接是 base64 字串（不要再加 "image:" 前綴）
			};
			socket.send(JSON.stringify(msg));
		}
	};
	reader.readAsDataURL(file);

	// 主動更新自己聊天室列表（preview + 時間）
	const li = document.querySelector(`.chat-list li[data-roomid="${currentRoomId}"]`);
	if (li) {
		li.querySelector(".chat-preview").textContent = "[圖圖]";
		li.querySelector(".chat-time").textContent = getTimeString(); // 使用你自己寫好的時間函式
		document.querySelector(".chat-list").prepend(li);

		li.classList.remove("fade-in");
		void li.offsetWidth;
		li.classList.add("fade-in");
	}
});

// 將資料庫中的 sent_time 時間字串格式化為「時:分」的本地時間
function formatTime(dateTimeStr) {
	const date = new Date(dateTimeStr);
	return date.toLocaleTimeString("zh-TW", {
		hour: "2-digit",
		minute: "2-digit"
	});
}

function getTimeString() {
	return new Date().toLocaleTimeString("zh-TW", {
		hour: "2-digit",
		minute: "2-digit"
	});
}


// ✅ 建立訊息 DOM 元素（取代 innerHTML 寫死結構）
function createMessageElement({ isMe, content = "", imageBase64 = null, timeStr, avatarUrl = "", peerId = "", peerName = "", isRead = false }) {
	const el = document.createElement("div");
	el.className = "message " + (isMe ? "right" : "left");
	
	const bubbleBlock = document.createElement("div");
	bubbleBlock.className = "bubble-block";
	
	// ✅ 文字 or 圖片內容
	if (imageBase64) {
		const img = document.createElement("img");
		img.src = imageBase64;
		img.style.maxWidth = "200px";
		img.style.borderRadius = "8px";
		img.classList.add("chat-img");
		// ✅ 點圖片放大
		document.querySelector("#chatContent").addEventListener("click", function(e) {
			if (e.target.classList.contains("chat-img")) {
				// 使用者點到圖片了，做放大處理
				showImageModal(e.target.src);
			}
		});
		bubbleBlock.appendChild(img);
	} else {
		const bubble = document.createElement("div");
		bubble.className = "bubble";
		bubble.innerHTML = content.replaceAll(" ", "&nbsp;");
		bubbleBlock.appendChild(bubble);
	}

	// ✅ 時間字串
	const time = document.createElement("div");
	time.className = "time-label";
	time.textContent = timeStr;

	const meta = document.createElement("div");
	meta.className = "meta-info";
	
	// 判斷已讀
	if (isMe && isRead) {
		const read = document.createElement("span");
		read.className = "read-label";
		read.textContent = "已讀";
		meta.appendChild(read);
	}

	meta.appendChild(time);
	bubbleBlock.appendChild(meta);

	if (isMe) {
		// 👤 自己傳的訊息不需要頭像
		el.appendChild(bubbleBlock);
	} else {
		// 👤 對方傳的訊息，需加上頭像區塊
		const avatar = document.createElement("div");
		avatar.className = "avatar";
		avatar.onclick = () => openProfile(peerId, peerName, avatarUrl);

		const picWrapper = document.createElement("div");
		picWrapper.className = "pic";

		const img = document.createElement("img");
		img.src = avatarUrl;
		img.alt = peerName;
		img.onerror = () => (img.src = "img/default.jpg");

		picWrapper.appendChild(img);
		avatar.appendChild(picWrapper);

		el.appendChild(avatar);
		el.appendChild(bubbleBlock);
	}
	return el;
}

function showImageModal(src) {
	// 顯示前先移除舊的
	document.querySelectorAll(".img-modal").forEach(modal => modal.remove());

	const modal = document.createElement("div");
	modal.className = "img-modal";
	modal.innerHTML = `<img src="${src}" class="modal-img">`;
	modal.addEventListener("click", () => modal.remove());
	document.body.appendChild(modal);
}

// 點擊聊天室頂部選單的點點點按鈕
document.addEventListener("click", function(e) {
	const toggle = document.querySelector(".chatroom__options i");
	const menu = document.querySelector(".chatroom__bar-menu");

	if (toggle.contains(e.target)) {
		// 點擊點點點：開關選單
		menu.style.display = (menu.style.display === "block") ? "none" : "block";
	} else if (!menu.contains(e.target)) {
		// 點到其他地方：關閉選單
		menu.style.display = "none";
	}
});

// 渲染會員簡介彈窗卡牌的照片輪播
function renderPopupAvatarSwiper(profile) {
	console.log("🎯 avatarList 是：", profile.avatarList);
	const swiperWrapper = document.getElementById("popupAvatarWrapper");
	const swiperContainer = document.querySelector(".popup-avatar-swiper");

	// ✅ 銷毀舊 Swiper（如有）
	const oldSwiper = swiperContainer?.swiper;
	if (oldSwiper) {
		oldSwiper.destroy(true, true);
	}

	// ✅ 清空 wrapper 裡的舊圖片
	swiperWrapper.innerHTML = "";

	// ✅ 建立輪播圖片
	profile.avatarList.forEach(url => {
		const avatarSlide = document.createElement("div");
		avatarSlide.className = "swiper-slide";

		const img = document.createElement("img");
		img.className = "match__avatar"; // 保留原本樣式 class
		img.src = url;

		avatarSlide.appendChild(img);
		swiperWrapper.appendChild(avatarSlide);
	});

	// ✅ 初始化 Swiper（同配對頁設定）
	new Swiper(".popup-avatar-swiper", {
		pagination: {
			el: ".popup-avatar-swiper .swiper-pagination",
			clickable: true,
		},
		loop: true,
	});
}
