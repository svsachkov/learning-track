package hse.sachkov.learningtrackbackend.api.material.vacancy;

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
@Table(name = "vacancies")
public class Vacancy extends Material {

    @Column(length = 10000)
    String title;

    @Column(length = 10000)
    String description;

    String type;
    String field;
    String workLine;
    String workExperience;
    String employer;
    String occupancy;
    String city;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Vacancy vacancy = (Vacancy) o;
        return getId() != null && Objects.equals(getId(), vacancy.getId());
    }
}
