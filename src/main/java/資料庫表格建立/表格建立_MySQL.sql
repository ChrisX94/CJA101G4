DROP DATABASE IF EXISTS testshakemate;
CREATE DATABASE IF NOT EXISTS testshakemate;
USE testshakemate;


CREATE TABLE IF NOT EXISTS USERS (
  USER_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  USERNAME VARCHAR(200) NOT NULL,
  EMAIL VARCHAR(50) NOT NULL UNIQUE,
  PWD VARCHAR(255) NOT NULL,
  GENDER TINYINT NOT NULL,
  BIRTHDAY DATE,
  LOCATION VARCHAR(50),
  INTRO VARCHAR(200),
  CREATED_TIME DATETIME NOT NULL,
  IMG1 VARCHAR(300),
  IMG2 VARCHAR(300),
  IMG3 VARCHAR(300),
  IMG4 VARCHAR(300),
  IMG5 VARCHAR(300),
  INTERESTS VARCHAR(300),
  PERSONALITY VARCHAR(300),
  UPDATED_TIME DATETIME NOT NULL,
  USER_STATUS TINYINT NOT NULL DEFAULT 0,
  POST_STATUS BOOLEAN NOT NULL DEFAULT TRUE,
  AT_AC_STATUS BOOLEAN NOT NULL DEFAULT TRUE,
  SELL_STATUS BOOLEAN NOT NULL DEFAULT TRUE
);
-- user datas
INSERT INTO testshakemate.USERS VALUES
(1, '孔牛', 'jason88@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$t+y12RyUaOT1F1iJCaldRQ$pMLzj+VwBdgIjeH5z1gZDCZ4tqqKyX6l0m5U/DgXw4o', 0, '1995-06-12', '台北市大安區', '喜歡運動和美食，希望認識熱愛生活的你。', '2025-03-06 20:48:36', 'https://p1.music.126.net/3JiG9M60E4ffe4YEFvaGcQ==/18544363115769873.jpg?imageView=1&type=webp&thumbnail=985x0', 'https://images.ctee.com.tw/newsphoto/2021-12-09/1024/20211209700002.jpg', 'https://media.nownews.com/nn_media/thumbnail/2019/12/download.jpg?unShow=true', 'https://media.vogue.com.tw/photos/60e9974b845572c1f743ebb0/2:3/w_2560%2Cc_limit/%25E5%25AD%2594%25E5%258A%2589.jpeg', 'https://img.ltn.com.tw/Upload/ent/page/800/2017/12/15/phpuBWEeG.jpg', '追劇,旅遊,手作', '細心,感性,貼心', '2025-04-06 23:51:36', 1, TRUE, TRUE, TRUE),
(2, '哎U', 'lovelyme@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$6P2213x/iShzGUHEiaBr7g$aakg533wxbrODHTvvRYe7XBKipoyMSctrdFmMmHs6cs', 1, '1993-11-22', '新北市板橋區', '喜歡拍照與閱讀，希望遇見談得來的朋友。', '2025-03-07 01:37:29', 'https://img1.yna.co.kr/etc/inner/EN/2022/06/07/AEN20220607007800315_04_i_P2.jpg', 'https://s.yimg.com/ny/api/res/1.2/7M8RENCPBDXrgSrrRiLoUQ--/YXBwaWQ9aGlnaGxhbmRlcjt3PTY0MDtoPTgwMA--/https://s.yimg.com/os/creatr-uploaded-images/2025-05/8a5cdcd0-31a2-11f0-bfff-c1b6fb6e8288', 'https://media.nownews.com/nn_media/thumbnail/2024/10/1729162778015-70ffa88e360e417aa6a3c79489adceb8-800x899.webp', 'https://hips.hearstapps.com/hmg-prod/images/snapinsta-app-398083958-25056546057278149-8781455392402920667-n-1080-667555eeab430.jpg?crop=1xw:1xh;center,top&resize=980:*', 'https://img.ltn.com.tw/Upload/style/page/2022/04/21/220421-19556-3-4H5tU.jpg', '旅遊,閱讀,打電動', '靦腆,樂觀,陽光', '2025-04-19 05:26:29', 2, TRUE, TRUE, TRUE),
(3, '小南', 'kevinc2025@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$ZZO1kosxqhT+eZAf8HaRhg$y2Hf9hDP857k1Gx0BEOBzH8mXhoK1EqS93kC21Ma/6g', 0, '1990-04-03', '台中市西屯區', '一起去看海嗎？喜歡戶外活動和小動物。', '2025-03-15 15:52:29', 'https://a.ksd-i.com/a/2018-02-23/103030-586411.jpg', 'https://hips.hearstapps.com/hmg-prod/images/0206%E6%9B%B4%E6%96%B0-1-1644917775.jpg?crop=0.5332883187035786xw:1xh;center,top&resize=980:*', 'https://a.ksd-i.com/a/2018-02-23/103030-586408.jpg', 'https://a.ksd-i.com/a/2018-02-23/103030-586400.jpg', 'https://img.tagsis.com/202206/99888.jpeg', '閱讀,寫作,烹飪', '神秘,穩重,幽默', '2025-04-15 18:54:29', 1, TRUE, TRUE, TRUE),
(4, '喬妹', 'smileyyu@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$OWjviY0+tO+XDmAgz2BBbA$MWI2bDTfEobuQ6rXgB+gsaBRzkiXq4QhD6qaznkWX+Y', 1, '1996-09-10', '高雄市左營區', '我是一個熱愛生活、喜歡冒險的女生。', '2025-03-20 02:37:40', 'https://hips.hearstapps.com/hmg-prod/images/460318070-953953703205846-7874867108234439920-n-66ea4ef26ab98.jpg?crop=1xw:1xh;center,top&resize=980:*', 'https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2025/06/16/1/32310202.jpg&x=0&y=0&sw=0&sh=0&exp=3600&w=950', 'https://hips.hearstapps.com/hmg-prod/images/%E7%9B%B8%E7%89%87-2024-9-17-%E5%87%8C%E6%99%A83-06-02-1-66e8d66001a6f.jpg?crop=1xw:1xh;center,top&resize=980:*', 'https://hips.hearstapps.com/hmg-prod/images/song-674d533a72361.png?crop=0.494xw:0.987xh;0,0&resize=640:*', 'https://imgproxy.poponote.app/1/auto/850/0/sm/0/aHR0cHM6Ly9hc3NldHMucG9wb25vdGUuYXBwL25vdGUvNjdjMDI0NjQyOGMxYmMwMDA3ZGMwODkxL21lZGlhLzY3YzAyNDc5NDg3ODM3MDAwN2UxMzg3MQ==', '聽音樂,手作,旅遊', '文靜,開朗,務實', '2025-04-20 08:16:40', 2, TRUE, TRUE, TRUE),
(5, '大蘇', 'bchang@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$NpZaW+E9VSAKrPP1qhJ5pQ$7Rp/1zgPCrY/9kUtcsnB65sgPxFg8xTI+9wtVGfJCkc', 0, '1988-12-01', '桃園市中壢區', '找一個可以一起慢慢變老的伴。', '2025-04-05 05:16:24', 'https://www.kpopn.com/upload/9bd0c012b12b73d8912b.jpg', 'https://cdn.hk01.com/di/media/images/dw/20210526/474208592992407552826390.jpeg/FsRZSRvwdssd4h-8r7X8J1wMjrPUy4u1YHieTmB4nk4?v=w1920', 'https://hips.hearstapps.com/hmg-prod/images/collageaa-6841710809468.png?crop=0.494xw:0.986xh;0.506xw,0&resize=640:*', 'https://hips.hearstapps.com/hmg-prod/images/000-685e73959bacb.jpg?crop=0.502xw:0.756xh;0.498xw,0.0489xh&resize=768:*', 'https://image.presslogic.com/kdaily-image.presslogic.com/wp-content/uploads/2018/09/5173.jpg?auto=format&w=1053', '閱讀,投資,健身', '體貼,神秘,理性', '2025-05-05 07:01:24', 1, TRUE, TRUE, TRUE),
(6, '智圓', 'moonwind@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$8/pM62xkimFxltcZp51Abg$+hGmoPD+DUSRrS57w9JXma61ljZv6vCUMaZS/zY7Hrk', 1, '1992-07-19', '新竹市東區', '喜歡聽音樂、寫詩，也想遇到能懂我的靈魂伴侶。', '2025-04-09 06:41:35', 'https://imgproxy.poponote.app/1/auto/850/0/sm/0/aHR0cHM6Ly9hc3NldHMucG9wb25vdGUuYXBwL25vdGUvNjVmZDQ0YzZmMGI3NTgwMDA3M2FjMjg3L21lZGlhLzY1ZmQ0NmZjZjcyNzYwMDAwN2I4OTVkNg==', 'https://img.ltn.com.tw/Upload/ent/page/800/2023/12/31/4537858_1.jpg', 'https://media.nownews.com/nn_media/thumbnail/2024/04/1713615235544-55959e50935e487495f6e958fc01b8f9-800x450.webp?unShow=false', 'https://dzuxi6xtrktbc.cloudfront.net/indeximage/QnJc5P9jD05pgQl2ptXWYPuoTUcJ83Q0YcMmstt7.jpeg', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSMFov9JQqtaJlAWksvpFCxLF4QWpdNXAq1cw&s', '寫作,攝影,旅遊', '浪漫,理性,貼心', '2025-05-09 11:09:35', 2, TRUE, TRUE, TRUE),
(7, '車', 'lucas.tw@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$AtJnGZpw6uq+oKMVwsnuVg$WW6g1zUkGjTyzlmej7CvMuRv+KIEyAABX/YhACCLs2o', 0, '1994-10-05', '台南市永康區', 'IT業工程師，喜歡看Netflix與開發Side Project。', '2025-04-15 16:25:57', 'https://cdn2.ettoday.net/images/5661/5661618.jpg', 'https://wimg.mk.co.kr/news/cms/202404/22/news-p.v1.20240422.c47a418f8f284856b3f676ec996fbc40_P1.jpg', 'https://img.kpopdata.com/upload/content/234/252/642566d0c55787510264.jpeg', 'https://image-cdn.hypb.st/https%3A%2F%2Fpopbee.com%2Fimage%2F2022%2F07%2Fcha-eun-woo-2-.jpg?w=1260&cbr=1&q=90&fit=max', 'https://cdn.hk01.com/di/media/images/dw/20210925/518425945468571648385169.jpeg/JfpqAqA88EYmK_PyR4xvuQXWz29YdYDGt-UctrflHLY?v=w1920', '寫作,打電動,健身', '穩重,細心,幽默', '2025-05-17 22:12:57', 1, TRUE, TRUE, TRUE),
(8, 'Jennie', 'heartshin@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$bQXWwj6iLW/42C9u1DbWlw$dAPrJAXqvbYKQCuad++m5nBRwdKpXaxY99+pSSSp+zE', 1, '1997-08-30', '台中市南屯區', '喜歡小動物、下廚、浪漫約會。', '2025-04-18 17:27:32', 'https://today-obs.line-scdn.net/0hBY1pbjUkHXYMCQp_6s9iITRfEQc_bwd_Lm8BRCpcSkF1JVMhMzhOFSkNEFooOFklLG5SFC9cE0cha1h3NQ/w644', 'https://today-obs.line-scdn.net/0h46Cjtdqiaxl-E3qwDScUTkZFZ2hNdXEQXHIkegtHZXlRPyRGSyE4eggUMDUDIXlNXictfVgbNnlVIC1JSw/w644', 'https://gcp-obs.line-scdn.net/0hrqOl65oMLV5yFT1DMGNSCUtDIS9BcThYHG1gPVFFIz4KJ3YBTXJrJFJCI2tDdmJaSG9qPl5CJjpZIGMBSyY', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSW8HKTdsn3PJOysi-H2gtaIw1lCnlA3TS8RQ&s', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRU0K-GnLUpD1ldiLx1CGtzBvAluYm7zi7ekQ&s', '烹飪,手作,閱讀', '靦腆,開朗,感性', '2025-05-18 18:57:32', 2, TRUE, TRUE, TRUE),
(9, '小丁', 'andykuo88@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$rSKYqZMaKXsEvM611CM3wQ$9RDl5HtXZZnOhmunsmkwjriIZRV1L55T6lphT8YdJZw', 0, '1991-03-25', '新竹縣竹北市', '白天上班、晚上打球，生活簡單但充實。', '2025-04-19 10:04:26', 'https://attach.setn.com/newsimages/2023/08/26/4297794-PH.jpg', 'https://lh6.googleusercontent.com/proxy/uZh-3iB_7jejAwLhgTaQ_99VOw-4IvPFHDQzCjRv1B_N35xpLVFHMSIZrCntCI5PINW-2dZGCw4t5Ed0rTwbfqEAz1VXPBB6LSicaR2ab3Rfnx74b-oifQxU23GE7Ll8ob1D_Oy5', 'https://news.agentm.tw/wp-content/uploads/3a94e70a-d83d-49ef-900d-f71dff092ab6.png', 'https://imgproxy.poponote.app/1/auto/850/0/sm/0/aHR0cHM6Ly9hc3NldHMucG9wb25vdGUuYXBwL25vdGUvNjdjZmEwNDJlZjYyN2QwMDA3MGY1MmZkL21lZGlhLzY3Y2ZkMGI1ZWY2MjdkMDAwNzE1NjI5Mw==', 'https://i.kfs.io/article5_cover/global/5314714v1/fit/800x420.jpg', '投資,旅遊,打籃球', '陽光,神秘,開朗', '2025-05-20 11:41:26', 1, TRUE, TRUE, TRUE),
(10, 'Bible', 'miduki.love@mail.com', '$argon2i$v=19$m=65536,t=3,p=1$eypXlkTcupPifY5szj+ehQ$lUlqgE3dfezXVpZ7VFKylE/3VGK7E2vVbUZILWI5IQ8', 1, '1995-05-18', '宜蘭縣羅東鎮', '在旅行中尋找靈感，也在等待那個願意陪我走世界的人。', '2025-04-20 18:17:27', 'https://hips.hearstapps.com/hmg-prod/images/cover8-6790b72414c2e.png?crop=0.481xw:0.961xh;0.507xw,0.0293xh&resize=640:*', 'https://megapx-assets.dcard.tw/images/27e65eb6-41c6-46b3-b12d-7ee5de4fa3c5/640.jpeg', 'https://dzuxi6xtrktbc.cloudfront.net/indeximage/c4MnrkelIj9Fb4eNi64QrAwTFoyWq3yEnZDVaCDT.jpeg', 'https://i.pinimg.com/736x/73/6b/8a/736b8a9615a6125d825b836a208a7b6b.jpg', 'https://cdn.beautyexchange.com.hk/wp-content/uploads/2024/11/17175523/20230411185214-86c3ab7b.jpg', '攝影,旅遊,寫作', '溫柔,貼心,樂觀', '2025-05-20 20:37:27', 2, TRUE, TRUE, TRUE);
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
  ADM_NAME VARCHAR(20) NOT NULL UNIQUE,
  ADM_ACC VARCHAR(20) NOT NULL,
  ADM_PWD VARCHAR(100) NOT NULL,
  ADM_STA BOOLEAN DEFAULT TRUE
);

INSERT INTO ADM (
        ADM_ID, ADM_NAME, ADM_ACC, ADM_PWD, ADM_STA
    ) VALUES 
    (1, 'Alice', 'admin_alice', '$2a$10$nSX4XuMPjiiIkY3CC8Ccbu4NGSDkUdzFNn/5/4KvlcBKj8clWNxB2',true),
	(2, 'Bob', 'admin_bob', '$2a$10$nSX4XuMPjiiIkY3CC8Ccbu4NGSDkUdzFNn/5/4KvlcBKj8clWNxB2',true),
    (3, 'Cindy', 'admin_cindy', '$2a$10$nSX4XuMPjiiIkY3CC8Ccbu4NGSDkUdzFNn/5/4KvlcBKj8clWNxB2',true),
    (4, 'David', 'admin_david', '$2a$10$nSX4XuMPjiiIkY3CC8Ccbu4NGSDkUdzFNn/5/4KvlcBKj8clWNxB2',true),
	(5, 'Eva', 'admin_eva', '$2a$10$nSX4XuMPjiiIkY3CC8Ccbu4NGSDkUdzFNn/5/4KvlcBKj8clWNxB2',true),
    (6, 'Charlie', 'admin_charlie', '$2a$10$nSX4XuMPjiiIkY3CC8Ccbu4NGSDkUdzFNn/5/4KvlcBKj8clWNxB2',true);
    

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
(10, 5, 1, '2025-05-18 17:01:00'),
(1, 10, 2, '2025-05-20 17:01:00');


-- 聊天室
CREATE TABLE IF NOT EXISTS CHAT_ROOM (
  ROOM_ID INT AUTO_INCREMENT PRIMARY KEY COMMENT '聊天室編號',
  USER1_ID INT NOT NULL COMMENT '會員A編號（編號較小者）',
  USER2_ID INT NOT NULL COMMENT '會員B編號（編號較大者）',
  MATCH_ID INT NOT NULL COMMENT '配對成功對應的MATCH紀錄ID',
  CREATED_TIME DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP COMMENT '聊天室建立時間',
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
  MESSAGE_CONTENT VARCHAR(500) COMMENT '文字訊息內容' ,
  MESSAGE_IMG MEDIUMBLOB COMMENT '圖片訊息（可選）',
  SENT_TIME DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '發送時間',
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
  CONSTRAINT FK_RP_TARGET FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
  CONSTRAINT FK_RP_ADMIN FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID)
);

-- USER_RP 假資料（三筆檢舉紀錄）

INSERT INTO USER_RP (REPORTER_ID, USER_ID, RP_REASON, RP_CONTENT, RP_PIC, RP_TIME, ADM_ID, RP_DONE_TIME, RP_STATUS, RP_NOTE) VALUES 
(6, 4, 6, '對方傳送騷擾訊息，內容令人不適。', NULL, '2025-05-18 17:54:00', 1, '2025-05-18 17:36:00', 2, '內容不足，暫不採納。'),
(8, 2, 0, '頭貼疑似裸露照片，請盡快處理。', NULL, '2025-05-18 18:57:00', 1, '2025-05-18 19:11:00', 1, '已審核完畢，依規定處理。'),
(5, 2, 4, '自我介紹帶有推銷意味，懷疑是詐騙帳號。', NULL, '2025-05-18 18:23:00', 2, '2025-05-18 18:05:00', 2, '內容不足，暫不採納。');


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
  PROD_STATUS_DESC VARCHAR(800) NOT NULL,
  PROD_PRICE INT NOT NULL,
  PROD_BRAND VARCHAR(50),
  PROD_COUNT INT NOT NULL DEFAULT 0,
  PROD_VIEWS INT NOT NULL DEFAULT 0,
  PROD_STATUS TINYINT NOT NULL,
  PROD_REG_TIME DATETIME NOT NULL,
  UPDATED_TIME DATETIME NOT NULL,
  FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
  FOREIGN KEY (PROD_TYPE_ID) REFERENCES SH_PROD_TYPE(PROD_TYPE_ID)
);

INSERT INTO SH_PROD (
            USER_ID, PROD_NAME, PROD_TYPE_ID, PROD_CONTENT, PROD_STATUS_DESC,
            PROD_PRICE, PROD_BRAND, PROD_COUNT, PROD_VIEWS, PROD_STATUS, PROD_REG_TIME, UPDATED_TIME
        ) VALUES 
(8, 'ASUS筆電', 1, 'ASUS筆電出清，運行順暢，保存狀況良好。', 'ASUS筆電僅有輕微使用痕跡，無任何功能損壞。', 8888, 'ASUS', 1, 17, 2,  '2025-05-19 04:43:21', '2025-05-19 04:43:21'), 
(2, 'Nike毛衣', 2, 'Nike毛衣釋出，布料舒適，穿著感佳。', 'Nike毛衣輕微使用，無污漬與破損。', 247, 'Nike', 2, 49, 2,  '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
(3, '時鐘', 3, 'Ikea時鐘，極簡設計，運作正常。', 'Ikea時鐘表面完好，輕微使用痕跡。', 276, 'Ikea', 3, 27, 2,  '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
(1, 'Java書籍', 4, 'Java書籍，完整無缺頁，適合學習。', 'Java書籍封面輕微磨損，內頁乾淨無筆記。', 175, 'book', 8, 10, 2,  '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
(5, '瑜珈墊', 5, '迪卡農瑜珈墊，防滑耐用，適合各類瑜珈練習。', '迪卡農瑜珈墊表面輕微磨損，功能正常。', 175, '迪卡農', 2, 10, 2,  '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
(4, '化妝品', 6, '全新未拆封化妝品，保存良好。', '化妝品外包裝完整，內容物未開封。', 903, 'ShiningBeauty', 3, 4, 2,  '2025-05-19 04:43:21', '2025-05-19 04:43:21'),
(6, '永生花禮盒', 1, '戀人首選永生花禮盒，浪漫與長情的象徵。', '經典浪漫不凋謝花束，附燈飾與精美禮盒。', 1480, 'LoveBloom', 1, 0, 2, '2025-05-05 02:20:27', '2025-05-06 06:50:27'),
(7, '情侶對杯', 1, '送禮首選，CoupleMug情侶對杯，擁有專屬的杯子。', '可愛情侶圖案馬克杯，一對販售，無瑕疵。', 699, 'CoupleMug', 1, 0, 2, '2025-05-04 17:08:12', '2025-05-07 15:52:12'),
(3, '情侶對戒', 1, 'PromiseRing情侶對戒，象徵彼此的承諾與連結。', '簡約風對戒，銀色款，尺寸可調整，外觀完好。', 1299, 'PromiseRing', 1, 0, 2, '2025-05-24 12:15:48', '2025-05-29 21:10:48'),
(8, '驚喜盒子', 1, 'SurpriseYou驚喜盒子，戀愛驚喜包，適合曖昧階段送出。', '內含浪漫小物、零食、告白卡，隨機出貨。', 499, 'SurpriseYou', 1, 0, 2, '2025-05-24 00:09:01', '2025-05-28 22:52:01'),
(2, '香氛蠟燭', 2, 'AromaLite香氛蠟燭，讓你的房間充滿香氣與情調。', '天然精油蠟燭，多款香味可選，燃燒穩定。', 780, 'AromaLite', 1, 0, 2, '2025-05-11 15:48:33', '2025-05-15 05:08:33'),
(10, '星空投影燈', 2, 'SkyMood星空投影燈，視訊、約會必備，打造浪漫氛圍。', '投影夜空圖樣，操作簡單，氣氛滿分。', 1280, 'SkyMood', 1, 0, 2, '2025-05-05 11:45:13', '2025-05-12 04:18:13'),
(10, '擴香瓶', 2, 'Fragrance+擴香瓶，居家與辦公環境香氛首選。', '高質感藤枝擴香瓶，香氣持久，包裝完整。', 890, 'Fragrance+', 1, 0, 2, '2025-05-30 21:42:29', '2025-06-05 19:00:29'),
(5, '自拍補光燈', 3, 'BrightMe自拍補光燈，讓你的自拍與大頭貼更上相。', 'USB充電，三段亮度調節，輕巧便攜。', 320, 'BrightMe', 1, 0, 2, '2025-06-14 06:30:40', '2025-06-19 14:25:40'),
(1, '小型三腳架', 3, 'FlexiPod小型三腳架，適合自拍或視訊固定角度。', '手機支架，可360度旋轉，支撐穩定。', 450, 'FlexiPod', 1, 0, 2, '2025-06-08 00:36:33', '2025-06-09 09:57:33'),
(6, '穿搭指南手冊', 3, 'StyleUp穿搭指南手冊，幫你第一次見面留下好印象。', '男女皆適用的約會穿搭建議本，內容完整。', 180, 'StyleUp', 1, 0, 2, '2025-06-12 13:26:55', '2025-06-16 03:31:55'),
(7, '吉伊卡哇娃娃', 4, 'LINE FRIENDS吉伊卡哇娃娃，可愛又療癒。', '超人氣角色娃娃，柔軟材質，品質良好。', 690, 'LINE FRIENDS', 1, 0, 2, '2025-06-01 20:00:46', '2025-06-05 16:37:46'),
(8, '姓名吊飾', 4, 'NameMe姓名吊飾，情侶對飾推薦，自用收藏皆宜。', '壓克力姓名牌，可自訂名字，堅固耐用。', 299, 'NameMe', 1, 0, 2, '2025-05-28 02:16:57', '2025-06-01 13:07:57'),
(2, '情侶任務本', 4, 'GrowTogether情侶任務本，給你們更多互動與儀式感。', '收錄52個任務，鼓勵一起完成，增進感情。', 399, 'GrowTogether', 1, 0, 2, '2025-05-26 04:58:36', '2025-05-26 10:31:36');

				
-- SH_PROD_PIC
CREATE TABLE IF NOT EXISTS SH_PROD_PIC (
  PROD_PIC_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  PROD_ID INT NOT NULL,
  PROD_PIC VARCHAR(300),
  FOREIGN KEY (PROD_ID) REFERENCES SH_PROD(PROD_ID)
);

INSERT INTO SH_PROD_PIC (PROD_ID, PROD_PIC)
			VALUES 
            (1, 'https://www.shutterstock.com/shutterstock/photos/2573878489/display_1500/stock-photo-laptop-with-a-blank-white-screen-isolated-on-a-white-background-providing-a-clean-minimalist-2573878489.jpg'),
			(2, 'https://www.shutterstock.com/shutterstock/photos/2479636085/display_1500/stock-photo-blue-flying-crumpled-women-s-autumn-knitted-sweater-with-blank-label-tag-isolated-on-white-2479636085.jpg'),
			(3, 'https://www.shutterstock.com/shutterstock/photos/2553099279/display_1500/stock-photo-round-wall-clock-with-gold-frame-and-numbers-elegant-gold-clock-design-minimalist-clock-style-2553099279.jpg'),
			(4, 'https://www.shutterstock.com/shutterstock/photos/2505565311/display_1500/stock-photo-png-a-stack-of-books-isolated-on-white-background-2505565311.jpg'),
			(5, 'https://www.shutterstock.com/shutterstock/photos/2613998575/display_1500/stock-photo-fitness-woman-folding-exercise-mat-before-working-out-in-yoga-studio-rolling-yoga-mat-after-2613998575.jpg'),
			(6, 'https://www.shutterstock.com/shutterstock/photos/2287814949/display_1500/stock-photo-pipette-drop-of-serum-test-on-a-beige-background-sun-glare-2287814949.jpg'),
			(7, 'https://www.shutterstock.com/shutterstock/photos/93916165/display_1500/stock-photo-flowers-and-gift-box-isolated-on-white-93916165.jpg'),
			(8, 'https://www.shutterstock.com/shutterstock/photos/2372398555/display_1500/stock-vector--d-mug-with-hot-tea-and-milk-or-cappuccino-and-latte-realistic-americano-and-espresso-drink-2372398555.jpg'),
			(9, 'https://www.shutterstock.com/shutterstock/photos/2298601103/display_1500/stock-vector-isolated-gold-wedding-rings-realistic-linked-gold-rings-2298601103.jpg'),
			(10, 'https://www.shutterstock.com/shutterstock/photos/2411610469/display_1500/stock-vector-new-year-s-sale-banner-illustration-new-year-s-sale-fortune-2411610469.jpg'),
			(11, 'https://www.shutterstock.com/shutterstock/photos/2410293667/display_1500/stock-vector-a-realistic-d-illustration-of-a-caramel-scented-candle-in-brown-glass-with-glowing-flame-perfect-2410293667.jpg'),
			(12, 'https://www.shutterstock.com/shutterstock/photos/2095334329/display_1500/stock-vector-boost-creative-idea-imagination-innovation-or-technology-to-help-success-invent-new-solution-to-2095334329.jpg'),
			(13, 'https://www.shutterstock.com/shutterstock/photos/1921332719/display_1500/stock-vector-aroma-diffuser-glass-transparent-plastic-realistic-set-aromatherapy-evaporative-tools-for-1921332719.jpg'),
			(14, 'https://www.shutterstock.com/shutterstock/photos/2041273067/display_1500/stock-vector-lamp-flash-for-selfie-shooting-bloggers-vector-illustration-isolated-on-background-2041273067.jpg'),
			(15, 'https://www.shutterstock.com/shutterstock/photos/1147651661/display_1500/stock-photo-mobile-phone-on-tripod-for-take-photos-isolated-on-white-background-with-clipping-path-1147651661.jpg'),
			(16, 'https://www.shutterstock.com/shutterstock/photos/2408499005/display_1500/stock-photo-dreamy-galaxy-night-light-projector-lamp-on-table-2408499005.jpg'),
			(17, 'https://www.shutterstock.com/shutterstock/photos/2341304654/display_1500/stock-photo-elegant-reed-diffuser-on-office-desk-2341304654.jpg'),
			(18, 'https://www.shutterstock.com/shutterstock/photos/2523892601/display_1500/stock-photo-compact-led-ring-light-for-smartphone-photography-2523892601.jpg'),
			(19, 'https://www.shutterstock.com/shutterstock/photos/2467851082/display_1500/stock-photo-tripod-stand-for-mobile-camera-recording-2467851082.jpg'),
            (11, 'https://www.shutterstock.com/shutterstock/photos/2153110685/display_1500/stock-vector--d-scented-candle-set-isolated-on-beige-background-brown-glass-product-package-one-with-and-2153110685.jpg'),
			(15, 'https://www.shutterstock.com/shutterstock/photos/2428092545/display_1500/stock-photo-fashion-style-book-flat-lay-on-wooden-background-2428092545.jpg');


-- 客服類別表
CREATE TABLE IF NOT EXISTS casetype(
	CASE_TYPE_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    TYPE_NAME VARCHAR(50) NOT NULL,
    TYPE_DESC VARCHAR(100) NOT NULL
);

-- 插入案件類別資料 (8筆)
INSERT INTO casetype (TYPE_NAME, TYPE_DESC) VALUES 
('商城疑問', '產品功能使用、設定、訂單狀態等相關問題'),
('帳戶問題', '帳號註冊、登入、安全、隱私等相關問題'),
('付款問題', '支付方式、發票、退款等相關問題'),
('技術支援', '系統錯誤、連接問題、軟體更新等相關問題'),
('活動諮詢', '平台活動、優惠券使用、積分兌換等相關問題'),
('會員權益', '會員等級、權益、福利等相關問題'),
('平台建議', '使用者體驗、界面設計、功能建議等相關問題'),
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
    EMAIL VARCHAR(255) NOT NULL,
	FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
	FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID),
	FOREIGN KEY (CASE_TYPE_ID) REFERENCES CASETYPE(CASE_TYPE_ID)
);

-- 插入客服案件資料 (10筆)
INSERT INTO servicecase (USER_ID, ADM_ID, CASE_TYPE_ID, CREATE_TIME, UPDATE_TIME, TITLE, CONTENT, CASE_STATUS, EMAIL) VALUES 
(1, 1, 1, '2025-05-01 09:15:00', '2025-05-01 14:30:00', '如何更改個人資料', '我無法在個人頁面找到修改生日的選項，請問要如何更改？', 2, 'a123456@gmail.com'),
(2, 2, 2, '2025-05-02 10:20:00', '2025-05-02 11:45:00', '訂單#12345尚未收到', '我的訂單已顯示送達，但實際上還沒收到商品', 1, 'a78910@gmail.com'),
(3, 3, 3, '2025-05-03 13:30:00', '2025-05-03 15:10:00', '無法登入帳戶', '輸入正確密碼後系統仍顯示錯誤，請協助處理', 0, 'b1212331224@gmail.com'),
(4, 4, 4, '2025-05-04 16:45:00', '2025-05-04 17:20:00', '如何申請退款', '想取消最近的訂單並申請退款，請問流程為何？', 2, 'c2121312@gmail.com'),
(5, 5, 5, '2025-05-05 08:30:00', '2025-05-05 09:45:00', 'APP閃退問題', '最新版本的APP使用時頻繁閃退，請問有解決方法嗎？', 1, 'fdsf3333@gmail.com'),
(6, 1, 6, '2025-05-06 11:20:00', '2025-05-06 14:15:00', '會員日活動詳情', '請問下次會員日何時舉辦？有哪些優惠？', 2, 'ddgsf33557@gmail.com'),
(7, 2, 7, '2025-05-07 09:40:00', '2025-05-07 10:30:00', '金卡會員權益查詢', '我最近升級為金卡會員，想了解有哪些專屬權益', 0, 'kk2312@gmail.com'),
(8, 3, 8, '2025-05-08 15:25:00', '2025-05-08 16:50:00', '購物車功能建議', '建議購物車能保存更長時間，目前24小時後就會清空', 1, 'gg3361@gmail.com'),
(9, 4, 1, '2025-05-09 13:10:00', '2025-05-09 14:30:00', '產品尺寸諮詢', '網站上的XL尺寸對應的實際肩寬和胸圍是多少？', 2, 'yaya8891@gmail.com'),
(10, 5, 2, '2025-05-10 10:05:00', '2025-05-10 11:20:00', '無法收到驗證碼', '嘗試註冊新帳號但手機收不到驗證簡訊，請協助處理', 0, 'ggdadgaa446@gmail.com');


-- 最新消息
-- 類別表
CREATE TABLE IF NOT EXISTS newstype (
	CATEGORY_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    CATEGORY_NAME VARCHAR(50) NOT NULL,
    NEWS_DESC VARCHAR(100) NOT NULL
);

-- 插入消息類別資料 (5筆)
INSERT INTO newstype (CATEGORY_NAME, NEWS_DESC) VALUES 
('系統公告', '平台系統更新、維護、變更等相關公告'),
('活動訊息', '促銷活動、節日特惠、限時優惠等相關訊息'),
('新品上架', '新產品上架、新功能推出等相關訊息'),
('會員通知', '會員權益、等級變更、積分相關等通知'),
('使用教學', '平台功能、服務使用教學等相關資訊');

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
(1, '隱私權政策更新', '因應數位安全法規調整，本平台隱私權政策已於5月10日更新，請會員重新閱讀並同意相關條款。', '2025-05-10 13:20:00', 1, TRUE),
(2, '歡迎星光品牌加入', '知名美妝品牌「星光」正式進駐本平台，開幕期間全品項85折，還有限量體驗組等您來搶購！', '2025-05-09 10:30:00', 2, TRUE),
(2, '線上直播講座預告', '5月25日晚間8點，邀請時尚達人小華分享「夏日穿搭技巧」，會員可提前報名預留座位。', '2025-05-08 15:10:00', 3, FALSE),
(1, '本平台榮獲電商服務獎', '感謝用戶支持，本平台榮獲2025年電子商務優質服務金獎，我們將持續精進提供更好的服務。', '2025-05-07 09:50:00', 4, TRUE),
(2, '三週年慶典活動預告', '平台即將迎來三週年！6月1日至6月15日將推出史上最大優惠，敬請期待更多驚喜。', '2025-05-06 14:40:00', 5, FALSE);


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
    PAYMENT_METHOD TINYINT NOT NULL COMMENT '0:貨到付款(預設), 1:線上支付',
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
(2, 1, 1, '2025-01-10 15:30:00', 1, 8888, 8988, 40, 60, 0, 1, '新北市板橋區中山路200號', 0, 2, 1, NULL, '2025-01-10 15:30:00'),
(3, 2, 2, '2025-01-15 10:45:00', 1, 247, 297, 10, 40, 1, 1, '台中市西區精誠路300號', 0, 2, 1, NULL, '2025-01-15 10:45:00'),
(4, 3, 3, '2025-01-20 14:10:00', 1, 276, 326, 10, 40, 1, 1, '高雄市前鎮區中山二路400號', 0, 2, 1, NULL, '2025-01-20 14:10:00'),
(5, 4, 4, '2025-01-25 09:20:00', 1, 175, 226, 10, 40, 0, 1, '台南市東區大學路500號', 0, 1, 0, NULL, '2025-01-25 09:20:00'),
(6, 5, 5, '2025-02-01 16:15:00', 5, 175, 226, 10, 40, 1, 1, '桃園市中壢區環中東路600號', 0, 2, 1, NULL, '2025-02-01 16:15:00');

-- ============================================
-- 通知系統相關資料表 (含外鍵約束)
-- ============================================

-- 1. 通知主表 (Notification)
CREATE TABLE IF NOT EXISTS NOTIFICATION (
    NOTIFICATION_ID INT NOT NULL AUTO_INCREMENT,
    NOTIFICATION_TYPE VARCHAR(50) NOT NULL,
    NOTIFICATION_CATEGORY VARCHAR(50) NOT NULL,
    NOTIFICATION_LEVEL TINYINT NOT NULL DEFAULT 1,
    NOTIFICATION_TITLE VARCHAR(200) NOT NULL,
    NOTIFICATION_CONTENT VARCHAR(800) NOT NULL,
    IS_BROADCAST BOOLEAN NOT NULL DEFAULT FALSE,
    TARGET_CRITERIA JSON NULL,
    VALID_FROM DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    VALID_UNTIL DATETIME NULL,
    CREATED_TIME DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_TIME DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADM_ID INT NOT NULL,
    NOTI_STATUS TINYINT NOT NULL DEFAULT 0,
    
    PRIMARY KEY (NOTIFICATION_ID),
    INDEX idx_notification_type (NOTIFICATION_TYPE),
    INDEX idx_notification_category (NOTIFICATION_CATEGORY),
    INDEX idx_valid_period (VALID_FROM, VALID_UNTIL),
    INDEX idx_created_time (CREATED_TIME),
    INDEX idx_noti_status (NOTI_STATUS),
    INDEX idx_adm_id (ADM_ID),
    
    CONSTRAINT fk_notification_adm 
        FOREIGN KEY (ADM_ID) REFERENCES ADM(ADM_ID) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知主表 - 儲存所有通知的基本資訊';

-- 1. 通知主表 10筆資料 
INSERT INTO NOTIFICATION (NOTIFICATION_TYPE, NOTIFICATION_CATEGORY, NOTIFICATION_LEVEL, NOTIFICATION_TITLE, NOTIFICATION_CONTENT, IS_BROADCAST, TARGET_CRITERIA, VALID_FROM, VALID_UNTIL, ADM_ID, NOTI_STATUS) VALUES
('系統通知', '更新公告', 2, '系統維護通知', '系統將於6月15日凌晨2:00-4:00進行維護升級，期間服務暫停。', true, NULL, '2024-06-14 00:00:00', '2024-06-16 00:00:00', 1, 1),
('訂單通知', '訂單狀態', 1, '訂單出貨通知', '您的訂單已出貨，預計1-2個工作天送達。', false, '{"user_ids": [1,3,5]}', '2024-06-01 00:00:00', NULL, 2, 1),
('活動通知', '促銷活動', 2, '夏日特賣活動', '夏日特賣開跑！全站商品最高8折優惠，活動期間限定。', true, NULL, '2024-06-01 00:00:00', '2024-06-30 23:59:59', 1, 1),
('系統通知', '安全提醒', 3, '帳戶安全提醒', '發現異常登入行為，請立即檢查您的帳戶安全設定。', false, '{"user_ids": [7,9]}', '2024-06-03 00:00:00', NULL, 3, 1),
('訂單通知', '付款提醒', 1, '付款提醒通知', '您有一筆待付款訂單，請在24小時內完成付款。', false, '{"user_ids": [11,13]}', '2024-06-05 00:00:00', '2024-06-06 23:59:59', 2, 1),
('活動通知', '新功能介紹', 1, '新增超商取貨功能', '現在支援超商取貨服務，購物更便利！', true, NULL, '2024-06-01 00:00:00', NULL, 1, 1),
('系統通知', '政策更新', 2, '隱私政策更新', '我們已更新隱私政策條款，請詳閱相關內容。', true, NULL, '2024-06-01 00:00:00', NULL, 4, 1),
('訂單通知', '評價提醒', 1, '商品評價提醒', '您購買的商品已送達，歡迎給予評價回饋。', false, '{"user_ids": [15,17]}', '2024-06-08 00:00:00', NULL, 2, 1),
('活動通知', '會員專享', 2, 'VIP會員優惠', 'VIP會員專享優惠，精選商品享額外9折優惠。', false, '{"user_type": "VIP"}', '2024-06-01 00:00:00', '2024-06-30 23:59:59', 1, 1),
('系統通知', '更新公告', 1, 'APP版本更新', '新版本APP已發布，包含多項功能改善和錯誤修正。', true, NULL, '2024-06-10 00:00:00', NULL, 5, 1);

-- 2. 通知偏好設定表 (Notification Preference)
CREATE TABLE IF NOT EXISTS NOTIFICATION_PREFERENCE (
    PREFERENCE_ID INT NOT NULL AUTO_INCREMENT,
    USER_ID INT NOT NULL,
    NOTIFICATION_CATEGORY VARCHAR(50) NOT NULL,
    EMAIL_ENABLED BOOLEAN NOT NULL DEFAULT TRUE,
    SMS_ENABLED BOOLEAN NOT NULL DEFAULT TRUE,
    PUSH_ENABLED BOOLEAN NOT NULL DEFAULT TRUE,
    IN_APP_ENABLED BOOLEAN NOT NULL DEFAULT TRUE,
    QUIET_HOURS_ENABLED BOOLEAN NOT NULL DEFAULT FALSE,
    QUIET_HOURS_START TIME NULL,
    QUIET_HOURS_END TIME NULL,
    UPDATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (PREFERENCE_ID),
    UNIQUE KEY uk_user_category (USER_ID, NOTIFICATION_CATEGORY),
    INDEX idx_user_id (USER_ID),
    INDEX idx_notification_category (NOTIFICATION_CATEGORY),
    
    CONSTRAINT fk_preference_user 
        FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知偏好設定表 - 儲存用戶對各類通知的接收偏好';

-- 2. 通知偏好設定表 10筆資料
INSERT INTO NOTIFICATION_PREFERENCE (USER_ID, NOTIFICATION_CATEGORY, EMAIL_ENABLED, SMS_ENABLED, PUSH_ENABLED, IN_APP_ENABLED, QUIET_HOURS_ENABLED, QUIET_HOURS_START, QUIET_HOURS_END) VALUES
(1, '訂單狀態', true, true, true, true, true, '22:00:00', '08:00:00'),
(2, '促銷活動', true, false, true, true, false, NULL, NULL),
(3, '更新公告', false, false, true, true, true, '23:00:00', '07:00:00'),
(4, '訂單狀態', true, true, false, true, false, NULL, NULL),
(5, '促銷活動', true, true, true, false, true, '21:30:00', '08:30:00'),
(6, '安全提醒', true, true, true, true, false, NULL, NULL),
(7, '新功能介紹', false, false, true, true, true, '22:30:00', '07:30:00'),
(8, '訂單狀態', true, false, true, true, true, '23:30:00', '06:30:00'),
(9, '政策更新', true, false, false, true, false, NULL, NULL),
(10, '促銷活動', false, true, true, true, true, '22:00:00', '08:00:00');

-- 3. 通知模板表 (Notification Template)
CREATE TABLE IF NOT EXISTS NOTIFICATION_TEMPLATE (
    TEMPLATE_ID INT NOT NULL AUTO_INCREMENT,
    CREATED_BY_ID INT NOT NULL,
    TEMPLATE_CODE VARCHAR(50) NOT NULL,
    TEMPLATE_NAME VARCHAR(100) NOT NULL,
    TEMPLATE_TYPE VARCHAR(50) NOT NULL,
    TEMPLATE_CATEGORY VARCHAR(50) NOT NULL,
    SUBJECT VARCHAR(200) NOT NULL,
    CONTENT TEXT NOT NULL,
    HTML_CONTENT VARCHAR(255) NULL,
    VARIABLES JSON NOT NULL,
    IS_ACTIVE BOOLEAN NOT NULL DEFAULT TRUE,
    CREATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATE_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    IS_SYSTEM BOOLEAN NOT NULL DEFAULT FALSE,
    DESCRIPTION VARCHAR(200) NULL,
    PREVIEW_IMAGE VARCHAR(255) NULL,
    
    PRIMARY KEY (TEMPLATE_ID),
    UNIQUE KEY uk_template_code (TEMPLATE_CODE),
    INDEX idx_template_type (TEMPLATE_TYPE),
    INDEX idx_template_category (TEMPLATE_CATEGORY),
    INDEX idx_is_active (IS_ACTIVE),
    INDEX idx_created_by (CREATED_BY_ID),
    INDEX idx_is_system (IS_SYSTEM),
    
    CONSTRAINT fk_template_creator 
        FOREIGN KEY (CREATED_BY_ID) REFERENCES ADM(ADM_ID) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表 - 儲存各種通知的模板內容';

-- 3. 通知模板表 10筆資料 
INSERT INTO NOTIFICATION_TEMPLATE (CREATED_BY_ID, TEMPLATE_CODE, TEMPLATE_NAME, TEMPLATE_TYPE, TEMPLATE_CATEGORY, SUBJECT, CONTENT, HTML_CONTENT, VARIABLES, IS_ACTIVE, IS_SYSTEM, DESCRIPTION, PREVIEW_IMAGE) VALUES
(1, 'ORDER_SHIPPED', '訂單出貨通知模板', 'EMAIL', '訂單狀態', '您的訂單已出貨', true, '<h2>訂單出貨通知</h2><p>親愛的 {{user_name}}，您的訂單 {{order_id}} 已出貨。</p>', '{"user_name": "string", "order_id": "string", "tracking_number": "string"}', true, true, '訂單出貨時發送的電子郵件模板', NULL),
(2, 'PAYMENT_REMINDER', '付款提醒模板', 'SMS', '付款提醒', '付款提醒', true, NULL, '{"user_name": "string", "order_id": "string", "amount": "number", "deadline": "datetime"}', true, true, '付款期限提醒簡訊模板', NULL),
(1, 'PROMO_CAMPAIGN', '促銷活動模板', 'PUSH', '促銷活動', '限時優惠活動', true, NULL, '{"campaign_name": "string", "discount": "number", "end_date": "datetime"}', true, false, '促銷活動推播通知模板', 'promo_banner.jpg'),
(3, 'SECURITY_ALERT', '安全警示模板', 'EMAIL', '安全提醒', '帳戶安全警示', true, '<div style="color:red;"><h2>安全警示</h2><p>{{user_name}}，偵測到異常登入。</p></div>', '{"user_name": "string", "login_time": "datetime", "ip_address": "string"}', true, true, '帳戶安全異常警示模板', NULL),
(2, 'ORDER_COMPLETED', '訂單完成模板', 'EMAIL', '訂單狀態', '訂單交易完成', true, '<h2>交易完成</h2><p>感謝您的購買，訂單 {{order_id}} 已完成。</p>', '{"user_name": "string", "order_id": "string", "product_name": "string"}', true, true, '訂單完成確認模板', NULL),
(1, 'WELCOME_NEW_USER', '新用戶歡迎模板', 'EMAIL', '系統通知', '歡迎加入我們', true, '<h1>歡迎 {{user_name}} 加入！</h1><p>感謝您註冊成為我們的會員。</p>', '{"user_name": "string", "registration_date": "datetime"}', true, true, '新用戶註冊歡迎訊息', 'welcome_banner.jpg'),
(4, 'SYSTEM_MAINTENANCE', '系統維護通知模板', 'PUSH', '更新公告', '系統維護通知', true, NULL, '{"maintenance_start": "datetime", "maintenance_end": "datetime", "affected_services": "array"}', true, true, '系統維護期間通知模板', NULL),
(2, 'REVIEW_REQUEST', '評價邀請模板', 'EMAIL', '評價提醒', '邀請您給予商品評價', true, '<h2>商品評價邀請</h2><p>{{user_name}}，請為您購買的 {{product_name}} 給予評價。</p>', '{"user_name": "string", "product_name": "string", "order_id": "string"}', true, false, '購買後評價邀請模板', NULL),
(5, 'PASSWORD_RESET', '密碼重設模板', 'EMAIL', '安全提醒', '密碼重設要求', true, '<h2>密碼重設</h2><p>點擊連結重設密碼：<a href="{{reset_link}}">重設密碼</a></p>', '{"user_name": "string", "reset_link": "string", "expire_time": "datetime"}', true, true, '密碼重設郵件模板', NULL),
(1, 'VIP_EXCLUSIVE', 'VIP專屬優惠模板', 'EMAIL', '會員專享', 'VIP會員專屬優惠', true, '<h1 style="color:gold;">VIP專屬優惠</h1><p>親愛的VIP會員 {{user_name}}，專屬優惠來了！</p>', '{"user_name": "string", "discount_code": "string", "valid_until": "datetime"}', true, false, 'VIP會員專屬優惠通知', 'vip_gold.jpg');

-- 4. 會員通知記錄表 (MEMBER_NOTIFICATION)
CREATE TABLE IF NOT EXISTS MEMBER_NOTIFICATION (
    NOTIFICATION_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    IS_READ BOOLEAN NOT NULL DEFAULT FALSE,
    READ_TIME DATETIME NULL,
    SENT_TIME DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DELIVERY_METHOD VARCHAR(20) NOT NULL,
    DELIVERY_STATUS TINYINT NOT NULL DEFAULT 0,
    RETRY_COUNT INT NOT NULL DEFAULT 0,
    LAST_RETRY_TIME DATETIME NULL,
    ERROR_MESSAGE VARCHAR(400) NULL,
    USER_INTERACTION TINYINT NOT NULL DEFAULT 0,
    INTERACTION_TIME DATETIME NULL,
    TRACKING_DATA JSON NULL,
    DEVICE_INFO VARCHAR(255) NULL,
    
    PRIMARY KEY (NOTIFICATION_ID, USER_ID),
    INDEX idx_user_id (USER_ID),
    INDEX idx_is_read (IS_READ),
    INDEX idx_sent_time (SENT_TIME),
    INDEX idx_delivery_status (DELIVERY_STATUS),
    INDEX idx_user_interaction (USER_INTERACTION),
    INDEX idx_read_time (READ_TIME),
    INDEX idx_notification_id (NOTIFICATION_ID),
    
    CONSTRAINT fk_member_notification 
        FOREIGN KEY (NOTIFICATION_ID) REFERENCES NOTIFICATION(NOTIFICATION_ID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_member_user 
        FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='會員通知記錄表 - 記錄每個用戶接收通知的詳細狀態';

-- 4. 通知會員紀錄表 10筆資料 
INSERT INTO MEMBER_NOTIFICATION (NOTIFICATION_ID, USER_ID, IS_READ, READ_TIME, SENT_TIME, DELIVERY_METHOD, DELIVERY_STATUS, RETRY_COUNT, LAST_RETRY_TIME, ERROR_MESSAGE, USER_INTERACTION, INTERACTION_TIME, TRACKING_DATA, DEVICE_INFO) VALUES
(1, 1, true, '2024-06-14 08:30:00', '2024-06-14 08:00:00', 'PUSH', 1, 0, NULL, NULL, 1, '2024-06-14 08:35:00', '{"opened": true, "clicked": true}', 'iPhone 12, iOS 15.0'),
(1, 2, false, NULL, '2024-06-14 08:00:00', 'EMAIL', 1, 0, NULL, NULL, 0, NULL, '{"opened": false}', NULL),
(2, 1, true, '2024-06-01 11:00:00', '2024-06-01 10:45:00', 'EMAIL', 1, 0, NULL, NULL, 1, '2024-06-01 11:05:00', '{"opened": true, "clicked": true}', 'Windows 10, Chrome'),
(3, 3, true, '2024-06-01 14:20:00', '2024-06-01 12:00:00', 'PUSH', 1, 0, NULL, NULL, 1, '2024-06-01 14:25:00', '{"opened": true}', 'Samsung Galaxy S21, Android 11'),
(4, 7, false, NULL, '2024-06-03 10:00:00', 'SMS', 2, 2, '2024-06-03 10:15:00', '號碼無效', 0, NULL, NULL, NULL),
(5, 8, true, '2024-06-05 16:30:00', '2024-06-05 15:00:00', 'PUSH', 1, 0, NULL, NULL, 2, '2024-06-05 16:35:00', '{"opened": true, "replied": true}', 'iPhone 13, iOS 16.0'),
(6, 4, false, NULL, '2024-06-01 09:00:00', 'EMAIL', 1, 0, NULL, NULL, 0, NULL, '{"opened": false}', NULL),
(8, 9, true, '2024-06-08 19:00:00', '2024-06-08 18:30:00', 'EMAIL', 1, 0, NULL, NULL, 1, '2024-06-08 19:10:00', '{"opened": true, "clicked": true}', 'MacBook Pro, Safari'),
(9, 6, false, NULL, '2024-06-01 10:00:00', 'PUSH', 1, 0, NULL, NULL, 3, '2024-06-01 10:05:00', '{"opened": true, "unsubscribed": true}', 'Xiaomi Mi 11, Android 12'),
(10, 8, true, '2024-06-10 20:15:00', '2024-06-10 20:00:00', 'EMAIL', 1, 0, NULL, NULL, 1, '2024-06-10 20:20:00', '{"opened": true}', 'iPad Pro, Safari');

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




-- 活動問題
CREATE TABLE IF NOT EXISTS ACTIVITY_QUESTIONS (
    QUESTION_ID INT NOT NULL AUTO_INCREMENT,
    ACTIVITY_ID INT NOT NULL,
    QUESTION_TEXT VARCHAR(255) NOT NULL,
    QUESTION_ORDER TINYINT,
    PRIMARY KEY (QUESTION_ID),
    FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY(ACTIVITY_ID)
);




-- 活動參與表
CREATE TABLE ACTIVITY_PARTICIPANT (
    PARTICIPANT_ID INT NOT NULL,
    ACTIVITY_ID INT NOT NULL,
    ADM_REVIEW_TIME DATETIME,
    PAR_STATUS TINYINT NOT NULL DEFAULT 0,
    APPLYING_DATE DATETIME NOT NULL,
    RATING TINYINT DEFAULT 5, 
    REVIEW_CONTENT VARCHAR(500), 
    REVIEW_TIME DATETIME, 
    PRIMARY KEY (PARTICIPANT_ID, ACTIVITY_ID),
    FOREIGN KEY (PARTICIPANT_ID) REFERENCES USERS(USER_ID),
    FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY(ACTIVITY_ID)
);



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


-- ===============================
-- ACTIVITY (活動) 共10筆
-- ===============================
INSERT INTO ACTIVITY 
(USER_ID, TITLE, CONTENT, IMAGE_URL, LOCATION, ACTIVITY_STATUS, CREATED_TIME, UPDATED_TIME, REG_START_TIME, REG_END_TIME, ACTIV_START_TIME, ACTIV_END_TIME, GENDER_FILTER, MAX_AGE, MIN_AGE, EXPIRED_TIME, MAX_PEOPLE, MIN_PEOPLE, SIGNUP_COUNT, RATING_COUNT, RATING, COMMENT_COUNT, REPORT_COUNT)
VALUES
(1, '夏季湖畔露營體驗', '在清涼的湖畔享受夏日露營，包含篝火晚會與獨木舟活動。', 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80', '南投魚池鄉', 2, '2025-05-01 08:00:00', '2025-06-01 10:00:00', '2025-05-01 08:00:00', '2025-05-20 23:59:59', '2025-06-01 18:00:00', '2025-06-03 12:00:00', 0, 50, 18, '2025-05-25 23:59:59', 10, 3, 9, 7, 4, 3, 0),
(2, '晨跑挑戰賽', '跟著我們一起晨跑，感受清新早晨的活力。', 'https://i.ibb.co/xqJcxdXz/photo-1740872853422-bcfb6c3c110f.jpg', '台北市大安森林公園', 1, '2025-06-10 09:00:00', '2025-06-20 09:00:00', '2025-06-10 09:00:00', '2025-06-25 23:59:59', '2025-06-26 06:00:00', '2025-06-26 08:00:00', 0, 40, 16, '2025-06-24 23:59:59', 10, 3, 4, 0, 0, 1, 0),
(3, '野外生存技能訓練', '學習如何在自然環境中求生，包含搭帳篷、生火和水源尋找。', 'https://i.ibb.co/N2jrFXGp/andreas-wagner-e-I-n-Ob1-K5g-E-unsplash.jpg', '花蓮秀林鄉', 1, '2025-06-15 12:00:00', '2025-06-25 12:00:00', '2025-06-15 12:00:00', '2025-06-27 23:59:59', '2025-07-01 08:00:00', '2025-07-03 18:00:00', 0, 45, 20, '2025-06-28 23:59:59', 10, 3, 3, 0, 0, 0, 1),
(4, '親子自然生態探索', '帶孩子一起認識自然，觀察植物與昆蟲。', 'https://i.ibb.co/SXsFPGDp/jessica-rockowitz-5-NLCaz2w-JXE-unsplash.jpg', '新竹五峰鄉', 0, '2025-06-20 10:00:00', '2025-06-20 10:00:00', '2025-07-01 00:00:00', '2025-07-10 23:59:59', '2025-07-15 08:00:00', '2025-07-15 17:00:00', 0, 50, 5, '2025-07-05 23:59:59', 10, 3, 0, 0, 0, 0, 0),
(5, '陶藝入門工作坊', '透過親手製作陶藝，體驗泥土的魅力與創作樂趣。', 'https://i.ibb.co/DgVRXSQ8/earl-wilcox-i-Ubsw-VOkb-M-unsplash.jpg', '台中市西區', 0, '2025-06-18 09:00:00', '2025-06-18 09:00:00', '2025-06-20 00:00:00', '2025-07-10 23:59:59', '2025-07-20 10:00:00', '2025-07-20 16:00:00', 0, 40, 18, '2025-07-15 23:59:59', 10, 3, 2, 0, 0, 0, 0),
(6, '文學讀書會：夏季特輯', '每週一起分享當代文學佳作，增進閱讀深度與交流。', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=800&q=80', '台北市信義區', 0, '2025-06-22 15:00:00', '2025-06-22 15:00:00', '2025-07-05 00:00:00', '2025-07-25 23:59:59', '2025-07-30 19:00:00', '2025-07-30 21:00:00', 0, 60, 20, '2025-07-28 23:59:59', 10, 3, 0, 0, 0, 0, 0),
(7, '瑜伽放鬆工作坊', '從基礎瑜伽動作開始，舒緩壓力並提升身心靈健康。', 'https://i.ibb.co/Q7wS8FCL/ginny-rose-stewart-Uxkc-Sz-RWM2s-unsplash.jpg', '台南市中西區', 1, '2025-06-10 08:00:00', '2025-06-25 08:00:00', '2025-06-12 00:00:00', '2025-07-01 23:59:59', '2025-07-05 08:00:00', '2025-07-05 10:00:00', 0, 55, 18, '2025-07-02 23:59:59', 10, 3, 6, 0, 0, 3, 1),
(8, '攝影愛好者聚會', '聚集喜愛攝影的朋友，分享技巧與作品。', 'https://i.ibb.co/prk0knss/alif-ngoylung-jg-6-ARMia-PM-unsplash.jpg', '高雄市鼓山區', 0, '2025-07-01 10:00:00', '2025-07-01 10:00:00', '2025-07-10 00:00:00', '2025-07-25 23:59:59', '2025-07-30 09:00:00', '2025-07-30 17:00:00', 0, 60, 15, '2025-07-25 23:59:59', 10, 3, 1, 0, 0, 1, 0),
(9, '熱氣球飛行體驗', '搭乘熱氣球俯瞰壯麗景色，體驗難忘的高空之旅。', 'https://i.ibb.co/CpRMwYTr/kyle-hinkson-xy-Xc-GADv-Aw-E-unsplash.jpg', '台東縣鹿野鄉', 0, '2025-06-25 08:00:00', '2025-06-25 08:00:00', '2025-07-01 00:00:00', '2025-07-10 23:59:59', '2025-07-15 05:00:00', '2025-07-15 07:00:00', 0, 50, 18, '2025-07-12 23:59:59', 10, 3, 0, 0, 0, 0, 0),
(10, '手沖咖啡工作坊', '教你如何掌握手沖咖啡技巧，品味一杯香濃好咖啡。', 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=800&q=80', '台北市大同區', 0, '2025-07-05 09:00:00', '2025-07-05 09:00:00', '2025-07-15 00:00:00', '2025-07-30 23:59:59', '2025-08-05 14:00:00', '2025-08-05 17:00:00', 0, 45, 20, '2025-07-28 23:59:59', 10, 3, 0, 0, 0, 0, 0);





-- ===============================
-- ACTIVITY_PARTICIPANT (活動參與)
-- 不包含團主，且不包含 USER_ID 欄位
-- ===============================
INSERT INTO ACTIVITY_PARTICIPANT
(PARTICIPANT_ID, ACTIVITY_ID, ADM_REVIEW_TIME, PAR_STATUS, APPLYING_DATE, RATING, REVIEW_CONTENT, REVIEW_TIME)
VALUES
-- 活動1 已結束，8人參與（團主為1號，未列入）
(2, 1, '2025-05-25 10:00:00', 2, '2025-05-10 08:00:00', 5, '非常棒的露營體驗，期待下次！', '2025-06-04 20:00:00'),
(3, 1, '2025-05-25 11:00:00', 2, '2025-05-11 09:00:00', 4, '活動安排很豐富，值得推薦。', '2025-06-05 21:00:00'),
(4, 1, '2025-05-25 12:00:00', 2, '2025-05-12 14:00:00', 5, '導師講解詳細，環境優美。', '2025-06-05 19:00:00'),
(5, 1, '2025-05-25 13:00:00', 2, '2025-05-13 10:00:00', 4, '很喜歡獨木舟活動。', '2025-06-06 20:00:00'),
(6, 1, '2025-05-25 14:00:00', 2, '2025-05-14 15:30:00', 3, '露營設備有待提升。', '2025-06-07 18:00:00'),
(7, 1, '2025-05-25 15:00:00', 2, '2025-05-15 09:00:00', 5, '星空講座精彩絕倫。', '2025-06-07 20:00:00'),
(8, 1, '2025-05-25 16:00:00', 2, '2025-05-16 16:00:00', 4, '希望增加互動遊戲。', '2025-06-08 19:00:00'),
(9, 1, '2025-05-25 17:00:00', 2, '2025-05-17 14:00:00', 5, '難忘的夏夜體驗。', '2025-06-08 21:00:00'),

-- 活動2 已結束，4人參與（不含團主）
(3, 2, '2025-06-20 10:00:00', 2, '2025-06-10 09:00:00', 4, '晨跑氣氛很好，感覺身體更健康。', '2025-06-27 07:00:00'),
(4, 2, '2025-06-20 11:00:00', 2, '2025-06-11 11:00:00', 3, '能持續跑完全程，成就感滿滿。', '2025-06-27 07:30:00'),
(5, 2, '2025-06-20 12:00:00', 2, '2025-06-12 09:00:00', 5, '結識很多新朋友，活動棒極了！', '2025-06-27 08:00:00'),
(6, 2, '2025-06-20 13:00:00', 2, '2025-06-13 10:00:00', 4, '組織得很好，值得參加。', '2025-06-27 08:30:00'),

-- 活動3 報名結束，活動尚未開始，3人參與
(4, 3, '2025-06-27 10:00:00', 2, '2025-06-15 14:00:00', NULL, NULL, NULL),
(5, 3, '2025-06-27 11:00:00', 2, '2025-06-16 09:00:00', NULL, NULL, NULL),
(6, 3, '2025-06-27 12:00:00', 2, '2025-06-17 13:00:00', NULL, NULL, NULL),

-- 活動5 報名中，2人參與
(7, 5, '2025-06-25 10:00:00', 2, '2025-06-20 09:00:00', NULL, NULL, NULL),
(8, 5, '2025-06-25 11:00:00', 2, '2025-06-21 10:30:00', NULL, NULL, NULL),

-- 活動7 報名結束，等待活動中，6人參與
(3, 7, '2025-07-01 09:00:00', 2, '2025-06-15 08:00:00', NULL, NULL, NULL),
(4, 7, '2025-07-01 10:00:00', 2, '2025-06-16 10:00:00', NULL, NULL, NULL),
(5, 7, '2025-07-01 11:00:00', 2, '2025-06-17 12:00:00', NULL, NULL, NULL),
(6, 7, '2025-07-01 12:00:00', 2, '2025-06-18 13:00:00', NULL, NULL, NULL),
(7, 7, '2025-07-01 13:00:00', 2, '2025-06-19 14:00:00', NULL, NULL, NULL),
(8, 7, '2025-07-01 14:00:00', 2, '2025-06-20 15:00:00', NULL, NULL, NULL),

-- 活動8 報名中，1人參與
(9, 8, '2025-07-10 10:00:00', 2, '2025-07-05 09:00:00', NULL, NULL, NULL);


-- ===============================
-- ACTIVITY_QUESTIONS (活動問題)
-- ===============================
INSERT INTO ACTIVITY_QUESTIONS
(QUESTION_ID, ACTIVITY_ID, QUESTION_TEXT, QUESTION_ORDER)
VALUES
(1, 1, '你是否有過露營經驗？', 1),
(2, 1, '你希望參與哪些活動項目？', 2),

(3, 2, '你跑步的主要目標是什麼？', 1),

(4, 3, '你具備哪些野外生存技能？', 1),
(5, 3, '你有參加過類似的挑戰嗎？', 2),

(6, 4, '孩子的年齡是？', 1),
(7, 4, '孩子是否對自然生態感興趣？', 2),

(8, 5, '你有無陶藝製作經驗？', 1),

(9, 6, '你最喜歡哪類型的文學作品？', 1),

(10, 7, '你是否曾練習瑜伽？', 1),

(11, 8, '你喜歡攝影的哪些題材？', 1),

(12, 9, '你搭乘熱氣球的動機是？', 1),

(13, 10, '你有喝過手沖咖啡嗎？', 1);

-- ===============================
-- ACTIVITY_ANSWERS (活動回答)
-- ===============================
INSERT INTO ACTIVITY_ANSWERS
(ACTIVITY_ID, QUESTION_ID, USER_ID, ANSWER_TEXT)
VALUES
-- 活動1 問題1與2回答者：2,3,5
(1, 1, 2, '是的，我去年也參加過，覺得很有趣！'),
(1, 2, 2, '想參加獨木舟和篝火晚會'),
(1, 1, 3, '第一次參加，期待體驗'),
(1, 2, 3, '喜歡戶外活動，想多了解生火技巧'),
(1, 1, 5, '之前參加過，期待再體驗'),
(1, 2, 5, '希望增加團康遊戲'),

-- 活動2 問題3回答者：3,7,9
(2, 3, 3, '提升體能，健康為主'),
(2, 3, 7, '想挑戰自己，跑完10公里'),
(2, 3, 9, '保持體力和交朋友'),

-- 活動3 問題4與5回答者：5,6,10
(3, 4, 5, '會搭帳篷和生火'),
(3, 5, 5, '參加過一次野外挑戰'),
(3, 4, 6, '懂得基本生火技巧'),
(3, 5, 6, '沒有類似經驗，期待學習'),
(3, 4, 10, '野外求生新手'),
(3, 5, 10, '無經驗，想嘗試'),

-- 活動4 問題6與7回答者：6,8
(4, 6, 6, '7歲'),
(4, 7, 6, '非常感興趣'),
(4, 6, 8, '5歲'),
(4, 7, 8, '還好，喜歡戶外'),

-- 活動5 問題8回答者：7,8
(5, 8, 7, '沒有陶藝經驗，想學習'),
(5, 8, 8, '之前學過一些基礎'),

-- 活動6 問題9回答者：9
(6, 9, 9, '喜歡現代小說'),

-- 活動7 問題10回答者：10
(7, 10, 10, '有，已練習一年');

-- ===============================
-- ACTIVITY_COMMENTS (活動留言)
-- ===============================
INSERT INTO ACTIVITY_COMMENTS
(COMMENT_ID, ACTIVITY_ID, USER_ID, CONTENT, PARENT_COMMENT_ID, COMMENT_TIME, COMMENT_COUNT)
VALUES
(1, 1, 2, '這次露營真棒，風景美極了！', NULL, '2025-06-04 21:00:00', 2),
(2, 1, 3, '同意！特別是星空很震撼！', 1, '2025-06-04 22:00:00', 0),
(3, 1, 5, '設備有點不足，希望下次改進。', NULL, '2025-06-05 10:00:00', 0),
(4, 2, 4, '晨跑活動時間安排得剛好，感謝團主！', NULL, '2025-06-26 09:00:00', 0),
(5, 7, 6, '瑜伽課程很放鬆，老師專業。', NULL, '2025-07-06 12:00:00', 0),
(6, 7, 2, '可以增加難度多點挑戰。', NULL, '2025-07-06 14:00:00', 1),
(7, 7, 6, '謝謝建議，下次會考慮。', 6, '2025-07-06 15:00:00', 0),
(8, 8, 10, '攝影分享很有收穫，期待下次活動。', NULL, '2025-07-31 18:00:00', 0);

-- ===============================
-- ACTIVITY_TRACKING (活動追蹤)
-- ===============================
INSERT INTO ACTIVITY_TRACKING
(ACTIVITY_ID, USER_ID, TRACKING_TIME, TRACKING_STATE)
VALUES
(1, 2, '2025-05-05 08:00:00', 0),
(1, 3, '2025-05-06 09:00:00', 0),
(2, 4, '2025-06-15 10:00:00', 0),
(3, 5, '2025-06-20 11:00:00', 0),
(4, 6, '2025-06-25 12:00:00', 0),
(5, 7, '2025-07-01 13:00:00', 0),
(6, 8, '2025-07-05 14:00:00', 0),
(7, 9, '2025-07-10 15:00:00', 0),
(8, 10, '2025-07-15 16:00:00', 0),
(1, 4, '2025-05-10 17:00:00', 1); -- 取消追蹤示範

-- ===============================
-- ACTIVITY_REPORTS (活動檢舉)
-- ===============================
INSERT INTO ACTIVITY_REPORTS
(USER_ID, ACTIVITY_ID, RP_REASON, RP_CONTENT, RP_PIC, RP_TIME, ADM_ID, RP_DONE_TIME, RP_STATUS, RP_NOTE)
VALUES
(5, 3, 1, '疑似含有廣告連結，請查核', NULL, '2025-06-28 10:00:00', 1, '2025-06-29 15:00:00', 1, '經審核為誤報，無進一步處理'),
(6, 7, 0, '活動內容不符實際描述，請調查', NULL, '2025-07-07 11:00:00', 2, NULL, 0, NULL);

