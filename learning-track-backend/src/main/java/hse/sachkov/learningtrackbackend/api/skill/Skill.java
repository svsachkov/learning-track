package hse.sachkov.learningtrackbackend.api.skill;

import hse.sachkov.learningtrackbackend.api.user.User;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username", referencedColumnName = "username")
    User user;

    String skill;
    Double level;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Skill skill = (Skill) o;
        return getId() != null && Objects.equals(getId(), skill.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
