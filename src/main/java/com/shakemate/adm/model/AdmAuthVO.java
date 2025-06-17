package com.shakemate.adm.model;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "ADM_AUTH")
public class AdmAuthVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AdmAuthId id;

	@ManyToOne
	@MapsId("admId") // 對應到 EmbeddedId 裡的 admId 欄位
	@JoinColumn(name = "ADM_ID", nullable = false)
	private AdmVO admVO;

	@ManyToOne
	@MapsId("authId") // 對應到 EmbeddedId 裡的 authId 欄位
	@JoinColumn(name = "AUTH_ID", nullable = false)
	private AuthFuncVO authFuncVO;

	public AdmAuthVO() {
	}

	public AdmAuthId getId() {
		return id;
	}

	public void setId(AdmAuthId id) {
		this.id = id;
	}

	public AdmVO getAdmVO() {
		return admVO;
	}

	public void setAdmVO(AdmVO admVO) {
		this.admVO = admVO;
	}

	public AuthFuncVO getAuthFuncVO() {
		return authFuncVO;
	}

	public void setAuthFuncVO(AuthFuncVO authFuncVO) {
		this.authFuncVO = authFuncVO;
	}

}
