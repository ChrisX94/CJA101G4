--整理後表格
DROP DATABASE IF EXISTS shakemate;
CREATE DATABASE IF NOT EXISTS shakemate;
USE shakemate;

-------------------------------------------
DROP TABLE IF EXISTS Order_Detail;
DROP TABLE IF EXISTS ord;
DROP TABLE IF EXISTS SH_Order;
DROP TABLE IF EXISTS Member_Notification_Record;
DROP TABLE IF EXISTS Notification;
-------------------------------------------
DROP TABLE IF EXISTS SH_PROD_RP;
DROP TABLE IF EXISTS SH_PROD_PIC;
DROP TABLE IF EXISTS SH_PROD;
DROP TABLE IF EXISTS SH_PROD_TYPE;
-------------------------------------------
DROP TABLE IF EXISTS PROD_PIC;
DROP TABLE IF EXISTS PROD;
DROP TABLE IF EXISTS PROD_TYPE;
-----------------------------------------
DROP TABLE IF EXISTS USER_COUPON;
DROP TABLE IF EXISTS COUPON;
-------------------------------------------
DROP TABLE IF EXISTS servicecase;
DROP TABLE IF EXISTS casetype;
DROP TABLE IF EXISTS news;
DROP TABLE IF EXISTS newstype;
-------------------------------------------
DROP TABLE IF EXISTS POST_COMMENTS;
DROP TABLE IF EXISTS POST_REPORTS;
DROP TABLE IF EXISTS POST;
-------------------------------------------
DROP TABLE IF EXISTS ACTIVITY_REPORTS;
DROP TABLE IF EXISTS ACTIVITY_ANSWERS;
DROP TABLE IF EXISTS ACTIVITY_QUESTIONS;
DROP TABLE IF EXISTS ACTIVITY_PARTICIPANT;
DROP TABLE IF EXISTS ACTIVITY_COMMENTS;
DROP TABLE IF EXISTS ACTIVITY_TRACKING;
DROP TABLE IF EXISTS ACTIVITY;
-----------------------------------------
DROP TABLE IF EXISTS USER_RP;
DROP TABLE IF EXISTS CHAT_MESSAGE;
DROP TABLE IF EXISTS CHAT_ROOM;
DROP TABLE IF EXISTS USER_MATCHES;
-----------------------------------------
DROP TABLE IF EXISTS ADM_AUTH;
DROP TABLE IF EXISTS ADM;
DROP TABLE IF EXISTS AUTH_FUNC;
-----------------------------------------
DROP TABLE IF EXISTS USERS;
-----------------------------------------

CREATE TABLE IF NOT EXISTS USERS (
  USER_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  USERNAME VARCHAR(30) NOT NULL,
  EMAIL VARCHAR(30) NOT NULL UNIQUE,
  PWD VARCHAR(255) NOT NULL,
  GENDER TINYINT NOT NULL,
  BIRTHDAY DATE,
  LOCATION VARCHAR(50),
  INTRO VARCHAR(200),
  CREATED_TIME DATETIME NOT NULL,
  PROFILE_PICTURE VARCHAR(300),
  IMG2 VARCHAR(300),
  IMG3 VARCHAR(300),
  IMG4 VARCHAR(300),
  IMG5 VARCHAR(300),
  INTERESTS VARCHAR(300),
  PERSONALLY VARCHAR(300),
  UPDATED_TIME DATETIME NOT NULL,
  USER_STATUS TINYINT NOT NULL,
  POST_STATUS BOOLEAN NOT NULL DEFAULT TRUE,
  AT_AC_STATUS BOOLEAN NOT NULL DEFAULT TRUE,
  SELL_STATUS BOOLEAN NOT NULL DEFAULT TRUE
);
-- user datas
INSERT INTO shakemate.USERS VALUES (1, 'jasonLin88', 'jason88@mail.com', 'a9f7dfe426c836699ef3', 0, '1995-06-12', '台北市大安區', '喜歡運動和美食，希望認識熱愛生活的你。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', 'https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg', 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg', 'https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg', 'https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg', '打籃球,旅遊,烹飪', '外向,樂觀,開朗', '2025-05-18 15:00:00', 1, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (2, '小美', 'lovelyme@mail.com', '7fe87321c2b02a7f23b9', 1, '1993-11-22', '新北市板橋區', '喜歡拍照與閱讀，希望遇見談得來的朋友。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/1130626/pexels-photo-1130626.jpeg', 'https://images.pexels.com/photos/1043474/pexels-photo-1043474.jpeg', 'https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg', 'https://images.pexels.com/photos/1181686/pexels-photo-1181686.jpeg', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', '攝影,閱讀,散步', '文靜,細心,浪漫', '2025-05-18 15:00:00', 2, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (3, 'KevinChen2025', 'kevinc2025@mail.com', '3c18c163bbf6e33039b2', 0, '1990-04-03', '台中市西屯區', '一起去看海嗎？喜歡戶外活動和小動物。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg', 'https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', 'https://images.pexels.com/photos/356378/pexels-photo-356378.jpeg', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', '登山,潛水,烹飪', '熱情,勇敢,正直', '2025-05-18 15:00:00', 1, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (4, '愛笑的妤', 'smileyyu@mail.com', '6fba84f7ee47e2d0d97d', 1, '1996-09-10', '高雄市左營區', '我是一個熱愛生活、喜歡冒險的女生。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/937481/pexels-photo-937481.jpeg', 'https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg', 'https://images.pexels.com/photos/341884/pexels-photo-341884.jpeg', 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg', 'https://images.pexels.com/photos/2379175/pexels-photo-2379175.jpeg', '露營,聽音樂,追劇', '活潑,幽默,自信', '2025-05-18 15:00:00', 2, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (5, 'brianChang', 'bchang@mail.com', '605ef3961bbd01e58b14', 0, '1988-12-01', '桃園市中壢區', '找一個可以一起慢慢變老的伴。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/428364/pexels-photo-428364.jpeg', 'https://images.pexels.com/photos/428340/pexels-photo-428340.jpeg', 'https://images.pexels.com/photos/1468379/pexels-photo-1468379.jpeg', 'https://images.pexels.com/photos/936117/pexels-photo-936117.jpeg', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', '下棋,看電影,散步', '冷靜,理性,溫暖', '2025-05-18 15:00:00', 1, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (6, '吟風弄月', 'moonwind@mail.com', '178c3607eac0742ee936', 1, '1992-07-19', '新竹市東區', '喜歡聽音樂、寫詩，也想遇到能懂我的靈魂伴侶。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', 'https://images.pexels.com/photos/1603656/pexels-photo-1603656.jpeg', 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg', 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg', 'https://images.pexels.com/photos/1130626/pexels-photo-1130626.jpeg', '寫作,彈鋼琴,攝影', '感性,神秘,浪漫', '2025-05-18 15:00:00', 2, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (7, 'lucas_tw', 'lucas.tw@mail.com', '5ff738bc0f5a19320099', 0, '1994-10-05', '台南市永康區', 'IT業工程師，喜歡看Netflix與開發Side Project。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/709802/pexels-photo-709802.jpeg', 'https://images.pexels.com/photos/1704488/pexels-photo-1704488.jpeg', 'https://images.pexels.com/photos/1620760/pexels-photo-1620760.jpeg', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', 'https://images.pexels.com/photos/341884/pexels-photo-341884.jpeg', '寫程式,電玩,健身', '安靜,理性,專注', '2025-05-18 15:00:00', 1, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (8, '芯芯', 'heartshin@mail.com', '3c95b2dd72aa086318b2', 1, '1997-08-30', '台中市南屯區', '喜歡小動物、下廚、浪漫約會。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/709802/pexels-photo-709802.jpeg', 'https://images.pexels.com/photos/1181686/pexels-photo-1181686.jpeg', 'https://images.pexels.com/photos/1181686/pexels-photo-1181686.jpeg', 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg', 'https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg', '攝影,旅遊,追劇', '可愛,溫柔,黏人', '2025-05-18 15:00:00', 2, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (9, 'andykuo', 'andykuo88@mail.com', '59481bb5b3c246a0ed5f', 0, '1991-03-25', '新竹縣竹北市', '白天上班、晚上打球，生活簡單但充實。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/341884/pexels-photo-341884.jpeg', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', 'https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg', 'https://images.pexels.com/photos/1704488/pexels-photo-1704488.jpeg', 'https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg', '打籃球,投資,健身', '穩重,務實,陽光', '2025-05-18 15:00:00', 1, TRUE, TRUE, TRUE);
INSERT INTO shakemate.USERS VALUES (10, '美月Miduki', 'miduki.love@mail.com', 'b460e62835d50fc7191d', 1, '1995-05-18', '宜蘭縣羅東鎮', '在旅行中尋找靈感，也在等待那個願意陪我走世界的人。', '2025-05-18 15:00:00', 'https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg', 'https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg', 'https://images.pexels.com/photos/937481/pexels-photo-937481.jpeg', 'https://images.pexels.com/photos/709802/pexels-photo-709802.jpeg', 'https://images.pexels.com/photos/428364/pexels-photo-428364.jpeg', '旅遊,繪畫,品酒', '浪漫,自由,安靜', '2025-05-18 15:00:00', 2, TRUE, TRUE, TRUE);

    -- AUTH_FUNC 功能權限
CREATE TABLE IF NOT EXISTS AUTH_FUNC (
  AUTH_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  AUTH_NAME VARCHAR(30) NOT NULL,
  AUTH_DES VARCHAR(100) NOT NULL
);

INSERT INTO AUTH_FUNC (
        AUTH_ID, AUTH_NAME, AUTH_DES
    ) VALUES 
    (1, '管理員帳號管理', '管理所有管理員帳號與權限'),
	(2, '系統操作紀錄', '查看後台操作記錄'),
	(3, '會員帳號管理', '管理使用者帳號資訊包含會員檢舉'),
	(4, '留言/檢舉審核', '負責審核檢舉或留言內容'),
	(5, '活動管理', '管理平台活動'),
	(6, '商品管理', '商城與上架與折扣碼管理'),
	(7, '訂單處理', '處理商城與會員訂單'),
	(8, '二手商品管理', '二手商品審核與檢舉處理'),
    (9, '最新消息管理', '新增/刪除/修改最新消息');

-- ADM 系統管理員
CREATE TABLE IF NOT EXISTS ADM (
  ADM_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ADM_NAME VARCHAR(20) NOT NULL,
  ADM_ACC VARCHAR(20) NOT NULL,
  ADM_PWD VARCHAR(100) NOT NULL
);

INSERT INTO ADM (
        ADM_ID, ADM_NAME, ADM_ACC, ADM_PWD
    ) VALUES 
    (1, 'Alice', 'admin_alice', '66ee09cb24f0c6abf49e6bbdf823ee6df2debd9e2b4184aa240b9f0b9a50e5b0'),
	(2, 'Bob', 'admin_bob', 'c398548173264fe6e9fd553be9b8a5a1599b5c5d2a4d2f3b37f256a2ef9478dc'),
    (3, 'Cindy', 'admin_cindy', '46e2029c8e7640d988e9c4ea6bcf45f3c5023e0b0a64d51fc44df6ce28e8e90a'),
    (4, 'David', 'admin_david', 'b94c75d12dfae6d1ef9ac3e2f44dbe633f429b066d7f5c4f198ed92bce7fd349'),
	(5, 'Eva', 'admin_eva', '238b828fa93aa86d3b370f49e764d69a44bc940342bff7a728f1c8e0d89c0b7a');

-- ADM_AUTH權限對應
CREATE TABLE IF NOT EXISTS ADM_AUTH (
  AUTH_ID INT NOT NULL,
  ADM_ID INT NOT NULL,
  PRIMARY KEY (ADM_ID, AUTH_ID),
  FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID),
  FOREIGN KEY (AUTH_ID) REFERENCES AUTH_FUNC(AUTH_ID)
);

INSERT INTO ADM_AUTH (
            ADM_ID, AUTH_ID
) VALUES 
        (1, 1),
        (1, 2),
        (1, 3),
        (1, 8),
		(1, 9),
        (2, 1),
        (2, 2),
		(2, 3),
		(2, 8),
		(2, 9),
		(3, 4),
		(3, 5),
		(3, 9),
		(4, 6),
		(4, 7),
        (4, 9),
		(5, 6),
        (5, 7),
		(5, 9);

-- 配對紀錄
CREATE TABLE IF NOT EXISTS USER_MATCHES (
  MATCH_ID INT AUTO_INCREMENT PRIMARY KEY COMMENT '配對紀錄編號',
  ACTION_USER_ID INT NOT NULL COMMENT '行為會員編號（按下 like/dislike 的人）',
  TARGET_USER_ID INT NOT NULL COMMENT '被選擇會員編號（被按的人）',
  ACTION_TYPE TINYINT NOT NULL COMMENT '配對行為類型：0=like、1=dislike、2=matched',
  ACTION_TIME DATETIME NOT NULL COMMENT '執行這個動作的時間',
  CONSTRAINT UC_MATCH_ACTION UNIQUE (ACTION_USER_ID, TARGET_USER_ID, ACTION_TYPE),
  CONSTRAINT FK_ACTION_USER FOREIGN KEY (ACTION_USER_ID) REFERENCES USERS(USER_ID),
  CONSTRAINT FK_TARGET_USER FOREIGN KEY (TARGET_USER_ID) REFERENCES USERS(USER_ID)
);
INSERT INTO USER_MATCHES (ACTION_USER_ID, TARGET_USER_ID, ACTION_TYPE, ACTION_TIME) VALUES
(4, 10, 0, '2025-05-18 17:46:00'),
(10, 4, 0, '2025-05-18 16:14:00'),
(10, 4, 2, '2025-05-18 16:44:00'),
(3, 4, 0, '2025-05-18 16:38:00'),
(4, 3, 0, '2025-05-18 18:01:00'),
(4, 3, 2, '2025-05-18 17:27:00'),
(5, 8, 0, '2025-05-18 16:01:00'),
(8, 5, 0, '2025-05-18 18:55:00'),
(8, 5, 2, '2025-05-18 17:46:00'),
(3, 10, 0, '2025-05-18 18:35:00'),
(10, 3, 0, '2025-05-18 18:34:00'),
(10, 3, 2, '2025-05-18 17:04:00'),
(5, 7, 0, '2025-05-18 16:47:00'),
(7, 5, 0, '2025-05-18 18:58:00'),
(7, 5, 2, '2025-05-18 17:42:00'),
(1, 4, 0, '2025-05-18 18:45:00'),
(4, 1, 0, '2025-05-18 16:24:00'),
(4, 1, 2, '2025-05-18 17:27:00'),
(2, 9, 0, '2025-05-18 17:49:00'),
(9, 2, 0, '2025-05-18 16:25:00'),
(9, 2, 2, '2025-05-18 16:25:00'),
(5, 10, 0, '2025-05-18 16:22:00'),
(10, 5, 0, '2025-05-18 17:34:00'),
(10, 5, 2, '2025-05-18 16:28:00'),
(6, 10, 0, '2025-05-18 18:32:00'),
(10, 6, 0, '2025-05-18 16:02:00'),
(10, 6, 2, '2025-05-18 18:47:00'),
(5, 6, 0, '2025-05-18 17:03:00'),
(6, 5, 0, '2025-05-18 16:24:00'),
(6, 5, 2, '2025-05-18 16:05:00'),
(1, 4, 1, '2025-05-18 17:13:00'),
(1, 7, 1, '2025-05-18 16:29:00'),
(2, 10, 1, '2025-05-18 18:19:00'),
(2, 3, 1, '2025-05-18 16:33:00'),
(3, 1, 1, '2025-05-18 18:29:00'),
(3, 8, 0, '2025-05-18 17:29:00'),
(4, 6, 0, '2025-05-18 17:28:00'),
(4, 5, 1, '2025-05-18 16:12:00'),
(5, 1, 1, '2025-05-18 16:17:00'),
(6, 1, 0, '2025-05-18 16:29:00'),
(6, 3, 1, '2025-05-18 17:55:00'),
(7, 6, 0, '2025-05-18 16:59:00'),
(7, 8, 1, '2025-05-18 17:47:00'),
(8, 2, 0, '2025-05-18 17:09:00'),
(9, 8, 1, '2025-05-18 16:58:00'),
(9, 6, 0, '2025-05-18 16:22:00'),
(10, 9, 1, '2025-05-18 16:25:00'),
(10, 5, 1, '2025-05-18 17:01:00');

-- 聊天室
CREATE TABLE IF NOT EXISTS CHAT_ROOM (
  ROOM_ID INT AUTO_INCREMENT PRIMARY KEY COMMENT '聊天室編號',
  USER1_ID INT NOT NULL COMMENT '會員A編號（編號較小者）',
  USER2_ID INT NOT NULL COMMENT '會員B編號（編號較大者）',
  MATCH_ID INT NOT NULL COMMENT '配對成功對應的MATCH紀錄ID',
  CREATED_TIME DATETIME NOT NULL COMMENT '聊天室建立時間',
  ROOM_STATUS BOOLEAN NOT NULL DEFAULT 0 COMMENT '聊天室狀態：0=顯示、1=不顯示' default 0,

  -- 外鍵設定
  CONSTRAINT FK_USER1 FOREIGN KEY (USER1_ID) REFERENCES USERS(USER_ID),
  CONSTRAINT FK_USER2 FOREIGN KEY (USER2_ID) REFERENCES USERS(USER_ID),
  CONSTRAINT FK_MATCH FOREIGN KEY (MATCH_ID) REFERENCES USER_MATCHES(MATCH_ID)
);

INSERT INTO CHAT_ROOM (USER1_ID, USER2_ID, MATCH_ID, CREATED_TIME, ROOM_STATUS) VALUES 
(4, 10, 3, '2025-05-18 16:44:00', 0),
(3, 4, 6, '2025-05-18 17:27:00', 0),
(5, 8, 9, '2025-05-18 17:46:00', 0),
(3, 10, 12, '2025-05-18 17:04:00', 0),
(5, 7, 15, '2025-05-18 17:42:00', 0),
(1, 4, 18, '2025-05-18 17:27:00', 0),
(2, 9, 21, '2025-05-18 16:25:00', 0),
(5, 9, 24, '2025-05-18 16:28:00', 0),
(6, 10, 27, '2025-05-18 18:47:00', 0),
(5, 6, 30, '2025-05-18 16:05:00', 0);


CREATE TABLE IF NOT EXISTS CHAT_MESSAGE (
  MESSAGE_ID INT AUTO_INCREMENT PRIMARY KEY COMMENT '訊息編號',
  ROOM_ID INT NOT NULL COMMENT '聊天室編號',
  SENDER_ID INT NOT NULL COMMENT '發送訊息的會員編號',
  MESSAGE_CONTENT VARCHAR(500) COMMENT '文字訊息內容',
  MESSAGE_IMG MEDIUMBLOB COMMENT '圖片訊息（可選）',
  SENT_TIME DATETIME NOT NULL COMMENT '發送時間',
  IS_READ BOOLEAN NOT NULL COMMENT '是否已讀：0=未讀，1=已讀' DEFAULT 0,

  -- 外來鍵設定
  CONSTRAINT FK_MSG_ROOM FOREIGN KEY (ROOM_ID) REFERENCES CHAT_ROOM(ROOM_ID),
  CONSTRAINT FK_MSG_SENDER FOREIGN KEY (SENDER_ID) REFERENCES USERS(USER_ID)
);

-- 假資料：CHAT_MESSAGE 表（10 間聊天室，約 40~50 則訊息）

INSERT INTO CHAT_MESSAGE (ROOM_ID, SENDER_ID, MESSAGE_CONTENT, MESSAGE_IMG, SENT_TIME, IS_READ) VALUES
 (4, 4, '很高興認識你～', NULL, '2025-05-18 17:33:00', 0),
(4, 10, '嗨你好！', NULL, '2025-05-18 16:21:00', 1),
(4, 4, '很高興認識你～', NULL, '2025-05-18 17:13:00', 0),
(4, 10, '有推薦的電影嗎？', NULL, '2025-05-18 16:42:00', 1),
(4, 4, '你住哪裡呢？', NULL, '2025-05-18 16:37:00', 1),
(3, 3, '晚安～', NULL, '2025-05-18 18:09:00', 1),
(3, 4, '有推薦的電影嗎？', NULL, '2025-05-18 16:02:00', 0),
(3, 3, '很高興認識你～', NULL, '2025-05-18 18:42:00', 0),
(3, 4, '有推薦的電影嗎？', NULL, '2025-05-18 17:16:00', 0),
(5, 5, '晚安～', NULL, '2025-05-18 18:20:00', 0),
(5, 8, '我們興趣很像耶', NULL, '2025-05-18 17:02:00', 1),
(5, 5, '嗨你好！', NULL, '2025-05-18 18:52:00', 0),
(5, 8, '嗨你好！', NULL, '2025-05-18 18:44:00', 0),
(3, 3, '你喜歡旅遊嗎？', NULL, '2025-05-18 16:56:00', 0),
(3, 10, '這週有空見面嗎？', NULL, '2025-05-18 17:56:00', 0),
(3, 3, '這週有空見面嗎？', NULL, '2025-05-18 18:45:00', 1),
(3, 10, '我們興趣很像耶', NULL, '2025-05-18 18:50:00', 0),
(3, 3, '下次再聊：）', NULL, '2025-05-18 17:08:00', 1),
(5, 5, '很高興認識你～', NULL, '2025-05-18 18:29:00', 0),
(5, 7, '你住哪裡呢？', NULL, '2025-05-18 17:52:00', 0),
(5, 5, '這週有空見面嗎？', NULL, '2025-05-18 16:33:00', 1),
(5, 7, '有推薦的電影嗎？', NULL, '2025-05-18 16:42:00', 0),
(1, 1, '你住哪裡呢？', NULL, '2025-05-18 16:23:00', 1),
(1, 4, '嗨你好！', NULL, '2025-05-18 16:16:00', 0),
(1, 1, '很高興認識你～', NULL, '2025-05-18 16:18:00', 1),
(1, 4, '哈哈哈哈', NULL, '2025-05-18 17:21:00', 0),
(1, 1, '有推薦的電影嗎？', NULL, '2025-05-18 18:14:00', 0),
(2, 2, '很高興認識你～', NULL, '2025-05-18 16:30:00', 1),
(2, 9, '哈哈哈哈', NULL, '2025-05-18 16:11:00', 1),
(2, 2, '晚安～', NULL, '2025-05-18 16:11:00', 0),
(2, 9, '嗨你好！', NULL, '2025-05-18 17:17:00', 0),
(2, 2, '哈哈哈哈', NULL, '2025-05-18 17:03:00', 0),
(5, 5, '哈哈哈哈', NULL, '2025-05-18 18:51:00', 1),
(5, 9, '很高興認識你～', NULL, '2025-05-18 17:33:00', 1),
(5, 5, '嗨你好！', NULL, '2025-05-18 17:31:00', 0),
(5, 9, '有推薦的電影嗎？', NULL, '2025-05-18 17:43:00', 0),
(5, 5, '有推薦的電影嗎？', NULL, '2025-05-18 16:19:00', 0),
(6, 6, '晚安～', NULL, '2025-05-18 18:51:00', 0),
(6, 10, '很高興認識你～', NULL, '2025-05-18 16:55:00', 0),
(6, 6, '你住哪裡呢？', NULL, '2025-05-18 18:55:00', 1),
(6, 10, '有推薦的電影嗎？', NULL, '2025-05-18 17:15:00', 1),
(5, 5, '下次再聊：）', NULL, '2025-05-18 18:06:00', 0),
(5, 6, '你喜歡旅遊嗎？', NULL, '2025-05-18 17:31:00', 1),
(5, 5, '你住哪裡呢？', NULL, '2025-05-18 18:21:00', 1),
(5, 6, '哈哈哈哈', NULL, '2025-05-18 16:51:00', 0);

-- 會員檢舉
CREATE TABLE IF NOT EXISTS USER_RP (
  RP_ID INT AUTO_INCREMENT PRIMARY KEY COMMENT '檢舉編號',

  REPORTER_ID INT NOT NULL COMMENT '檢舉人 USER_ID',
  USER_ID INT NOT NULL COMMENT '被檢舉人 USER_ID',

  RP_REASON TINYINT NOT NULL COMMENT '檢舉事由（0~7 對應前端選項）',
  RP_CONTENT VARCHAR(800) NOT NULL COMMENT '檢舉文字內容',
  RP_PIC LONGBLOB COMMENT '附圖（可選）',
  RP_TIME DATETIME NOT NULL COMMENT '檢舉時間',
  ADM_ID INT NOT NULL COMMENT '處理該筆檢舉的管理員 ID',
  RP_DONE_TIME DATETIME COMMENT '處理完成時間',
  RP_STATUS TINYINT NOT NULL DEFAULT 0 COMMENT '處理狀態（0=未處理，1=通過，2=不通過）',
  RP_NOTE VARCHAR(800) COMMENT '處理註記',
  -- 外來鍵關聯
  CONSTRAINT FK_RP_REPORTER FOREIGN KEY (REPORTER_ID) REFERENCES USERS(USER_ID),
  CONSTRAINT FK_RP_TARGET FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
  -- CONSTRAINT FK_RP_ADMIN FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID)
);

-- USER_RP 假資料（三筆檢舉紀錄）

INSERT INTO USER_RP (REPORTER_ID, USER_ID, RP_REASON, RP_CONTENT, RP_PIC, RP_TIME, ADM_ID, RP_DONE_TIME, RP_STATUS, RP_NOTE) VALUES 
(6, 4, 6, '對方傳送騷擾訊息，內容令人不適。', NULL, '2025-05-18 17:54:00', 1, '2025-05-18 17:36:00', 2, '內容不足，暫不採納。'),
(8, 2, 0, '頭貼疑似裸露照片，請盡快處理。', NULL, '2025-05-18 18:57:00', 1, '2025-05-18 19:11:00', 1, '已審核完畢，依規定處理。'),
(5, 2, 4, '自我介紹帶有推銷意味，懷疑是詐騙帳號。', NULL, '2025-05-18 18:23:00', 2, '2025-05-18 18:05:00', 2, '內容不足，暫不採納。');


-- 商品分類
CREATE TABLE IF NOT EXISTS PROD_TYPE (
  PROD_TYPE_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  PROD_TYPE_NAME VARCHAR(50) NOT NULL
);

-- 插入分類資料
INSERT INTO PROD_TYPE ( PROD_TYPE_NAME) VALUES
  ('送禮／情人節／告白專用商品'),
  (' 香氛／氣氛營造商品'),
  ('自我形象提升／交友實用工具'),
  ('療癒／收藏型商品');
  
-- 建立商品表
CREATE TABLE IF NOT EXISTS PROD (
  PROD_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  PROD_NAME VARCHAR(30) NOT NULL,
  PROD_TYPE_ID INT NOT NULL,
  PROD_CONTENT VARCHAR(800),
  PRODUCT_DESCRIBE VARCHAR(800),
  PROD_PRICE INT NOT NULL,
  PROD_BRAND VARCHAR(50) NOT NULL,
  PROD_SOLD INT NOT NULL DEFAULT 0,
  PROD_RATE_SUM INT NOT NULL DEFAULT 0,
  PROD_RATE_COUNT_SUM INT NOT NULL DEFAULT 0,
  PROD_VIEWS INT NOT NULL DEFAULT 0,
  PROD_REG_TIME DATETIME NOT NULL,
  PROD_STATUS TINYINT NOT NULL DEFAULT 0,
  FOREIGN KEY (PROD_TYPE_ID) REFERENCES PROD_TYPE(PROD_TYPE_ID)
);
INSERT INTO PROD (
        PROD_NAME, PROD_TYPE_ID, PROD_CONTENT, PRODUCT_DESCRIBE,
        PROD_PRICE, PROD_BRAND, PROD_SOLD, PROD_RATE_SUM, PROD_RATE_COUNT_SUM,
        PROD_VIEWS, PROD_REG_TIME, PROD_STATUS) VALUES 
    ('永生花禮盒', 1, '經典浪漫不凋謝花束，附燈飾與禮盒包裝', '戀人首選永生花禮盒，浪漫與長情的象徵',1480, 'LoveBloom', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
	('情侶對杯', 1, '可愛情侶圖案馬克杯，一對販售', '送禮首選，讓你們擁有專屬的杯子', 699, 'CoupleMug', 0, 0, 0,0, '2025-05-18 13:56:13', 0),
    ('情侶對戒', 1, '簡約風對戒，銀色款，尺寸可調', '象徵彼此的承諾與連結',1299, 'PromiseRing', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('驚喜盒子', 1, '隨機出貨浪漫小物、零食、告白卡', '戀愛驚喜包，適合曖昧階段送出', 499, 'SurpriseYou', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('香氛蠟燭', 2, '天然精油蠟燭，多款香味可選', '讓你的房間充滿香氣與情調', 780, 'AromaLite', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('星空投影燈', 2, '投影夜空圖樣，氣氛滿分', '視訊、見面都能創造浪漫空間', 1280, 'SkyMood', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('擴香瓶', 2, '藤枝擴香瓶，高質感包裝', '居家與辦公環境香氛首選', 890, 'Fragrance+', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('自拍補光燈', 3, 'USB 充電，三段亮度調節', '讓你的大頭貼更上相', 320, 'BrightMe', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('小型三腳架', 3, '手機支架，可360度旋轉', '適合自拍或視訊固定角度', 450, 'FlexiPod', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('穿搭指南手冊', 3, '男女皆適用的約會穿搭建議本', '幫你第一次見面留下好印象', 180, 'StyleUp', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('吉伊卡哇娃娃', 4, '超人氣角色娃娃，柔軟材質', '可愛又療癒，讓人一見傾心', 690, 'LINE FRIENDS', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('姓名吊飾', 4, '壓克力姓名牌，可自訂名字', '情侶對飾推薦，自用收藏皆宜', 299, 'NameMe', 0, 0, 0, 0, '2025-05-18 13:56:13', 0),
    ('情侶任務本', 4, '收錄52個任務，鼓勵一起完成', '給正在交往的你們更多互動與儀式感',399, 'GrowTogether', 0, 0, 0, 0, '2025-05-18 13:56:13', 0);

-- 建立商品圖片表(先用空值)
CREATE TABLE IF NOT EXISTS PROD_PIC (
  PROD_PIC_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  PROD_ID INT NOT NULL,
  PROD_PIC LONGBLOB,
  FOREIGN KEY (PROD_ID) REFERENCES PROD(PROD_ID)
);


CREATE TABLE IF NOT EXISTS COUPON (
  COUPON_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  COUPON_CODE VARCHAR(50) NOT NULL,
  COUPON_CONTENT VARCHAR(800) NOT NULL,
  SPEND_OVER INT NOT NULL,
  COUPON_START DATETIME NOT NULL,
  COUPON_END DATETIME NOT NULL,
  DISCOUNT_AMOUNT INT NOT NULL
);

INSERT INTO COUPON (
         COUPON_CODE, COUPON_CONTENT, SPEND_OVER, COUPON_START, COUPON_END, DISCOUNT_AMOUNT) VALUES 
    ('LOVE100', '新會員折扣 NT$100', 500, '2025-05-19 00:00:00', '2025-06-18 23:59:59', 100),
    ('VALENTINE20', '情人節滿千折 NT$200', 1000, '2025-05-19 00:00:00', '2025-06-18 23:59:59', 200),
    ('FREESHIP', '滿599免運費折抵 NT$60', 599, '2025-05-19 00:00:00', '2025-06-18 23:59:59', 60),
    ('COUPLE520', '情侶對飾專用折扣 NT$150', 799, '2025-05-19 00:00:00', '2025-06-18 23:59:59', 150),
    ('FIRSTBUY', '首購限定滿800折 NT$180', 800, '2025-05-19 00:00:00', '2025-06-18 23:59:59', 180);

CREATE TABLE IF NOT EXISTS USER_COUPON (
  USER_ID INT NOT NULL,
  COUPON_ID INT NOT NULL,
  USED_FLAG TINYINT NOT NULL DEFAULT 0, -- 0:未使用, 1:已使用, 2:失效
  REDEEM_DATE DATETIME,
  PRIMARY KEY (USER_ID, COUPON_ID),
  FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
  FOREIGN KEY (COUPON_ID) REFERENCES COUPON(COUPON_ID)
);

INSERT INTO USER_COUPON (
        USER_ID, COUPON_ID, USED_FLAG, REDEEM_DATE) VALUES 
    (1, 1, 0, NULL),
    (1, 2, 1, '2025-05-19 03:48:34'),
    (2, 1, 0, NULL),
    (3, 3, 1, '2025-05-19 03:48:34'),
    (4, 4, 0, NULL),
    (5, 5, 0, NULL),
    (6, 2, 1, '2025-05-19 03:48:34'),
    (6, 5, 0, NULL);

-- SH_PROD_TYPE
CREATE TABLE IF NOT EXISTS SH_PROD_TYPE (
  PROD_TYPE_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  PROD_TYPE_NAME VARCHAR(50) NOT NULL
);

INSERT INTO SH_PROD_TYPE (PROD_TYPE_NAME) VALUES 
('3C產品'),
('服飾配件'),
('生活用品'),
('書籍文具'),
('運動戶外'),
('美妝保養');

-- SH_PROD
CREATE TABLE IF NOT EXISTS SH_PROD (
  PROD_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  USER_ID INT NOT NULL,
  PROD_NAME VARCHAR(30) NOT NULL,
  PROD_TYPE_ID INT NOT NULL,
  PROD_CONTENT VARCHAR(800) NOT NULL,
  PRODUCT_STATUS_DESCRIBE VARCHAR(800) NOT NULL,
  PROD_PRICE INT NOT NULL,
  PROD_BRAND VARCHAR(50),
  PROD_COUNT INT NOT NULL,
  PROD_VIEWS INT NOT NULL DEFAULT 0,
  PROD_STATUS TINYINT NOT NULL,
  IsSold BOOLEAN NOT NULL,
  PROD_REG_TIME DATETIME NOT NULL,
  UPDATED_TIME DATETIME NOT NULL,
  FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
  FOREIGN KEY (PROD_TYPE_ID) REFERENCES SH_PROD_TYPE(PROD_TYPE_ID)
);

INSERT INTO SH_PROD (
            USER_ID, PROD_NAME, PROD_TYPE_ID, PROD_CONTENT, PRODUCT_STATUS_DESCRIBE,
            PROD_PRICE, PROD_BRAND, PROD_COUNT, PROD_VIEWS, PROD_STATUS, IsSold, PROD_REG_TIME, UPDATED_TIME
        ) VALUES 
        (8, '二手物品1', 5, '二手物品1 出清，保存狀況良好。', '二手物品1 僅輕微使用痕跡，無功能損壞。', 805, 'ASUS', 1, 17, 2, TRUE, '2025-05-19 04:43:21', '2025-05-19 04:43:21'), 
        (3, '二手物品2', 6, '二手物品2 出清，保存狀況良好。', '二手物品2 僅輕微使用痕跡，無功能損壞。', 247, 'Nike', 2, 49, 2, FALSE, '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
        (3, '二手物品3', 1, '二手物品3 出清，保存狀況良好。', '二手物品3 僅輕微使用痕跡，無功能損壞。', 276, 'ASUS', 3, 27, 2, TRUE, '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
        (1, '二手物品4', 2, '二手物品4 出清，保存狀況良好。', '二手物品4 僅輕微使用痕跡，無功能損壞。', 175, 'Logitech', 2, 10, 2, TRUE, '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
        (3, '二手物品5', 1, '二手物品5 出清，保存狀況良好。', '二手物品5 僅輕微使用痕跡，無功能損壞。', 808, 'Nike', 3, 33, 2,TRUE, '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
        (1, '二手物品6', 4, '二手物品6 出清，保存狀況良好。', '二手物品6 僅輕微使用痕跡，無功能損壞。', 903, 'ASUS', 3, 4, 2, TRUE, '2025-05-19 04:43:21', '2025-05-19 04:43:21');

-- SH_PROD_PIC
CREATE TABLE IF NOT EXISTS SH_PROD_PIC (
  PROD_PIC_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  PROD_ID INT NOT NULL,
  PROD_PIC VARCHAR(300),
  FOREIGN KEY (PROD_ID) REFERENCES SH_PROD(PROD_ID)
);

INSERT INTO SH_PROD_PIC (PROD_ID, PROD_PIC)
			VALUES 
            (1, 'https://images.pexels.com/photos/1004014/pexels-photo-1004014.jpeg'),
			(2, 'https://images.pexels.com/photos/1386604/pexels-photo-1386604.jpeg'),
			(3, 'https://images.pexels.com/photos/374074/pexels-photo-374074.jpeg'),
			(4, 'https://images.pexels.com/photos/461198/pexels-photo-461198.jpeg'),
			(5, 'https://images.pexels.com/photos/593655/pexels-photo-593655.jpeg'),
			(6, 'https://images.pexels.com/photos/207983/pexels-photo-207983.jpeg');

-- SH_PROD_RP
CREATE TABLE IF NOT EXISTS SH_PROD_RP (
  PROD_RP_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  USER_ID INT NOT NULL,
  PROD_ID INT NOT NULL,
  PROD_RP_REASON TINYINT NOT NULL,
  PROD_RP_CONTENT VARCHAR(800) NOT NULL,
  PROD_RP_PIC LONGBLOB,
  PROD_RP_TIME DATETIME NOT NULL,
  ADM_ID INT,
  PROD_RP_DONE_TIME DATETIME,
  PROD_RP_STATUS TINYINT NOT NULL,
  PROD_RP_NOTE VARCHAR(800),
  FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
  FOREIGN KEY (PROD_ID) REFERENCES SH_PROD(PROD_ID),
  FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID)
);

INSERT INTO SH_PROD_RP (
        USER_ID, PROD_ID, PROD_RP_REASON, PROD_RP_CONTENT, PROD_RP_PIC, PROD_RP_TIME, ADM_ID, PROD_RP_DONE_TIME, PROD_RP_STATUS, PROD_RP_NOTE
    ) VALUES 
    (2, 3, 2, '圖片與文字完全與其他賣家相同，疑似抄襲', NULL, '2025-05-19 04:43:21', 1, NULL, 0, '待審查');
    

-- 客服類別表
CREATE TABLE IF NOT EXISTS casetype(
	CASE_TYPE_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    TYPE_NAME VARCHAR(50) NOT NULL,
    TYPE_DESC VARCHAR(100) NOT NULL
);

-- 插入案件類別資料 (10筆)
INSERT INTO casetype (TYPE_NAME, TYPE_DESC) VALUES 
('產品使用', '產品功能使用、設定、操作指南等相關問題'),
('訂單查詢', '訂單狀態、物流、退換貨等相關問題'),
('帳戶問題', '帳號註冊、登入、安全、隱私等相關問題'),
('付款問題', '支付方式、發票、退款等相關問題'),
('技術支援', '系統錯誤、連接問題、軟體更新等相關問題'),
('活動諮詢', '平台活動、優惠券使用、積分兌換等相關問題'),
('會員權益', '會員等級、權益、福利等相關問題'),
('平台建議', '使用者體驗、界面設計、功能建議等相關問題'),
('商品諮詢', '商品資訊、規格、庫存等相關問題'),
('其他問題', '其他未列類別的客服問題');

-- 案件表
CREATE TABLE IF NOT EXISTS servicecase(
    CASE_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    USER_ID INT NOT NULL,
    ADM_ID INT NOT NULL,
    CASE_TYPE_ID INT NOT NULL,
    CREATE_TIME DATETIME,
    UPDATE_TIME DATETIME,
    TITLE VARCHAR(100) NOT NULL,
    CONTENT VARCHAR(800) NOT NULL,
    CASE_STATUS TINYINT default 0 NOT NULL,
	FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
	FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID),
	FOREIGN KEY (CASE_TYPE_ID) REFERENCES CASETYPE(CASE_TYPE_ID)
);

-- 插入客服案件資料 (10筆)
INSERT INTO servicecase (USER_ID, ADM_ID, CASE_TYPE_ID, CREATE_TIME, UPDATE_TIME, TITLE, CONTENT, CASE_STATUS) VALUES 
(1, 1, 1, '2025-05-01 09:15:00', '2025-05-01 14:30:00', '如何更改個人資料', '我無法在個人頁面找到修改生日的選項，請問要如何更改？', 2),
(2, 2, 2, '2025-05-02 10:20:00', '2025-05-02 11:45:00', '訂單#12345尚未收到', '我的訂單已顯示送達，但實際上還沒收到商品', 1),
(3, 3, 3, '2025-05-03 13:30:00', '2025-05-03 15:10:00', '無法登入帳戶', '輸入正確密碼後系統仍顯示錯誤，請協助處理', 0),
(4, 4, 4, '2025-05-04 16:45:00', '2025-05-04 17:20:00', '如何申請退款', '想取消最近的訂單並申請退款，請問流程為何？', 2),
(5, 5, 5, '2025-05-05 08:30:00', '2025-05-05 09:45:00', 'APP閃退問題', '最新版本的APP使用時頻繁閃退，請問有解決方法嗎？', 1),
(6, 1, 6, '2025-05-06 11:20:00', '2025-05-06 14:15:00', '會員日活動詳情', '請問下次會員日何時舉辦？有哪些優惠？', 2),
(7, 2, 7, '2025-05-07 09:40:00', '2025-05-07 10:30:00', '金卡會員權益查詢', '我最近升級為金卡會員，想了解有哪些專屬權益', 0),
(8, 3, 8, '2025-05-08 15:25:00', '2025-05-08 16:50:00', '購物車功能建議', '建議購物車能保存更長時間，目前24小時後就會清空', 1),
(9, 4, 9, '2025-05-09 13:10:00', '2025-05-09 14:30:00', '產品尺寸諮詢', '網站上的XL尺寸對應的實際肩寬和胸圍是多少？', 2),
(10, 5, 10, '2025-05-10 10:05:00', '2025-05-10 11:20:00', '無法收到驗證碼', '嘗試註冊新帳號但手機收不到驗證簡訊，請協助處理', 0);


-- 最新消息
-- 類別表
CREATE TABLE IF NOT EXISTS newstype (
	CATEGORY_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    CATEGORY_NAME VARCHAR(50) NOT NULL,
    NEWS_DESC VARCHAR(100) NOT NULL
);

-- 插入消息類別資料 (10筆)
INSERT INTO newstype (CATEGORY_NAME, NEWS_DESC) VALUES 
('系統公告', '平台系統更新、維護、變更等相關公告'),
('活動訊息', '促銷活動、節日特惠、限時優惠等相關訊息'),
('新品上架', '新產品上架、新功能推出等相關訊息'),
('會員通知', '會員權益、等級變更、積分相關等通知'),
('使用教學', '平台功能、服務使用教學等相關資訊'),
('隱私政策', '隱私權政策、使用條款等重要資訊更新'),
('合作夥伴', '新增合作品牌、商家等合作訊息'),
('社區活動', '線上、線下社區活動相關訊息'),
('媒體報導', '平台相關媒體報導、新聞等訊息'),
('周年慶典', '平台周年慶相關活動與優惠訊息');

-- 消息表
CREATE TABLE IF NOT EXISTS news(
    NEWS_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    CATEGORY_ID INT NOT NULL, 
    TITLE VARCHAR(100)  NOT NULL,
    CONTENT VARCHAR(800) NOT NULL, 
    PUBLISH_TIME DATETIME,
    ADM_ID INT NOT NULL,
    NEWS_STATUS BOOLEAN NOT NULL default FALSE,
	FOREIGN KEY (CATEGORY_ID) REFERENCES NEWSTYPE(CATEGORY_ID),
    FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID)
);

-- 插入最新消息資料 (10筆)
INSERT INTO news (CATEGORY_ID, TITLE, CONTENT, PUBLISH_TIME, ADM_ID, NEWS_STATUS) VALUES 
(1, '系統升級通知', '為提供更好的服務體驗，系統將於5月20日凌晨2:00-6:00進行升級維護，期間可能無法使用部分功能。', '2025-05-15 10:00:00', 1, TRUE),
(2, '五月限定優惠開跑', '歡慶五月天，全站商品限時9折，會員另享額外5%回饋，活動期間：5月15日至5月31日。', '2025-05-14 09:30:00', 2, TRUE),
(3, '夏季新品系列上架', '2025夏季新品系列「清涼一夏」正式上架，首購享88折優惠，限量商品售完為止。', '2025-05-13 14:00:00', 3, TRUE),
(4, '會員積分調整通知', '自6月1日起，會員積分計算方式將進行調整，消費每100元可獲得12點積分，詳情請見會員中心。', '2025-05-12 11:15:00', 4, TRUE),
(5, '行動支付使用教學', '現已支援多種行動支付方式，詳細操作流程及注意事項請參閱本文。', '2025-05-11 16:45:00', 5, FALSE),
(6, '隱私權政策更新', '因應數位安全法規調整，本平台隱私權政策已於5月10日更新，請會員重新閱讀並同意相關條款。', '2025-05-10 13:20:00', 1, TRUE),
(7, '歡迎星光品牌加入', '知名美妝品牌「星光」正式進駐本平台，開幕期間全品項85折，還有限量體驗組等您來搶購！', '2025-05-09 10:30:00', 2, TRUE),
(8, '線上直播講座預告', '5月25日晚間8點，邀請時尚達人小華分享「夏日穿搭技巧」，會員可提前報名預留座位。', '2025-05-08 15:10:00', 3, FALSE),
(9, '本平台榮獲電商服務獎', '感謝用戶支持，本平台榮獲2025年電子商務優質服務金獎，我們將持續精進提供更好的服務。', '2025-05-07 09:50:00', 4, TRUE),
(10, '三週年慶典活動預告', '平台即將迎來三週年！6月1日至6月15日將推出史上最大優惠，敬請期待更多驚喜。', '2025-05-06 14:40:00', 5, FALSE);


-- 訂單
CREATE TABLE IF NOT EXISTS ORD (
    ORDER_ID INT NOT NULL AUTO_INCREMENT,
    USER_ID INT NOT NULL,
    ORDER_TIME DATETIME DEFAULT CURRENT_TIMESTAMP,
    TOTAL_AMOUNT INT NOT NULL,
    DISCOUNT_AMOUNT INT DEFAULT 0,
    SHIPPING_FEE INT NOT NULL,
    COUPON_ID INT,
    ACTUAL_AMOUNT INT NOT NULL,
    PAYMENT_METHOD TINYINT NOT NULL COMMENT '0:貨到付款(預設), 1:轉帳, 2:信用卡, 3:信用卡分期',
    PAYMENT_STATUS TINYINT NOT NULL COMMENT '0:未付款(預設), 1:已付款, 2:已退款',
    SHIPPING_ADDRESS VARCHAR(200) NOT NULL,
    SHIPPING_METHOD TINYINT NOT NULL COMMENT '0:宅配(預設), 1:超商取貨',
    SHIPPING_STATUS TINYINT NOT NULL COMMENT '0:備貨中(預設), 1:已出貨, 2:已送達',
    ORDER_STATUS TINYINT NOT NULL COMMENT '0:處理中(預設), 1:已完成, 2:已取消, 3:退貨處理中',
    ORDER_NOTE VARCHAR(800),
    UPDATED_TIME DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ORDER_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
    -- FOREIGN KEY (COUPON_ID) REFERENCES COUPON(COUPON_ID)
);

INSERT INTO ORD (USER_ID, ORDER_TIME, TOTAL_AMOUNT, DISCOUNT_AMOUNT, SHIPPING_FEE, COUPON_ID, ACTUAL_AMOUNT, PAYMENT_METHOD, PAYMENT_STATUS, SHIPPING_ADDRESS, SHIPPING_METHOD, SHIPPING_STATUS, ORDER_STATUS, ORDER_NOTE, UPDATED_TIME)
VALUES 
(1, '2025-01-05 14:30:00', 38000, 3800, 150, 2, 34350, 2, 1, '台北市信義區信義路五段100號', 0, 1, 0, '請於週末配送', '2025-01-05 14:30:00'),
(2, '2025-01-10 10:15:00', 2500, 0, 100, NULL, 2600, 1, 1, '新北市板橋區中山路200號', 0, 2, 1, NULL, '2025-01-10 10:15:00'),
(3, '2025-01-15 16:45:00', 5000, 500, 150, 4, 4650, 2, 1, '台中市西區精誠路300號', 0, 2, 1, NULL, '2025-01-15 16:45:00'),
(4, '2025-01-20 09:30:00', 1500, 150, 100, 5, 1450, 3, 1, '高雄市前鎮區中山二路400號', 1, 0, 0, '請提前電話聯絡', '2025-01-20 09:30:00'),
(5, '2025-01-25 13:20:00', 12000, 0, 150, NULL, 12150, 0, 0, '台南市東區大學路500號', 0, 0, 0, NULL, '2025-01-25 13:20:00'),
(6, '2025-02-01 11:10:00', 3500, 350, 100, 2, 3250, 2, 1, '桃園市中壢區環中東路600號', 0, 1, 0, NULL, '2025-02-01 11:10:00'),
(7, '2025-02-05 15:00:00', 8000, 0, 150, NULL, 8150, 2, 1, '新竹市東區光復路700號', 0, 2, 1, NULL, '2025-02-05 15:00:00'),
(8, '2025-02-10 17:30:00', 4000, 400, 100, 1, 3700, 1, 1, '彰化縣彰化市中山路800號', 1, 2, 1, NULL, '2025-02-10 17:30:00'),
(9, '2025-02-15 12:45:00', 11000, 1000, 150, 9, 10150, 2, 1, '嘉義市西區民生路900號', 0, 1, 0, '易碎品請小心配送', '2025-02-15 12:45:00'),
(10, '2025-02-20 10:30:00', 6500, 650, 100, 1, 5950, 3, 1, '宜蘭縣宜蘭市中山路1000號', 0, 1, 0, NULL, '2025-02-20 10:30:00');

-- 訂單明細
CREATE TABLE IF NOT EXISTS Order_Detail (
    PRODUCT_ID INT NOT NULL,
    ORDER_ID INT NOT NULL,
    PRODUCT_QUANTITY INT NOT NULL,
    PRODUCT_PRICE INT NOT NULL,
    SUBTOTAL INT NOT NULL,
    IS_RETURNED BOOLEAN DEFAULT FALSE,
    RETURN_REASON VARCHAR(800),
    RETURN_TIME DATETIME,
    PRODUCT_RATE_CONTENT VARCHAR(800),
    PRODUCT_RATE_SCORE INT,
    PRODUCT_RATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (PRODUCT_ID, ORDER_ID),
    FOREIGN KEY (PRODUCT_ID) REFERENCES PROD(PROD_ID),
    FOREIGN KEY (ORDER_ID) REFERENCES ORD(ORDER_ID)
);

INSERT INTO Order_Detail (PRODUCT_ID, ORDER_ID, PRODUCT_QUANTITY, PRODUCT_PRICE, SUBTOTAL, IS_RETURNED, PRODUCT_RATE_CONTENT, PRODUCT_RATE_SCORE)
VALUES 
(1, 1,1, 35000, 35000, false, '效能非常好，很滿意這次購買', 5),
(3, 1,1, 2500, 2500, false, '功能齊全，使用方便', 4),
(2, 2,2, 1200, 2400, false, '尺寸合適，材質不錯', 4),
(5, 3,3, 1500, 4500, false, '效果很好，肌膚變得很水潤', 5),
(6, 4,4, 350, 1400, true, '味道不合我的喜好', 2),
(4, 5,1, 800, 800, false, NULL, NULL),
(7, 5,1, 1200, 1200, false, NULL, NULL),
(10, 6,2, 1800, 3600, false, '效果顯著，車內空氣品質改善', 5),
(8, 7,1, 900, 900, false, '孩子非常喜歡，很有教育意義', 5),
(9, 8,6, 650, 3900, false, '效果不錯，持續使用中', 4);
-- 二手訂單
CREATE TABLE IF NOT EXISTS SH_Order (
    SH_ORDER_ID INT NOT NULL AUTO_INCREMENT,
    BUYER_USER_ID INT NOT NULL,
    PROD_ID INT NOT NULL,
    SELLER_USER_ID INT NOT NULL,
    ORDER_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRODUCT_QUANTITY INT NOT NULL,
    PRODUCT_PRICE INT NOT NULL,
    TOTAL_AMOUNT INT NOT NULL,
    PLATFORM_FEE INT DEFAULT 0,
    SHIPPING_FEE INT DEFAULT 0,
    PAYMENT_METHOD TINYINT NOT NULL COMMENT '0:貨到付款(預設), 1:轉帳, 2:信用卡, 3:信用卡分期',
    PAYMENT_STATUS TINYINT NOT NULL COMMENT '0:未付款(預設), 1:已付款, 2:已退款',
    SHIPPING_ADDRESS VARCHAR(200) NOT NULL,
    SHIPPING_METHOD TINYINT NOT NULL COMMENT '0:宅配(預設), 1:超商取貨',
    SHIPPING_STATUS TINYINT NOT NULL COMMENT '0:備貨中(預設), 1:已出貨, 2:已送達',
    ORDER_STATUS TINYINT NOT NULL COMMENT '0:處理中(預設), 1:已完成, 2:已取消, 3:糾紛處理中',
    ORDER_NOTE VARCHAR(800),
    UPDATED_TIME DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (SH_ORDER_ID),
    FOREIGN KEY (BUYER_USER_ID) REFERENCES USERS(USER_ID),
    FOREIGN KEY (SELLER_USER_ID) REFERENCES USERS(USER_ID),
    FOREIGN KEY (PROD_ID) REFERENCES SH_PROD(PROD_ID)
);

INSERT INTO SH_Order (BUYER_USER_ID, PROD_ID, SELLER_USER_ID, ORDER_DATE, PRODUCT_QUANTITY, PRODUCT_PRICE, TOTAL_AMOUNT, PLATFORM_FEE, SHIPPING_FEE, PAYMENT_METHOD, PAYMENT_STATUS, SHIPPING_ADDRESS, SHIPPING_METHOD, SHIPPING_STATUS, ORDER_STATUS, ORDER_NOTE, UPDATED_TIME)
VALUES 
(2, 1, 1, '2025-01-10 15:30:00', 1, 15000, 15000, 750, 150, 2, 1, '新北市板橋區中山路200號', 0, 2, 1, NULL, '2025-01-10 15:30:00'),
(3, 2, 2, '2025-01-15 10:45:00', 1, 800, 800, 40, 100, 1, 1, '台中市西區精誠路300號', 0, 2, 1, NULL, '2025-01-15 10:45:00'),
(4, 3, 3, '2025-01-20 14:10:00', 1, 3500, 3500, 175, 300, 2, 1, '高雄市前鎮區中山二路400號', 0, 2, 1, NULL, '2025-01-20 14:10:00'),
(5, 4, 4, '2025-01-25 09:20:00', 1, 8000, 8000, 400, 200, 2, 1, '台南市東區大學路500號', 0, 1, 0, NULL, '2025-01-25 09:20:00'),
(6, 5, 5, '2025-02-01 16:15:00', 5, 1500, 7500, 375, 150, 1, 1, '桃園市中壢區環中東路600號', 0, 2, 1, NULL, '2025-02-01 16:15:00');

-- 通知
CREATE TABLE IF NOT EXISTS Notification (
    NOTIFICATION_ID INT NOT NULL AUTO_INCREMENT,
    NOTIFICATION_TYPE VARCHAR(50) NOT NULL COMMENT '系統通知、訂單通知、活動通知等',
    NOTIFICATION_CATEGORY VARCHAR(50) NOT NULL COMMENT '更新公告、促銷活動、訂單狀態等',
    NOTIFICATION_LEVEL TINYINT NOT NULL DEFAULT 1 COMMENT '1:一般(預設), 2:重要, 3:緊急',
    NOTIFICATION_TITLE VARCHAR(200) NOT NULL,
    NOTIFICATION_CONTENT VARCHAR(800) NOT NULL,
    IS_BROADCAST BOOLEAN NOT NULL DEFAULT FALSE COMMENT '1:false(預設), 2:true',
    TARGET_CRITERIA JSON,
    VALID_FROM DATETIME DEFAULT CURRENT_TIMESTAMP,
    VALID_UNTIL DATETIME,
    CREATED_TIME DATETIME DEFAULT CURRENT_TIMESTAMP,
    UPDATED_TIME DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADM_ID INT NOT NULL,
    NOTI_STATUS TINYINT NOT NULL COMMENT '0:草稿, 1:已發布, 2:已撤回, 3:已過期',
    PRIMARY KEY (NOTIFICATION_ID),
    FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID)
);

INSERT INTO Notification (NOTIFICATION_TYPE, NOTIFICATION_CATEGORY, NOTIFICATION_LEVEL, NOTIFICATION_TITLE, NOTIFICATION_CONTENT, IS_BROADCAST, TARGET_CRITERIA, VALID_FROM, VALID_UNTIL, CREATED_TIME, UPDATED_TIME, ADM_ID, NOTI_STATUS)
VALUES 
('系統通知', '更新公告', 2, '系統維護通知', '系統將於2025年3月1日凌晨2點至5點進行維護，期間將暫停服務', true, '{}', '2025-02-25', '2025-03-01', '2025-02-25', '2025-02-25', 1, 1),
('訂單通知', '訂單狀態', 1, '訂單已出貨', '您的訂單#12345已出貨，預計1-3天內送達', false, '{"user_id": 2}', '2025-01-15', NULL, '2025-01-15', '2025-01-15', 2, 1),
('活動通知', '促銷活動', 1, '春季大特賣', '全館商品8折，限時三天，把握機會！', true, '{}', '2025-03-10', '2025-03-20', '2025-03-09', '2025-03-09', 3, 1),
('系統通知', '帳戶安全', 3, '安全警告', '您的帳戶在不同地點登入，請確認是否為本人操作', false, '{"user_id": 5}', '2025-01-20', NULL, '2025-01-20', '2025-01-20', 1, 1),
('訂單通知', '訂單確認', 1, '訂單已確認', '感謝您的購買，您的訂單#23456已確認', false, '{"user_id": 3}', '2025-02-01', NULL, '2025-02-01', '2025-02-01', 4, 1),
('活動通知', '會員福利', 1, 'VIP會員專屬折扣', 'VIP會員獨享95折優惠，全年有效', false, '{"user_status": 2}', '2025-01-01', '2025-12-31', '2025-01-01', '2025-01-01', 5, 1),
('系統通知', '功能更新', 2, '新功能上線', '我們新增了商品比較功能，歡迎體驗', true, '{}', '2025-04-01', NULL, '2025-04-01', '2025-04-01', 2, 1),
('訂單通知', '評價提醒', 1, '請為您的購買評分', '您購買的商品已送達，請分享您的使用體驗', false, '{"order_id": 4}', '2025-02-05', '2025-02-15', '2025-02-05', '2025-02-05', 4, 1),
('活動通知', '限時特惠', 1, '閃購特賣', '限量商品特價中，先搶先贏', true, '{}', '2025-05-01', '2025-05-03', '2025-04-30', '2025-04-30', 3, 1),
('系統通知', '隱私政策', 2, '隱私政策更新', '我們更新了隱私政策，請查閱最新內容', true, '{}', '2025-03-15', NULL, '2025-03-15', '2025-03-15', 4, 1);

-- 會員通知紀錄
CREATE TABLE IF NOT EXISTS Member_Notification_Record (
    NOTIFICATION_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    IS_READ BOOLEAN DEFAULT FALSE,
    READ_TIME DATETIME,
    SENT_TIME DATETIME DEFAULT CURRENT_TIMESTAMP,
    DELIVERY_METHOD VARCHAR(20) NOT NULL COMMENT '推播、電子郵件、簡訊等',
    DELIVERY_STATUS TINYINT NOT NULL DEFAULT 0 COMMENT '0:等待發送(預設), 1:發送成功, 2:發送失敗',
    RETRY_COUNT INT DEFAULT 0,
    LAST_RETRY_TIME DATETIME,
    ERROR_MESSAGE VARCHAR(400),
    USER_INTERACTION TINYINT NOT NULL DEFAULT 0 COMMENT '0:無互動(預設), 1:點擊, 2:回覆, 3:不再接收此類通知',
    INTERACTION_TIME DATETIME,
    TRACKING_DATA JSON COMMENT '開啟率、點擊率等',
    DEVICE_INFO VARCHAR(255),
    PRIMARY KEY (NOTIFICATION_ID, USER_ID),
    FOREIGN KEY (NOTIFICATION_ID) REFERENCES Notification(NOTIFICATION_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);

INSERT INTO Member_Notification_Record (NOTIFICATION_ID, USER_ID, IS_READ, READ_TIME, SENT_TIME, DELIVERY_METHOD, DELIVERY_STATUS, RETRY_COUNT, LAST_RETRY_TIME, ERROR_MESSAGE, USER_INTERACTION, INTERACTION_TIME, TRACKING_DATA, DEVICE_INFO) VALUES 
(1, 1, FALSE, NULL, '2025-05-10 09:30:00', '推播', 1, 0, NULL, NULL, 0, NULL, '{"open_rate": 0, "click_rate": 0}', NULL),
(2, 2, TRUE, '2025-05-10 14:22:15', '2025-05-10 14:15:00', '電子郵件', 1, 0, NULL, NULL, 1, '2025-05-10 14:25:30', '{"open_rate": 1, "click_rate": 1, "link_clicked": "promotion"}', 'iPhone 16 Pro'),
(3, 3, FALSE, NULL, '2025-05-11 08:45:00', '簡訊', 2, 3, '2025-05-11 09:30:00', '手機號碼格式不正確', 0, NULL, NULL, NULL),
(1, 4, TRUE, '2025-05-12 18:05:42', '2025-05-12 17:30:00', '推播', 1, 0, NULL, NULL, 2, '2025-05-12 18:15:20', '{"open_rate": 1, "click_rate": 1, "response_time_seconds": 580}', 'Samsung Galaxy S26'),
(4, 5, TRUE, '2025-05-13 10:12:35', '2025-05-13 10:05:00', '電子郵件', 1, 0, NULL, NULL, 3, '2025-05-13 10:15:40', '{"open_rate": 1, "click_rate": 0}', 'Chrome on Windows 11'),
(5, 6, FALSE, NULL, '2025-05-14 15:00:00', '簡訊', 0, 0, NULL, NULL, 0, NULL, NULL, NULL),
(6, 7, TRUE, '2025-05-15 07:45:12', '2025-05-15 07:30:00', '推播', 1, 0, NULL, NULL, 0, NULL, '{"open_rate": 1, "click_rate": 0, "view_duration_seconds": 8}', 'Huawei Tablet'),
(7, 8, FALSE, NULL, '2025-05-16 12:40:00', '電子郵件', 2, 2, '2025-05-16 13:10:00', '電子郵件地址不存在或已停用', 0, NULL, NULL, NULL),
(8, 9, TRUE, '2025-05-17 19:22:18', '2025-05-17 19:15:00', '推播', 1, 0, NULL, NULL, 1, '2025-05-17 19:30:45', '{"open_rate": 1, "click_rate": 1, "multiple_devices": true}', 'iPad Air, then MacBook Pro'),
(9, 10, FALSE, NULL, '2025-05-18 08:00:00', '推播', 1, 0, NULL, NULL, 0, NULL, '{"broadcast_id": "SYS20250518", "recipient_group": "all_users"}', NULL);

CREATE TABLE IF NOT EXISTS POST (
    POST_ID INT NOT NULL AUTO_INCREMENT,
    USER_ID INT NOT NULL,
    POST_TEXT VARCHAR(1000) NOT NULL,
    IMAGE_URL VARCHAR(300),
    POST_TIME DATETIME NOT NULL,
    VIEWER_PERMISSION TINYINT NOT NULL DEFAULT 0,
    LIKES_COUNT INT DEFAULT 0,
    COMMENT_COUNT INT DEFAULT 0,
    PRIMARY KEY (POST_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);

-- 貼文
INSERT INTO POST (USER_ID, POST_TEXT, IMAGE_URL, POST_TIME, VIEWER_PERMISSION, LIKES_COUNT, COMMENT_COUNT) VALUES
(1, '今天在公園散步，感覺很放鬆。', NULL, '2025-05-01 10:00:00', 0, 12, 3),
(2, '剛完成馬拉松，好累但好值得！', 'img/marathon.jpg', '2025-05-02 08:15:00', 0, 34, 5),
(3, '最近愛上做甜點，這是我做的布朗尼～', 'img/brownie.jpg', '2025-05-02 20:30:00', 1, 27, 4),
(4, '來個自拍紀錄一下心情：）', 'img/selfie.png', '2025-05-03 11:22:00', 0, 45, 6),
(5, '關於閱讀，推薦大家這本書《被討厭的勇氣》', NULL, '2025-05-04 14:45:00', 0, 19, 1),
(6, '不開心的時候，最想聽哪首歌呢？', NULL, '2025-05-04 22:10:00', 2, 0, 0),
(1, '和家人露營的照片！', 'img/camping.jpg', '2025-05-05 09:10:00', 0, 22, 2),
(7, '今天試做了韓式料理～', 'img/korean_food.jpg', '2025-05-06 13:35:00', 0, 17, 3),
(8, '這是我最喜歡的風景照之一', 'img/view.jpg', '2025-05-07 07:50:00', 1, 9, 0),
(9, '最近在學習吉他，進度緩慢但持續努力！', NULL, '2025-05-07 21:00:00', 0, 15, 1);

--
CREATE TABLE IF NOT EXISTS POST_COMMENTS (
    COMMENT_ID INT NOT NULL AUTO_INCREMENT,
    POST_ID INT NOT NULL,
    COMMENT_TIME DATETIME,
    COMMENT_TEXT VARCHAR(500),
    USER_ID INT NOT NULL,
    PARENT_COMMENT_ID INT,
    COMMENT_COUNT INT DEFAULT 0,
    PRIMARY KEY (COMMENT_ID),
    FOREIGN KEY (POST_ID) REFERENCES POST(POST_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
    FOREIGN KEY (PARENT_COMMENT_ID) REFERENCES POST_COMMENTS(COMMENT_ID)
);

-- 貼文留言
INSERT INTO POST_COMMENTS (POST_ID, COMMENT_TIME, COMMENT_TEXT, USER_ID, PARENT_COMMENT_ID, COMMENT_COUNT) VALUES
(1, '2025-05-01 11:00:00', '感覺好棒，我也想去走走！', 2, NULL, 0),
(1, '2025-05-01 11:10:00', '哪個公園呢？', 3, NULL, 1),
(1, '2025-05-01 11:15:00', '大安森林公園：）', 1, 2, 0),
(2, '2025-05-02 09:00:00', '太厲害了！加油！', 4, NULL, 0),
(2, '2025-05-02 09:05:00', '佩服你的毅力！', 5, NULL, 0),
(3, '2025-05-02 21:00:00', '看起來很好吃欸', 6, NULL, 0),
(3, '2025-05-02 21:10:00', '想學做這個，分享一下食譜？', 7, NULL, 0),
(4, '2025-05-03 11:30:00', '自拍好美！', 8, NULL, 0),
(5, '2025-05-04 15:00:00', '這本書真的超棒的', 9, NULL, 0),
(7, '2025-05-06 14:00:00', '我也愛韓式料理～', 3, NULL, 0);

-- 貼文檢舉
CREATE TABLE IF NOT EXISTS POST_REPORTS (
    RP_USER_ID INT NOT NULL AUTO_INCREMENT,
    USER_ID INT NOT NULL,
    POST_ID INT NOT NULL,
    RP_REASON TINYINT NOT NULL,
    RP_CONTENT VARCHAR(800) NOT NULL,
    RP_PIC VARCHAR(800),
    RP_TIME DATETIME NOT NULL,
    ADM_ID INT NOT NULL,
    RP_DONE_TIME DATETIME,
    RP_STATUS TINYINT NOT NULL DEFAULT 0,
    RP_NOTE VARCHAR(800),
    PRIMARY KEY (RP_USER_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
    FOREIGN KEY (POST_ID) REFERENCES POST(POST_ID),
    FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID)
);

INSERT INTO POST_REPORTS (USER_ID, POST_ID, RP_REASON, RP_CONTENT, RP_PIC, RP_TIME, ADM_ID, RP_DONE_TIME, RP_STATUS, RP_NOTE) VALUES
(2, 6, 0, '言論帶有性暗示，令人不適', NULL, '2025-05-05 10:00:00', 1, '2025-05-05 15:00:00', 1, '已刪除不當貼文'),
(3, 6, 1, '貼圖內容不雅', 'img/report1.png', '2025-05-05 10:05:00', 2, '2025-05-05 16:00:00', 2, '未違規'),
(4, 5, 2, '內容像廣告推銷', NULL, '2025-05-06 09:00:00', 1, '2025-05-06 12:00:00', 1, '已提醒用戶'),
(5, 8, 3, '懷疑是假帳號冒用照片', NULL, '2025-05-07 10:00:00', 3, NULL, 0, NULL),
(6, 9, 5, '有詐騙連結', 'img/fraud.png', '2025-05-07 11:30:00', 1, NULL, 0, NULL),
(7, 7, 6, '歧視發言令人不舒服', NULL, '2025-05-08 08:00:00', 2, NULL, 0, NULL),
(8, 4, 4, '在私訊中引導加LINE，有疑慮', NULL, '2025-05-08 09:00:00', 3, '2025-05-08 12:00:00', 1, '禁止導流行為'),
(9, 3, 7, '感覺不太對勁', NULL, '2025-05-09 10:00:00', 1, NULL, 0, NULL),
(1, 2, 2, '內容不實且誤導', NULL, '2025-05-09 10:15:00', 2, NULL, 0, NULL),
(2, 1, 6, '使用歧視用語', 'img/hate.png', '2025-05-10 10:30:00', 3, '2025-05-10 13:00:00', 1, '已警告');

CREATE TABLE IF NOT EXISTS ACTIVITY (
    ACTIVITY_ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    USER_ID INT NOT NULL,
    TITLE VARCHAR(50) NOT NULL,
    CONTENT VARCHAR(1000) NOT NULL,
    IMAGE_URL VARCHAR(300),
    LOCATION VARCHAR(100) NOT NULL,
    ACTIVITY_STATUS TINYINT NOT NULL DEFAULT 0,
    CREATED_TIME DATETIME NOT NULL,
    UPDATED_TIME DATETIME NOT NULL,
    REG_START_TIME DATETIME NOT NULL,
    REG_END_TIME DATETIME NOT NULL,
    ACTIV_START_TIME DATETIME NOT NULL,
    ACTIV_END_TIME DATETIME NOT NULL,
    GENDER_FILTER TINYINT NOT NULL DEFAULT 0,
    MAX_AGE INT NOT NULL,
    MIN_AGE INT NOT NULL,
    EXPIRED_TIME DATETIME NOT NULL,
    MAX_PEOPLE INT NOT NULL,
    MIN_PEOPLE INT NOT NULL,
    SIGNUP_COUNT INT NOT NULL,
    RATING_COUNT INT DEFAULT 0,
    RATING INT DEFAULT 0,
    COMMENT_COUNT INT DEFAULT 0,
    REPORT_COUNT INT DEFAULT 0,
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);

-- 活動表
INSERT INTO ACTIVITY VALUES 
(1, 5, '活動標題1', '活動內容描述1', 'https://example.com/image1.jpg', '台北市中正區1號', 0, '2025-05-18 14:10:05', '2025-05-19 11:24:05', '2025-05-19 00:30:05', '2025-05-21 05:03:05', '2025-05-24 07:51:05', '2025-05-25 08:01:05', 0, 34, 34, '2025-05-21 19:26:05', 10, 10, 8, 0, 19, 5, 3),
(2, 1, '活動標題2', '活動內容描述2', 'https://example.com/image2.jpg', '台北市中正區2號', 0, '2025-05-13 00:18:05', '2025-05-17 22:59:05', '2025-05-18 17:49:05', '2025-05-21 17:51:05', '2025-05-22 19:45:05', '2025-05-26 08:13:05', 1, 38, 24, '2025-05-22 13:22:05', 17, 13, 14, 16, 14, 1, 1),
(3, 4, '活動標題3', '活動內容描述3', 'https://example.com/image3.jpg', '台北市中正區3號', 0, '2025-05-12 15:40:05', '2025-05-18 09:17:05', '2025-05-18 17:48:05', '2025-05-20 22:07:05', '2025-05-23 01:07:05', '2025-05-24 23:45:05', 1, 31, 26, '2025-05-22 15:02:05', 20, 9, 15, 13, 6, 1, 0),
(4, 7, '活動標題4', '活動內容描述4', 'https://example.com/image4.jpg', '台北市中正區4號', 2, '2025-05-14 04:32:05', '2025-05-18 12:44:05', '2025-05-18 16:28:05', '2025-05-21 18:18:05', '2025-05-22 21:37:05', '2025-05-25 19:30:05', 0, 33, 32, '2025-05-21 17:31:05', 8, 2, 8, 0, 2, 3, 1),
(5, 7, '活動標題5', '活動內容描述5', 'https://example.com/image5.jpg', '台北市中正區5號', 3, '2025-05-14 21:58:05', '2025-05-18 16:13:05', '2025-05-19 15:27:05', '2025-05-22 05:21:05', '2025-05-24 13:49:05', '2025-05-25 19:15:05', 1, 31, 18, '2025-05-22 14:23:05', 6, 2, 4, 15, 16, 8, 1),
(6, 1, '活動標題6', '活動內容描述6', 'https://example.com/image6.jpg', '台北市中正區6號', 0, '2025-05-09 17:41:05', '2025-05-18 12:58:05', '2025-05-19 03:10:05', '2025-05-21 09:41:05', '2025-05-23 10:08:05', '2025-05-26 12:40:05', 2, 35, 26, '2025-05-22 12:48:05', 15, 5, 8, 12, 4, 10, 5),
(7, 5, '活動標題7', '活動內容描述7', 'https://example.com/image7.jpg', '台北市中正區7號', 3, '2025-05-12 17:03:05', '2025-05-19 10:39:05', '2025-05-18 17:37:05', '2025-05-21 07:19:05', '2025-05-23 02:59:05', '2025-05-24 22:26:05', 1, 30, 25, '2025-05-23 10:54:05', 5, 4, 5, 3, 4, 4, 0),
(8, 2, '活動標題8', '活動內容描述8', 'https://example.com/image8.jpg', '台北市中正區8號', 1, '2025-05-12 00:41:05', '2025-05-18 13:24:05', '2025-05-19 12:43:05', '2025-05-22 07:34:05', '2025-05-23 16:08:05', '2025-05-25 23:05:05', 0, 35, 22, '2025-05-22 20:50:05', 19, 19, 13, 17, 0, 1, 0),
(9, 3, '活動標題9', '活動內容描述9', 'https://example.com/image9.jpg', '台北市中正區9號', 0, '2025-05-13 09:38:05', '2025-05-18 04:11:05', '2025-05-19 00:26:05', '2025-05-21 02:16:05', '2025-05-23 12:09:05', '2025-05-26 08:59:05', 1, 29, 21, '2025-05-21 20:59:05', 18, 2, 5, 10, 25, 6, 5),
(10, 4, '活動標題10', '活動內容描述10', 'https://example.com/image10.jpg', '台北市中正區10號', 2, '2025-05-10 13:09:05', '2025-05-18 16:57:05', '2025-05-19 22:15:05', '2025-05-22 02:22:05', '2025-05-22 22:04:05', '2025-05-25 03:24:05', 1, 27, 22, '2025-05-23 11:35:05', 17, 12, 0, 3, 8, 2, 4);

-- 活動參與表
CREATE TABLE IF NOT EXISTS ACTIVITY_PARTICIPANT (
    PARTICIPANT_ID INT NOT NULL,
    ACTIVITY_ID INT NOT NULL,
    ADM_REVIEW_TIME DATETIME,
    PAR_STATUS TINYINT NOT NULL DEFAULT 0,
    APPLYING_DATE DATETIME NOT NULL,
    RATING TINYINT NOT NULL DEFAULT 5,
    REVIEW_CONTENT VARCHAR(500) NOT NULL,
    REVIEW_TIME DATETIME NOT NULL,
    PRIMARY KEY (PARTICIPANT_ID, ACTIVITY_ID),
    FOREIGN KEY (PARTICIPANT_ID) REFERENCES USERS(USER_ID),
    FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY(ACTIVITY_ID)
);

INSERT INTO ACTIVITY_PARTICIPANT VALUES
(1, 1, '2025-05-01 10:00:00', 2, '2025-04-25 09:00:00', 4, '非常充實的活動！', '2025-05-01 12:00:00'),
(2, 1, '2025-05-02 14:30:00', 2, '2025-04-26 11:20:00', 5, '主辦人很用心，值得參加', '2025-05-02 15:00:00'),
(3, 2, '2025-05-05 08:15:00', 3, '2025-04-28 10:10:00', 3, '一般般，有點無聊', '2025-05-05 08:30:00'),
(4, 2, '2025-05-06 18:00:00', 0, '2025-04-30 09:00:00', 5, '目前還在等待審核', '2025-05-06 18:00:00'),
(5, 3, '2025-05-07 09:45:00', 2, '2025-05-01 08:00:00', 4, '活動安排不錯，時間掌握佳', '2025-05-07 10:00:00'),
(6, 4, '2025-05-10 16:20:00', 2, '2025-05-02 12:00:00', 5, '氣氛超棒，大家都很熱情', '2025-05-10 17:00:00'),
(7, 4, '2025-05-12 11:11:00', 1, '2025-05-03 10:15:00', 3, '臨時有事取消了', '2025-05-12 11:11:00'),
(8, 5, '2025-05-13 19:40:00', 2, '2025-05-05 13:00:00', 2, '有改善空間，場地太吵', '2025-05-13 20:00:00'),
(9, 6, '2025-05-15 07:30:00', 2, '2025-05-06 10:00:00', 5, '值得推薦，認識不少朋友', '2025-05-15 07:45:00'),
(10, 7, '2025-05-16 20:00:00', 0, '2025-05-07 14:00:00', 5, '等待主辦審核中', '2025-05-16 20:00:00');

-- 活動留言
CREATE TABLE IF NOT EXISTS ACTIVITY_COMMENTS (
    COMMENT_ID INT NOT NULL AUTO_INCREMENT,
    ACTIVITY_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    CONTENT VARCHAR(500) NOT NULL,
    PARENT_COMMENT_ID INT,
    COMMENT_TIME DATETIME,
    COMMENT_COUNT INT DEFAULT 0,
    PRIMARY KEY (COMMENT_ID),
    FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY(ACTIVITY_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
    FOREIGN KEY (PARENT_COMMENT_ID) REFERENCES ACTIVITY_COMMENTS(COMMENT_ID)
);

INSERT INTO ACTIVITY_COMMENTS (ACTIVITY_ID, USER_ID, CONTENT, PARENT_COMMENT_ID, COMMENT_TIME, COMMENT_COUNT) VALUES
(1, 2, '這活動好像不錯耶，有人要一起參加嗎？', NULL, '2025-05-01 10:00:00', 2),
(1, 3, '我也有興趣，我們可以揪團。', 1, '2025-05-01 10:15:00', 0),
(1, 4, '請問是戶外活動嗎？', 1, '2025-05-01 10:20:00', 0),
(2, 5, '主辦人是誰啊？可以介紹一下嗎？', NULL, '2025-05-02 09:30:00', 1),
(2, 6, '是 Alex 辦的，上次活動也很好玩。', 4, '2025-05-02 09:45:00', 0),
(3, 7, '請問是否提供午餐？', NULL, '2025-05-03 14:00:00', 0),
(4, 8, '我第一次參加這類活動，有需要準備什麼嗎？', NULL, '2025-05-04 08:00:00', 0),
(5, 9, '看起來很好玩，地點方便嗎？', NULL, '2025-05-05 16:20:00', 1),
(5, 10, '就在捷運站旁邊，超方便的。', 8, '2025-05-05 16:30:00', 0),
(6, 1, '希望能認識新朋友，期待！', NULL, '2025-05-06 19:00:00', 0);

-- 活動追蹤
CREATE TABLE IF NOT EXISTS ACTIVITY_TRACKING (
    ACTIVITY_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    TRACKING_TIME DATETIME NOT NULL,
    TRACKING_STATE TINYINT NOT NULL DEFAULT 0, -- 0: 正在追蹤, 1: 取消追蹤
    PRIMARY KEY (ACTIVITY_ID, USER_ID),
    FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY(ACTIVITY_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);

INSERT INTO ACTIVITY_TRACKING (ACTIVITY_ID, USER_ID, TRACKING_TIME, TRACKING_STATE) VALUES
(1, 2, '2025-05-01 09:00:00', 0),
(1, 3, '2025-05-01 09:10:00', 1),
(2, 4, '2025-05-02 11:30:00', 0),
(2, 5, '2025-05-02 11:45:00', 0),
(3, 6, '2025-05-03 14:15:00', 1),
(3, 7, '2025-05-03 14:20:00', 0),
(4, 8, '2025-05-04 08:50:00', 0),
(5, 9, '2025-05-05 17:00:00', 1),
(6, 10, '2025-05-06 18:30:00', 0),
(7, 1, '2025-05-07 10:00:00', 0);

-- 活動檢舉
CREATE TABLE IF NOT EXISTS ACTIVITY_REPORTS (
    RP_USER_ID INT NOT NULL AUTO_INCREMENT,
    USER_ID INT NOT NULL,
    ACTIVITY_ID INT NOT NULL,
    RP_REASON TINYINT NOT NULL,  -- 0~4
    RP_CONTENT VARCHAR(800) NOT NULL,
    RP_PIC VARCHAR(800),
    RP_TIME DATETIME NOT NULL,
    ADM_ID INT NOT NULL,
    RP_DONE_TIME DATETIME,
    RP_STATUS TINYINT NOT NULL DEFAULT 0, -- 0:未處理, 1:通過, 2:不通過
    RP_NOTE VARCHAR(800),
    PRIMARY KEY (RP_USER_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
    FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY(ACTIVITY_ID),
    FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID)
);

INSERT INTO ACTIVITY_REPORTS (
    USER_ID, ACTIVITY_ID, RP_REASON, RP_CONTENT, RP_PIC,
    RP_TIME, ADM_ID, RP_DONE_TIME, RP_STATUS, RP_NOTE
) VALUES
(2, 1, 0, '活動標題有不雅字眼', NULL, '2025-05-01 09:00:00', 1, '2025-05-01 12:00:00', 1, '已通知主辦人修改'),
(3, 2, 1, '內容像廣告，請查明', 'img/ad_warning.png', '2025-05-02 10:30:00', 1, '2025-05-02 13:00:00', 1, '已下架'),
(4, 3, 2, '有人要求先轉帳才能參加', NULL, '2025-05-03 11:45:00', 2, '2025-05-03 15:00:00', 1, '確認為詐騙，封鎖'),
(5, 4, 3, '使用歧視性語言', NULL, '2025-05-04 14:20:00', 2, '2025-05-04 17:00:00', 1, '刪除留言'),
(6, 5, 4, '活動時間有誤，請重新確認', NULL, '2025-05-05 09:10:00', 3, '2025-05-05 10:00:00', 2, '時間確實正確'),
(7, 1, 0, '圖片不當', 'img/warning1.jpg', '2025-05-05 10:45:00', 3, NULL, 0, NULL),
(8, 2, 2, '懷疑是詐騙帳號', NULL, '2025-05-06 08:30:00', 1, NULL, 0, NULL),
(9, 3, 1, '活動資訊與事實不符', NULL, '2025-05-07 07:00:00', 2, NULL, 0, NULL),
(10, 4, 4, '主辦人拒絕回應問題', NULL, '2025-05-07 10:00:00', 3, NULL, 0, NULL),
(1, 5, 3, '留言內容帶有仇恨言論', NULL, '2025-05-08 11:20:00', 1, NULL, 0, NULL);

-- 活動問題
CREATE TABLE IF NOT EXISTS ACTIVITY_QUESTIONS (
    QUESTION_ID INT NOT NULL AUTO_INCREMENT,
    ACTIVITY_ID INT NOT NULL,
    QUESTION_TEXT VARCHAR(255) NOT NULL,
    QUESTION_ORDER TINYINT,
    PRIMARY KEY (QUESTION_ID),
    FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY(ACTIVITY_ID)
);

INSERT INTO ACTIVITY_QUESTIONS (ACTIVITY_ID, QUESTION_TEXT, QUESTION_ORDER) VALUES
(1, '你是否曾參加過類似活動？', 1),
(1, '你是從哪裡得知這個活動的？', 2),
(2, '是否願意攜帶朋友一同參加？', 1),
(2, '是否有飲食禁忌？', 2),
(3, '你希望活動結束後有什麼環節？', 1),
(4, '是否需要接駁服務？', 1),
(5, '活動結束後是否願意提供評價？', 1),
(5, '你最期待的活動部分是什麼？', 2),
(6, '曾經參與過我們的活動嗎？', 1),
(7, '有其他建議或需求嗎？', 3);

-- 活動回答
CREATE TABLE IF NOT EXISTS ACTIVITY_ANSWERS (
    ANSWER_ID INT NOT NULL AUTO_INCREMENT,
    ACTIVITY_ID INT NOT NULL,
    QUESTION_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    ANSWER_TEXT VARCHAR(500) NOT NULL,
    PRIMARY KEY (ANSWER_ID),
    FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY(ACTIVITY_ID),
    FOREIGN KEY (QUESTION_ID) REFERENCES ACTIVITY_QUESTIONS(QUESTION_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);

INSERT INTO ACTIVITY_ANSWERS (ACTIVITY_ID, QUESTION_ID, USER_ID, ANSWER_TEXT) VALUES
(1, 1, 2, '是的，我去年也參加過，覺得很有趣！'),
(1, 2, 2, '從朋友分享的連結看到的'),
(2, 3, 3, '會的，我會邀請兩位朋友一同參加'),
(2, 4, 3, '我不吃海鮮，其他都可以'),
(3, 5, 4, '希望能有自由交流的時間'),
(4, 6, 5, '我不需要接駁服務，我自己開車'),
(5, 7, 6, '願意，幫助活動改善'),
(5, 8, 6, '最期待的是烤肉活動'),
(6, 9, 7, '參加過一次登山活動，安排不錯'),
(7, 10, 8, '希望活動能準時結束，交通好安排');

