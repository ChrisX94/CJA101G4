package com.shakemate.adm.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ADM")
public class AdmVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ADM_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer admId;

	@Column(name = "ADM_NAME")
	@NotEmpty(message = "管理員姓名: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "管理員姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String admName;

	@Column(name = "ADM_ACC")
	@NotEmpty(message = "管理員帳號: 請勿空白")
	@Size(min = 2, max = 20, message = "管理員帳號: 長度必需在{min}到{max}之間")
	private String admAcc;

	// 非資料庫欄位：用來裝使用者輸入的明碼密碼
	@Transient
	@Size(min = 6, message = "密碼至少要 6 碼")
	private String inputPwd;

	// 真正要存進資料庫的密碼（加密過的）
	@Column(name = "ADM_PWD")
	@Size(min = 60, max = 60, message = "管理員密碼HASH長度不符")
	private String admPwd;

	// 狀態欄位
	@Column(name = "ADM_STA")
	private boolean admSta;

	// 一對多：中介表 adm_auth 對應實體類別 AdmAuthVO
	@OneToMany(mappedBy = "admVO", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<AdmAuthVO> admAuths = new HashSet<>();

	// 多對多：查詢權限用
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "adm_auth", joinColumns = @JoinColumn(name = "adm_id"), inverseJoinColumns = @JoinColumn(name = "auth_id"))
	private Set<AuthFuncVO> authFuncs = new HashSet<>();

	// ===== Getter / Setter =====

	public Integer getAdmId() {
		return admId;
	}

	public void setAdmId(Integer admId) {
		this.admId = admId;
	}

	public String getAdmName() {
		return admName;
	}

	public void setAdmName(String admName) {
		this.admName = admName;
	}

	public String getAdmAcc() {
		return admAcc;
	}

	public void setAdmAcc(String admAcc) {
		this.admAcc = admAcc;
	}

	public String getAdmPwd() {
		return admPwd;
	}

	public void setAdmPwd(String admPwd) {
		this.admPwd = admPwd;
	}

	public Set<AdmAuthVO> getAdmAuths() {
		return admAuths;
	}

	public void setAdmAuths(Set<AdmAuthVO> admAuths) {
		this.admAuths = admAuths;
	}

	public Set<AuthFuncVO> getAuthFuncs() {
		return authFuncs;
	}

	public void setAuthFuncs(Set<AuthFuncVO> authFuncs) {
		this.authFuncs = authFuncs;
	}

	public String getInputPwd() {
		return inputPwd;
	}

	public void setInputPwd(String inputPwd) {
		this.inputPwd = inputPwd;
	}

	public boolean getAdmSta() {
		return admSta;
	}

	public void setAdmSta(boolean admSta) {
		this.admSta = admSta;
	}

	// 新增getStatus/setStatus 方法（對應 admSta）
	public Boolean getStatus() {
		return admSta;
	}

	public void setStatus(Boolean status) {
		this.admSta = (status != null && status);
	}
}
