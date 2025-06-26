package com.shakemate.activity.entity;

import com.shakemate.activity.entity.id.ActivityParticipantId;
import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

/**
 * 表格名稱：活動參與 (ACTIVITY_PARTICIPANT)
 *
 * 主鍵：
 *   - PARTICIPANT_ID + ACTIVITY_ID
 *
 * 外來鍵：
 *   - PARTICIPANT_ID ➔ USERS(USER_ID)
 *   - ACTIVITY_ID ➔ ACTIVITY(ACTIVITY_ID)
 *
 * 欄位說明：
 * ------------------------------------------------------------------------------
 * | 欄位名稱       | 欄位敘述       | 資料型態 | 長度 | 備註                          |
 * ------------------------------------------------------------------------------
 * | PARTICIPANT_ID | 參與者         | INT     |      | Not Null, 主鍵, 外來鍵        |
 * | ACTIVITY_ID    | 活動編號       | INT     |      | Not Null, 主鍵, 外來鍵        |
 * | ADM_REVIEW_TIME| 審核時間       | DATETIME|      |                                |
 * | PAR_STATUS     | 參與者狀態     | TINYINT |      | Not Null                       |
 * |                |                |         |      | 0: 申請中（預設）              |
 * |                |                |         |      | 1: 已取消申請                  |
 * |                |                |         |      | 2: 已加入                      |
 * |                |                |         |      | 3: 已退出                      |
 * | APPLYING_DATE  | 申請時間       | DATETIME|      | Not Null                       |
 * | RATING         | 評分星等       | TINYINT |      | 1~5 星，預設為 5 星             |
 * | REVIEW_CONTENT | 評語           | VARCHAR | 500  |                                |
 * | REVIEW_TIME    | 評價時間       | DATETIME|      |                                |
 * ------------------------------------------------------------------------------
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "activity_participant")
public class ActivityParticipant {

    @EmbeddedId
    private ActivityParticipantId id;

    // 關聯對應
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("participantId") // 對應複合主鍵中的欄位
    @JoinColumn(name = "PARTICIPANT_ID", nullable = false, insertable = false, updatable = false)
    private Users participant;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("activityId")
    @JoinColumn(name = "ACTIVITY_ID", nullable = false, insertable = false, updatable = false)
    private Activity activity;


    @Column(name = "ADM_REVIEW_TIME")
    private Timestamp admReviewTime;

    @Column(name = "PAR_STATUS", nullable = false)
    private Byte parStatus = 0; // 預設 0 申請中

    @Column(name = "APPLYING_DATE", nullable = false)
    private Timestamp applyingDate;

    @Column(name = "RATING")
    private Byte rating;

    @Column(name = "REVIEW_CONTENT", length = 500)
    private String reviewContent;

    @Column(name = "REVIEW_TIME")
    private Timestamp reviewTime;


}
