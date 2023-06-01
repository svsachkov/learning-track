package hse.sachkov.learningtrackbackend.api.material.course;

import hse.sachkov.learningtrackbackend.api.material.Material;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.*;

import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course extends Material {

    @Column(length = 10000)
    String title;

    @Column(length = 1000000)
    String description;

    String externalLink;
    String imageUrl;
    String category;
    String price;
    String headline;
    String rating;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Course course = (Course) o;
        return getId() != null && Objects.equals(getId(), course.getId());
    }
}
