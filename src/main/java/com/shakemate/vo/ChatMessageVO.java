package com.shakemate.vo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
public class ChatMessageVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int messageId;

    @Column(name = "room_id")
    private int roomId;

    @Column(name = "sender_id")
    private int senderId;

    @Column(name = "message_content")
    private String content;

    @Lob
    @Column(name = "message_img", columnDefinition = "LONGBLOB")
    private byte[] imgBytes;

    @Column(name = "sent_time", insertable = false, updatable = false)
    private String sentTime;

    @Column(name = "is_read")
    private boolean isRead;

    @Transient // 非資料庫欄位，只是給前端用
    private String imgBase64;

    public ChatMessageVO() {
    	super();
    }

	public ChatMessageVO(int messageId, int roomId, int senderId, String content, byte[] imgBytes, String sentTime, boolean isRead) {
		super();
		this.messageId = messageId;
		this.roomId = roomId;
		this.senderId = senderId;
		this.content = content;
		this.imgBytes = imgBytes;
		this.sentTime = sentTime;
		this.isRead = isRead;
	}
	
	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSentTime() {
		return sentTime;
	}

	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}


	public byte[] getImgBytes() {
		return imgBytes;
	}

	public void setImgBytes(byte[] imgBytes) {
		this.imgBytes = imgBytes;
	}

	public boolean isRead() {
		return isRead;
	}


	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getImgBase64() {
		return imgBase64;
	}

	public void setImgBase64(String imgBase64) {
		this.imgBase64 = imgBase64;
	}
	
	
}
