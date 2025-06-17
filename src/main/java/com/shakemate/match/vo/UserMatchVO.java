package com.shakemate.match.vo;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "user_matches")
public class UserMatchVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "match_id")
	private int matchId;

	@Column(name = "action_user_id", nullable = false)
	private int actionUserId;

	@Column(name = "target_user_id", nullable = false)
	private int targetUserId;

	@Column(name = "action_type", nullable = false)
	private int actionType; // 0=like、1=dislike、2=matched

	@Column(name = "action_time", nullable = false)
	private LocalDateTime actionTime;

	public UserMatchVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserMatchVO(int matchId, int actionUserId, int targetUserId, int actionType, LocalDateTime actionTime) {
		super();
		this.matchId = matchId;
		this.actionUserId = actionUserId;
		this.targetUserId = targetUserId;
		this.actionType = actionType;
		this.actionTime = actionTime;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public int getActionUserId() {
		return actionUserId;
	}

	public void setActionUserId(int actionUserId) {
		this.actionUserId = actionUserId;
	}

	public int getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(int targetUserId) {
		this.targetUserId = targetUserId;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public LocalDateTime getActionTime() {
		return actionTime;
	}

	public void setActionTime(LocalDateTime actionTime) {
		this.actionTime = actionTime;
	}
	
}
