package hse.sachkov.learningtrackbackend.api.material;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import hse.sachkov.learningtrackbackend.api.material.article.Article;
import hse.sachkov.learningtrackbackend.api.material.course.Course;
import hse.sachkov.learningtrackbackend.api.material.vacancy.Vacancy;
import hse.sachkov.learningtrackbackend.api.user.User;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        defaultImpl = Material.class,
        include = JsonTypeInfo.As.PROPERTY,
        property = "learningMaterialType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Course.class, name = "course"),
        @JsonSubTypes.Type(value = Vacancy.class, name = "vacancy"),
        @JsonSubTypes.Type(value = Article.class, name = "article")
})
public class Material {

    @Id
    Long id;

    @Transient
    @EqualsAndHashCode.Exclude
    Boolean liked;

    @Transient
    String title;

    @Transient
    String description;

    @Transient
    @EqualsAndHashCode.Exclude
    Boolean completed;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_material_like",
            joinColumns = @JoinColumn(
                    name = "material_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "username",
                    referencedColumnName = "username"
            )
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> likedUsers = new HashSet<>();

    public Material(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Material material = (Material) o;
        return getId() != null && Objects.equals(getId(), material.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
