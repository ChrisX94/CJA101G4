package com.shakemate.activity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * 表格名稱：活動（ACTIVITY）
 * 主鍵：ACTIVITY_ID
 * 外來鍵：USER_ID → USERS(USER_ID)
 *
 *
 * +-----+-------------------+------------------+-------------+----------+---------------------------------------------+
 * | No. | Column Name       | Description      | Type        | Length   | Notes                                       |
 * +-----+-------------------+------------------+-------------+----------+---------------------------------------------+
 * |  1  | ACTIVITY_ID       | 活動編號         | INT         |          | PK, AI, Not Null                            |
 * |  2  | USER_ID           | 發起者           | INT         |          | FK, Not Null                                |
 * |  3  | TITLE             | 活動標題         | VARCHAR     | 50       | Not Null                                    |
 * |  4  | CONTENT           | 活動內文         | VARCHAR     | 1000     | Not Null                                    |
 * |  5  | IMAGE_URL         | 活動圖片         | VARCHAR     | 300      | (IMG_URL)                                   |
 * |  6  | LOCATION          | 活動地點         | VARCHAR     | 100      | Not Null                                    |
 * |  7  | ACTIVITY_STATUS   | 活動狀態         | TINYINT     |          | Not Null; 0:發起中, 1:已開始, 2:已結束, 3:取消 |
 * |  8  | CREATED_TIME      | 創建時間         | DATETIME    |          | Not Null                                    |
 * |  9  | UPDATED_TIME      | 更新時間         | DATETIME    |          | Not Null                                    |
 * | 10  | REG_START_TIME    | 報名開始         | DATETIME    |          | Not Null                                    |
 * | 11  | REG_END_TIME      | 報名截止         | DATETIME    |          | Not Null                                    |
 * | 12  | ACTIV_START_TIME  | 活動開始時間     | DATETIME    |          | Not Null                                    |
 * | 13  | ACTIV_END_TIME    | 活動結束時間     | DATETIME    |          | Not Null                                    |
 * | 14  | GENDER_FILTER     | 篩選性別         | TINYINT     |          | Not Null; 0:不限, 1:男, 2:女                |
 * | 15  | MAX_AGE           | 年齡上限         | INT         |          | Not Null                                    |
 * | 16  | MIN_AGE           | 年齡下限         | INT         |          | Not Null                                    |
 * | 17  | EXPIRED_TIME      | 申請過期時間     | DATETIME    |          | Not Null                                    |
 * | 18  | MAX_PEOPLE        | 人數上限         | INT         |          | Not Null                                    |
 * | 19  | MIN_PEOPLE        | 人數下限         | INT         |          | Not Null                                    |
 * | 20  | SIGNUP_COUNT      | 已報名人數       | INT         |          | Not Null                                    |
 * | 21  | RATING_COUNT      | 評價人數         | INT         |          | Default 0                                   |
 * | 22  | RATING            | 評價分數         | INT         |          | Default 0                                   |
 * | 23  | COMMENT_COUNT     | 留言數           | INT         |          | Default 0                                   |
 * | 24  | REPORT_COUNT      | 檢舉次數         | INT         |          | Default 0                                   |
 * +-----+-------------------+------------------+-------------+----------+---------------------------------------------+
 */


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ACTIVITY")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACTIVITY_ID")
    private Integer activityId;

    // 多對一：多個活動對應一個會員
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    @Column(name = "TITLE", length = 50, nullable = false)
    private String title;

    @Column(name = "CONTENT", length = 1000, nullable = false)
    private String content;

    @Column(name = "IMAGE_URL", length = 300)
    private String imageUrl;

    @Column(name = "LOCATION", length = 100, nullable = false)
    private String location;

    @Column(name = "ACTIVITY_STATUS", nullable = false)
    private Byte activityStatus;

    @Column(name = "CREATED_TIME", nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Timestamp createdTime;

    @Column(name = "UPDATED_TIME", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Timestamp updatedTime;

    @Column(name = "REG_START_TIME", nullable = false)
    private Timestamp regStartTime;

    @Column(name = "REG_END_TIME", nullable = false)
    private Timestamp regEndTime;

    @Column(name = "ACTIV_START_TIME", nullable = false)
    private Timestamp activStartTime;

    @Column(name = "ACTIV_END_TIME", nullable = false)
    private Timestamp activEndTime;

    @Column(name = "GENDER_FILTER", nullable = false)
    private Byte genderFilter;

    @Column(name = "MAX_AGE", nullable = false)
    private Integer maxAge;

    @Column(name = "MIN_AGE", nullable = false)
    private Integer minAge;

    @Column(name = "EXPIRED_TIME", nullable = false)
    private Timestamp expiredTime;

    @Column(name = "MAX_PEOPLE", nullable = false)
    private Integer maxPeople;

    @Column(name = "MIN_PEOPLE", nullable = false)
    private Integer minPeople;

    @Column(name = "SIGNUP_COUNT", nullable = false)
    private Integer signupCount;

    @Column(name = "RATING_COUNT", nullable = false)
    private Integer ratingCount;

    @Column(name = "RATING", nullable = false)
    private Integer rating;

    @Column(name = "COMMENT_COUNT", nullable = false)
    private Integer commentCount;

    @Column(name = "REPORT_COUNT", nullable = false)
    private Integer reportCount;

    // One To Many --

    // 一個活動對應多個活動參與
    @JsonIgnore
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActivityParticipant> activityParticipants;

    // 一個活動對應多個活動問題
    @JsonIgnore
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActivityQuestion> activityQuestions;

    // 一個活動對應多個活動留言
    @JsonIgnore
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActivityComment> activityComments;
}

