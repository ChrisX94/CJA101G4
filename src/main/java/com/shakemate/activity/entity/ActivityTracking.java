package com.shakemate.activity.entity;

import com.shakemate.activity.entity.id.ActivityTrackingId;
import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
/**
 * 表格名稱：活動追蹤 (ACTIVITY_TRACKING)
 *
 * 主鍵：
 *   - ACTIVITY_ID + USER_ID
 *
 * 外來鍵：
 *   - ACTIVITY_ID ➔ ACTIVITY(ACTIVITY_ID)
 *   - USER_ID ➔ USERS(USER_ID)
 *
 * 欄位說明：
 * ------------------------------------------------------------------------------
 * | 欄位名稱       | 欄位敘述   | 資料型態 | 長度 | 備註                          |
 * ------------------------------------------------------------------------------
 * | ACTIVITY_ID    | 活動編號   | INT      |      | Not Null, 主鍵, 外來鍵        |
 * | USER_ID        | 會員編號   | INT      |      | Not Null, 主鍵, 外來鍵        |
 * | TRACKING_TIME  | 追蹤時間   | DATETIME |      | Not Null                       |
 * | TRACKING_STATE | 追蹤狀態   | TINYINT  |      | Not Null                       |
 * |                |            |          |      | 0: 正在追蹤                    |
 * |                |            |          |      | 1: 取消追蹤                    |
 * ------------------------------------------------------------------------------
 */

@Entity
@Table(name = "activity_tracking")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityTracking {

    @EmbeddedId
    private ActivityTrackingId id;

//    private Integer activityId;
//    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("activityId")
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    @Column(name = "TRACKING_TIME", nullable = false)
    private Timestamp trackingTime;

    @Column(name = "TRACKING_STATE", nullable = false)
    private Byte trackingState; // 0: 正在追蹤, 1: 取消追蹤


}
