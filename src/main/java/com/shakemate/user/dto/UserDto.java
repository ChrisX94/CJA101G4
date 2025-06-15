package com.shakemate.user.dto;

import com.shakemate.user.model.Users;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private Integer userId;
    private String username;
    private String email;
    private String pwd;
    private byte gender;
    private Date birthday;
    private String location;
    private String intro;
    private Timestamp createdTime;
    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String img5;
    private String interests;
    private String personality;
    private Timestamp updatedTime;
    private byte userStatus;
    private Boolean postStatus;
    private Boolean atAcStatus;
    private Boolean sellStatus;

    public UserDto SimpleUserDto(Users user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setImg1(user.getImg1());
        dto.setSellStatus(user.getSellStatus());
        return dto ;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                '}';
    }
}
