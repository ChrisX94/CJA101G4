package com.shakemate.activity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 表格名稱：活動問題 (ACTIVITY_QUESTIONS)
 *
 * 主鍵：
 *   - QUESTION_ID
 *
 * 外來鍵：
 *   - ACTIVITY_ID ➔ ACTIVITY(ACTIVITY_ID)
 *
 * 欄位說明：
 * ------------------------------------------------------------------------------
 * | 欄位名稱       | 欄位敘述   | 資料型態 | 長度 | 備註                         |
 * ------------------------------------------------------------------------------
 * | QUESTION_ID    | 問題編號   | INT      |      | Not Null, 主鍵, 自動遞增(AI) |
 * | ACTIVITY_ID    | 活動編號   | INT      |      | Not Null, 外來鍵             |
 * | QUESTION_TEXT  | 問題內文   | VARCHAR  | 255  | Not Null                     |
 * | QUESTION_ORDER | 顯示順序   | TINYINT  |      | 1~5，用於排序                |
 * ------------------------------------------------------------------------------
 */


@Entity
@Table(name = "activity_questions")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Integer questionId;

//    @NotNull
//    @Column(name = "ACTIVITY_ID", nullable = false)
//    private Integer activityId;

    @ManyToOne
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    private Activity activity;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "QUESTION_TEXT", nullable = false, length = 255)
    private String questionText;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "QUESTION_ORDER", nullable = false)
    private Byte questionOrder; // 顯示順序 (1~5)
}