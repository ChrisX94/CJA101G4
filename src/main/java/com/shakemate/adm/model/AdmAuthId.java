package com.shakemate.adm.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AdmAuthId implements Serializable {

    @Column(name = "ADM_ID")
    private Integer admId;

    @Column(name = "AUTH_ID")
    private Integer authId;

    public AdmAuthId() {}

    public AdmAuthId(Integer admId, Integer authId) {
        this.admId = admId;
        this.authId = authId;
    }

    // Getter & Setter

    public Integer getAdmId() {
        return admId;
    }

    public void setAdmId(Integer admId) {
        this.admId = admId;
    }

    public Integer getAuthId() {
        return authId;
    }

    public void setAuthId(Integer authId) {
        this.authId = authId;
    }

    // equals() & hashCode() → 複合 PK 一定要 override 這兩個！！！
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdmAuthId that = (AdmAuthId) o;
        return admId.equals(that.admId) && authId.equals(that.authId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admId, authId);
    }
}

