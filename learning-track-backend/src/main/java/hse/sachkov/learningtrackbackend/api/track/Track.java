package hse.sachkov.learningtrackbackend.api.track;

import hse.sachkov.learningtrackbackend.api.user.User;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.Hibernate;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_id_sequence")
    Long trackId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    User user;

    Date creationDate;
    String destination;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "track_id")
    @ToString.Exclude
    List<TrackStep> trackSteps;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Track track = (Track) o;
        return getTrackId() != null && Objects.equals(getTrackId(), track.getTrackId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
