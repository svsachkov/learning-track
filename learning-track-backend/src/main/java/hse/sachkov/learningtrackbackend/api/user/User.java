package hse.sachkov.learningtrackbackend.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import hse.sachkov.learningtrackbackend.api.material.Material;

import jakarta.persistence.*;

import lombok.*;

import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User implements Serializable {

    @Id
    String username;

    @Unique
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_sequence")
    @Column(nullable = false, name = "external_id")
    Long externalId;

    String fullName;
    Date lastSeen;
    Date signUp;
    Long birthdayYear;
    String city;
    String desiredPosition;
    String college;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_material_completed",
            joinColumns = @JoinColumn(
                    name = "user_external_id",
                    referencedColumnName = "external_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "material_id",
                    referencedColumnName = "id"
            )
    )
    private Set<Material> materialsCompleted = new HashSet<>();

    public User(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getUsername() != null && Objects.equals(getUsername(), user.getUsername());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
