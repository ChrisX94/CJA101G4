package com.shakemate.activity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * 表格名稱：活動留言 (ACTIVITY_COMMENTS)
 *
 * 主鍵：
 *   - COMMENT_ID
 *
 * 外來鍵：
 *   - ACTIVITY_ID ➔ ACTIVITY(ACTIVITY_ID)
 *   - USER_ID ➔ USERS(USER_ID)
 *   - PARENT_COMMENT_ID ➔ ACTIVITY_COMMENTS(COMMENT_ID)
 *
 * 欄位說明：
 * ------------------------------------------------------------------------------
 * | 欄位名稱          | 欄位敘述       | 資料型態 | 長度 | 備註                          |
 * ------------------------------------------------------------------------------
 * | COMMENT_ID        | 留言編號       | INT      |      | Not Null, 主鍵, 自動遞增(AI)  |
 * | ACTIVITY_ID       | 活動編號       | INT      |      | Not Null, 外來鍵              |
 * | USER_ID           | 會員編號       | INT      |      | Not Null, 外來鍵              |
 * | CONTENT           | 留言內容       | VARCHAR  | 500  | Not Null                      |
 * | PARENT_COMMENT_ID | 父留言編號     | INT      |      | 外來鍵，可為 null             |
 * | COMMENT_TIME      | 留言時間       | DATETIME |      |                                |
 * | COMMENT_COUNT     | 留言數         | INT      |      | 預設值為 0                    |
 * ------------------------------------------------------------------------------
 */

@Entity
@Table(name = "ACTIVITY_COMMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動遞增
    @Column(name = "COMMENT_ID", nullable = false)
    private Integer commentId;

//    @Column(name = "ACTIVITY_ID", nullable = false)
//    private Integer activityId;

    // 關聯到 Activity 實體
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    private Activity activity;

//    @Column(name = "USER_ID", nullable = false)
//    private Integer userId;

    // 關聯到 User 實體
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    @Column(name = "CONTENT", nullable = false, length = 500)
    private String content;

    // 父留言，多對一，nullable = true 表示可無父留言（null）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID")
    private ActivityComment parentComment;

//    @Column(name = "PARENT_COMMENT_ID", nullable = true)
//    private Integer parentCommentId;

    @Column(name = "COMMENT_TIME")
    private Timestamp commentTime;

    @Column(name = "COMMENT_COUNT", nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    // 一個留言有多個子留言
    @JsonIgnore
    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ActivityComment> activityComments;

}
