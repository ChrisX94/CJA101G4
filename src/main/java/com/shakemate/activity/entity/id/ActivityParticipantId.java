package com.shakemate.activity.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ActivityParticipantId {

    @Column(name = "PARTICIPANT_ID")
    private Integer participantId;

    @Column(name = "ACTIVITY_ID")
    private Integer activityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityParticipantId)) return false;
        ActivityParticipantId that = (ActivityParticipantId) o;
        return Objects.equals(participantId, that.participantId) &&
                Objects.equals(activityId, that.activityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantId, activityId);
    }
}
