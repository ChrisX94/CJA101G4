package com.shakemate.activity.entity;

import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import lombok.*;

/**
 * 表格名稱：活動回答 (ACTIVITY_ANSWERS)
 *
 * 主鍵：
 *   - ANSWER_ID
 *
 * 外來鍵：
 *   - ACTIVITY_ID ➔ ACTIVITY(ACTIVITY_ID)
 *   - QUESTION_ID ➔ ACTIVITY_QUESTIONS(QUESTION_ID)
 *   - USER_ID ➔ USERS(USER_ID)
 *
 * 欄位說明：
 * ------------------------------------------------------------------------------
 * | 欄位名稱     | 欄位敘述   | 資料型態 | 長度 | 備註                          |
 * ------------------------------------------------------------------------------
 * | ANSWER_ID    | 回答編號   | INT      |      | Not Null, 主鍵, 自動遞增(AI) |
 * | ACTIVITY_ID  | 活動編號   | INT      |      | Not Null, 外來鍵              |
 * | QUESTION_ID  | 問題編號   | INT      |      | Not Null, 外來鍵              |
 * | USER_ID      | 回答者     | INT      |      | Not Null, 外來鍵              |
 * | ANSWER_TEXT  | 回答內容   | VARCHAR  | 500  | Not Null                      |
 * ------------------------------------------------------------------------------
 */

@Entity
@Table(name = "ACTIVITY_ANSWERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANSWER_ID", nullable = false)
    private Integer answerId;


//    private Integer activityId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    private Activity activity;

//    private Integer questionId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID", nullable = false)
    private ActivityQuestion activityQuestion;

//    private Integer userId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    @Column(name = "ANSWER_TEXT", nullable = false, length = 500)
    private String answerText;

}
