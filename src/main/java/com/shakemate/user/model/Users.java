package com.shakemate.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shakemate.ordermaster.model.ShOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "USERS")
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Integer userId;

    @Size(min = 1, max = 50, message = "名稱必須為 1~50 字")
    @NotEmpty(message = "請輸入使用者名稱")
    @Column(name = "USERNAME", nullable = false, length = 50)
    private String username;

    @NotEmpty(message = "請輸入Email")
    @Column(name = "EMAIL", nullable = false, length = 50, unique = true)
    private String email;

    @NotEmpty(message = "請輸入密碼")
    @Column(name = "PWD", nullable = false, length = 255)
    private String pwd;

    @Min(value = 0, message = "性別只能為 0 或 1")
    @Max(value = 1, message = "性別只能為 0 或 1")
    @NotNull(message = "性別為必填欄位")
    @Column(name = "GENDER", nullable = false)
    private byte gender;

    @NotNull(message = "生日為必填")
    @Column(name = "BIRTHDAY")
    private Date birthday;

    @NotEmpty(message = "請輸入居住地")
    @Column(name = "LOCATION", length = 50)
    private String location;

    @Column(name = "INTRO", length = 200)
    private String intro;

    @Column(name = "CREATED_TIME", nullable = false)
    private Timestamp createdTime;

    @Column(name = "IMG1", length = 300)
    private String img1;

    @Column(name = "IMG2", length = 300)
    private String img2;

    @Column(name = "IMG3", length = 300)
    private String img3;

    @Column(name = "IMG4", length = 300)
    private String img4;

    @Column(name = "IMG5", length = 300)
    private String img5;

    @Transient
    private MultipartFile[] uploadedImages;

    public MultipartFile[] getUploadedImages() {
        return uploadedImages;
    }

    public void setUploadedImages(MultipartFile[] uploadedImages) {
        this.uploadedImages = uploadedImages;
    }

    @Transient
    private List<String> photos;

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    @Column(name = "INTERESTS", length = 300)
    private String interests;

    @Column(name = "PERSONALITY", length = 300)
    private String personality;

    @Transient
    private List<String> interestsList;

    @Transient
    private List<String> personalityList;

    @Column(name = "UPDATED_TIME", nullable = false)
    private Timestamp updatedTime;

    @Column(name = "USER_STATUS", nullable = false)
    private byte userStatus;

    @Column(name = "POST_STATUS", nullable = false)
    private Boolean postStatus;

    @Column(name = "AT_AC_STATUS", nullable = false)
    private Boolean atAcStatus;

    @Column(name = "SELL_STATUS", nullable = false)
    private Boolean sellStatus;

    @OneToMany(mappedBy = "buyer")
    @JsonIgnore
    private List<ShOrder> buyerOrders;

    @OneToMany(mappedBy = "seller")
    @JsonIgnore
    private List<ShOrder> sellerOrders;

    public Users() {
    }

    public Users(Integer userId, String username, String email, String pwd, byte gender, Date birthday, String location,
            String intro, Timestamp createdTime, String interests, String personality, Timestamp updatedTime,
            byte userStatus, Boolean postStatus, Boolean atAcStatus, Boolean sellStatus) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.pwd = pwd;
        this.gender = gender;
        this.birthday = birthday;
        this.location = location;
        this.intro = intro;
        this.createdTime = createdTime;
        this.interests = interests;
        this.personality = personality;
        this.updatedTime = updatedTime;
        this.userStatus = userStatus;
        this.postStatus = postStatus;
        this.atAcStatus = atAcStatus;
        this.sellStatus = sellStatus;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getImg5() {
        return img5;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public List<String> getInterestsList() {
        return interestsList;
    }

    public void setInterestsList(List<String> interestsList) {
        this.interestsList = interestsList;
    }

    public List<String> getPersonalityList() {
        return personalityList;
    }

    public void setPersonalityList(List<String> personalityList) {
        this.personalityList = personalityList;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    public byte getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(byte userStatus) {
        this.userStatus = userStatus;
    }

    public Boolean getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Boolean postStatus) {
        this.postStatus = postStatus;
    }

    public Boolean getAtAcStatus() {
        return atAcStatus;
    }

    public void setAtAcStatus(Boolean atAcStatus) {
        this.atAcStatus = atAcStatus;
    }

    public Boolean getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(Boolean sellStatus) {
        this.sellStatus = sellStatus;
    }

    public List<ShOrder> getBuyerOrders() {
        return buyerOrders;
    }

    public void setBuyerOrders(List<ShOrder> buyerOrders) {
        this.buyerOrders = buyerOrders;
    }

    public List<ShOrder> getSellerOrders() {
        return sellerOrders;
    }

    public void setSellerOrders(List<ShOrder> sellerOrders) {
        this.sellerOrders = sellerOrders;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", username='" + username +
                ", email='" + email +
                ", pwd='" + pwd +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", location='" + location +
                ", intro='" + intro +
                ", createdTime=" + createdTime +
                ", interests='" + interests +
                ", personality='" + personality +
                ", updatedTime=" + updatedTime +
                ", userStatus=" + userStatus +
                ", postStatus=" + postStatus +
                ", atAcStatus=" + atAcStatus +
                ", sellStatus=" + sellStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Users users))
            return false;
        return Objects.equals(userId, users.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
