package com.shakemate.user.dto;

import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {

    @NotNull(message = "使用者ID為必填")
    private Integer userId;

    @NotEmpty(message = "請輸入會員名稱")
    @Size(max = 50, message = "會員名稱不得超過 50 字")
    private String username;

    @NotEmpty(message = "請輸入Email")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotNull(message = "性別為必填")
    @Min(value = 0, message = "性別只接受 0 或 1")
    @Max(value = 1, message = "性別只接受 0 或 1")
    private Byte gender;

    @NotNull(message = "生日為必填")
    private Date birthday;

    @NotEmpty(message = "請輸入居住地")
    @Size(max = 50, message = "居住區域不得超過 50 字")
    private String location;

    @NotEmpty(message = "請輸入自我介紹")
    @Size(max = 200, message = "關於我不得超過 200 字")
    private String intro;

    @NotNull(message = "帳號狀態為必填")
    @Min(value = 1, message = "帳號狀態最小為 1")
    @Max(value = 3, message = "帳號狀態最大為 3")
    private Byte userStatus;

    // Getter / Setter
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

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
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

    public Byte getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Byte userStatus) {
        this.userStatus = userStatus;
    }

}
