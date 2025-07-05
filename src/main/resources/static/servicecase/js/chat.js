document.addEventListener('DOMContentLoaded', function () {
  let selectedCategoryId = null;
  let categories = [];

  const categoryList = document.getElementById('categoryList');
  const chatMessages = document.getElementById('chatMessages');
  const chatInput = document.getElementById('chatInput');
  const sendButton = document.getElementById('sendButton');
  const typingIndicator = document.getElementById('typingIndicator');

  // 載入並渲染類別
  fetch('/api/casetype') // '/shakemate/api/casetype'
    .then(res => res.json())
    .then(data => {
      categories = data;
      categories.forEach(cat => {
        const li = document.createElement('li');
        li.className = 'category-item';
        li.dataset.id = cat.caseTypeId;
        li.textContent = cat.typeName;
        categoryList.appendChild(li);

        li.addEventListener('click', () => {
          // 切換樣式
          document.querySelectorAll('.category-item').forEach(i => i.classList.remove('active'));
          li.classList.add('active');
          selectedCategoryId = cat.caseTypeId;

          // 啟用輸入
          chatInput.disabled = false;
          sendButton.disabled = false;
          chatInput.focus();

          // 清空聊天視窗（或保留歷史）
          chatMessages.innerHTML = '';
          addMessage(`您已選擇「${cat.typeName}」。請輸入問題：`, false);
        });
      });
    })
    .catch(err => console.error('載入類別失敗', err));

  sendButton.addEventListener('click', sendMessage);
  chatInput.addEventListener('keypress', e => {
    if (e.key === 'Enter') sendMessage();
  });

  function sendMessage() {
    const msg = chatInput.value.trim();
    if (!msg || !selectedCategoryId) return;

    addMessage(msg, true);
    chatInput.value = '';
    typingIndicator.classList.remove('hidden');

    // 呼叫 AI API
    fetch('/shakemate/api/ai/ask', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ categoryId: selectedCategoryId, question: msg })
    })
      .then(res => res.text())
      .then(text => {
        typingIndicator.classList.add('hidden');
        addMessage(text, false, true);
      })
      .catch(err => {
        typingIndicator.classList.add('hidden');
        console.error(err);
        addMessage('抱歉，回覆失敗，請稍後再試。', false);
      });
  }

  function addMessage(content, isUser, showFeedback = false) {
    const div = document.createElement('div');
    div.className = isUser ? 'message message-user' : 'message message-bot';
    div.innerHTML = `<p>${content.replace(/\n/g, '<br>')}</p>
  <span class="message-time">${new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>`;
    chatMessages.appendChild(div);

    if (showFeedback) {
      const feed = document.createElement('div');
      feed.className = 'feedback-container';
      feed.innerHTML = `
  <span>有幫助嗎？</span>
  <button class="feedback-button thumbs-up">👍</button>
  <button class="feedback-button thumbs-down">👎</button>
  `;
      div.appendChild(feed);

      // 找到時間元素
      const timeEl = div.querySelector('.message-time');

      // 點擊後將 feedback container 移除，並在 timeEl 之前插入感謝文字
      const thankSpan = document.createElement('span');
      thankSpan.className = 'feedback-thanks';
      thankSpan.textContent = '感謝您的回覆！';
      thankSpan.style.color = 'gray';
      thankSpan.style.marginRight = '8px'; // 與時間隔開一點

      // 綁定按鈕事件
      feed.querySelector('.thumbs-up').addEventListener('click', () => {
        alert('感謝您的正面回饋！'); // 你可以改為送出 API 等等
      });
      feed.querySelector('.thumbs-down').addEventListener('click', () => {
        alert('我們會持續改進，感謝您！');
      });


      // 點擊事件處理函式
      const handleFeedback = () => {
        feed.remove(); // 移除底下那整坨按鈕
        timeEl.parentNode.insertBefore(thankSpan, timeEl);
      };

      feed.querySelector('.thumbs-up').addEventListener('click', handleFeedback);
      feed.querySelector('.thumbs-down').addEventListener('click', handleFeedback);
    }
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }
});