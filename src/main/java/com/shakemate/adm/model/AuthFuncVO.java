package com.shakemate.adm.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "AUTH_FUNC")
public class AuthFuncVO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Integer authId;
    private String authName;
    private String authDes;

    public AuthFuncVO() {
    }

    @Id
    @Column(name = "AUTH_ID")
    public Integer getAuthId() {
        return this.authId;
    }

    public void setAuthId(Integer authId) {
        this.authId = authId;
    }

    @Column(name = "AUTH_NAME")
    public String getAuthName() {
        return this.authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    @Column(name = "AUTH_DES")
    public String getAuthDes() {
        return this.authDes;
    }

    public void setAuthDes(String authDes) {
        this.authDes = authDes;
    }
    
    
    private Set<AdmAuthVO> admAuths = new HashSet<>();
    
    @OneToMany(mappedBy = "authFuncVO", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<AdmAuthVO> getAdmAuths() {
        return admAuths;
    }

    public void setAdmAuths(Set<AdmAuthVO> admAuths) {
        this.admAuths = admAuths;
    }
}
