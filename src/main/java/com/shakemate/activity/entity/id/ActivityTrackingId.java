package com.shakemate.activity.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ActivityTrackingId implements Serializable {

    @Column(name = "ACTIVITY_ID")
    private Integer activityId;

    @Column(name = "USER_ID")
    private Integer userId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ActivityTrackingId that = (ActivityTrackingId) o;
        return Objects.equals(activityId, that.activityId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, userId);
    }
}
