package com.shakemate.shshop.model;



import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "SH_PROD_RP")
public class ShProdRp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROD_RP_ID")
    private Integer prodRpId;

    @Column(name = "PROD_RP_REASON", nullable = false)
    private byte prodRpReason;  // 0~4 對應 enum 可加以封裝處理

    @Column(name = "PROD_RP_CONTENT", length = 800, nullable = false)
    private String prodRpContent;

    @Column(name = "PROD_RP_PIC", length = 800)
    private String prodRpPic;

    @Column(name = "PROD_RP_TIME", nullable = false)
    @CreationTimestamp
    private Timestamp prodRpTime;


    @Column(name = "PROD_RP_DONE_TIME")
    private Timestamp prodRpDoneTime;

    @Column(name = "PROD_RP_STATUS", nullable = false)
    private byte prodRpStatus; // 0: 未處理, 1: 通過, 2: 不通過

    @Column(name = "PROD_RP_NOTE", length = 800)
    private String prodRpNote;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "PROD_ID", nullable = false)
    private ShProd shProd;

    /* 等到AMD做好再來取代 */
    @Column(name = "ADM_ID")
    private Integer admId;
//    @ManyToOne
//    @JoinColumn(name = "ADM_ID")
//    private Admin admin;
    /******************************/


    public ShProdRp() {
        super();
    }

    public ShProdRp(Integer prodRpId, byte prodRpReason, String prodRpContent, String prodRpPic, Timestamp prodRpTime, Timestamp prodRpDoneTime, byte prodRpStatus, String prodRpNote, Users users, ShProd shProd, Integer admId) {
        this.prodRpId = prodRpId;
        this.prodRpReason = prodRpReason;
        this.prodRpContent = prodRpContent;
        this.prodRpPic = prodRpPic;
        this.prodRpTime = prodRpTime;
        this.prodRpDoneTime = prodRpDoneTime;
        this.prodRpStatus = prodRpStatus;
        this.prodRpNote = prodRpNote;
        this.users = users;
        this.shProd = shProd;
        this.admId = admId;
    }

    public ShProdRp(Users users, ShProd shProd, byte reason, String content, String pic, Integer admId, String note) {
        this.users = users;
        this.shProd = shProd;
        this.prodRpReason = reason;
        this.prodRpContent = content;
        this.prodRpPic = pic;
//        this.prodRpTime = new Timestamp(System.currentTimeMillis());
        this.admId = admId;
        this.prodRpDoneTime = null;
        this.prodRpStatus = 0;
        this.prodRpNote = note;
    }

    public Integer getProdRpId() {
        return prodRpId;
    }

    public void setProdRpId(Integer prodRpId) {
        this.prodRpId = prodRpId;
    }

    public byte getProdRpReason() {
        return prodRpReason;
    }

    public void setProdRpReason(byte prodRpReason) {
        this.prodRpReason = prodRpReason;
    }

    public String getProdRpContent() {
        return prodRpContent;
    }

    public void setProdRpContent(String prodRpContent) {
        this.prodRpContent = prodRpContent;
    }

    public String getProdRpPic() {
        return prodRpPic;
    }

    public void setProdRpPic(String prodRpPic) {
        this.prodRpPic = prodRpPic;
    }

    public Timestamp getProdRpTime() {
        return prodRpTime;
    }

    public void setProdRpTime(Timestamp prodRpTime) {
        this.prodRpTime = prodRpTime;
    }

    public Timestamp getProdRpDoneTime() {
        return prodRpDoneTime;
    }

    public void setProdRpDoneTime(Timestamp prodRpDoneTime) {
        this.prodRpDoneTime = prodRpDoneTime;
    }

    public byte getProdRpStatus() {
        return prodRpStatus;
    }

    public void setProdRpStatus(byte prodRpStatus) {
        this.prodRpStatus = prodRpStatus;
    }

    public String getProdRpNote() {
        return prodRpNote;
    }

    public void setProdRpNote(String prodRpNote) {
        this.prodRpNote = prodRpNote;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public ShProd getShProd() {
        return shProd;
    }

    public void setShProd(ShProd shProd) {
        this.shProd = shProd;
    }

    public Integer getAdmId() {
        return admId;
    }

    public void setAdmId(Integer admId) {
        this.admId = admId;
    }

    @Override
    public String toString() {
        return "ShProdRp{" +
                "prodRpId=" + prodRpId +
                ", prodRpReason=" + prodRpReason +
                ", prodRpContent='" + prodRpContent + '\'' +
                ", prodRpTime=" + prodRpTime +
                ", prodRpDoneTime=" + prodRpDoneTime +
                ", prodRpStatus=" + prodRpStatus +
                ", prodRpNote='" + prodRpNote + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShProdRp shProdRp)) return false;
        return Objects.equals(prodRpId, shProdRp.prodRpId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prodRpId);
    }
}