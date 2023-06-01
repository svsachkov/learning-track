package hse.sachkov.learningtrackbackend.api.track;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hse.sachkov.learningtrackbackend.api.material.Material;

import hse.sachkov.learningtrackbackend.api.user.User;
import jakarta.persistence.*;

import lombok.*;

import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "track_steps")
public class TrackStep {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_step_id_sequence")
    Long trackStepId;

    Long stepOrderNumber;
    Boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false, updatable = false)
    Material material;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false, updatable = false)
    Track track;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TrackStep trackStep = (TrackStep) o;
        return getTrackStepId() != null && Objects.equals(getTrackStepId(), trackStep.getTrackStepId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
