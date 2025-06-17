package com.shakemate.adm.model;

import java.io.IOException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Past;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;

@Entity
@Table(name = "ADM")
public class AdmVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer admId;
	private String admName;
	private String admAcc;
	private String admPwd;

	public AdmVO() {
	}

	@Id
	@Column(name = "ADM_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getAdmId() {
		return this.admId;
	}

	public void setAdmId(Integer admId) {
		this.admId = admId;
	}

	@Column(name = "ADM_NAME")
	@NotEmpty(message = "管理員姓名: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "管理員姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	public String getAdmName() {
		return this.admName;
	}

	public void setAdmName(String admName) {
		this.admName = admName;
	}

	@Column(name = "ADM_ACC")
	@NotEmpty(message = "管理員帳號: 請勿空白")
	@Size(min = 2, max = 10, message = "管理員帳號: 長度必需在{min}到{max}之間")
	public String getAdmAcc() {
		return this.admAcc;
	}

	public void setAdmAcc(String admAcc) {
		this.admAcc = admAcc;
	}

	@Column(name = "ADM_PWD")
	@NotEmpty(message = "管理員密碼: 請勿空白")
	@Size(min = 60, max = 60, message = "管理員密碼HASH長度不符")
	public String getAdmPwd() {
		return this.admPwd;
	}

	public void setAdmPwd(String admPwd) {
		this.admPwd = admPwd;
	}

	private Set<AdmAuthVO> admAuths = new HashSet<>();

	@OneToMany(mappedBy = "admVO", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public Set<AdmAuthVO> getAdmAuths() {
		return admAuths;
	}

	public void setAdmAuths(Set<AdmAuthVO> admAuths) {
		this.admAuths = admAuths;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "adm_auth", // 中介表名稱
			joinColumns = @JoinColumn(name = "adm_id"), // 本表欄位
			inverseJoinColumns = @JoinColumn(name = "auth_id") // 對方表欄位
	)

	public Set<AuthFuncVO> getAuthFuncs() {
		return authFuncs;
	}

	public void setAuthFuncs(Set<AuthFuncVO> authFuncs) {
		this.authFuncs = authFuncs;
	}

	@Transient
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "adm_auth", joinColumns = @JoinColumn(name = "adm_id"), inverseJoinColumns = @JoinColumn(name = "auth_id"))
	private Set<AuthFuncVO> authFuncs = new HashSet<>();

}
