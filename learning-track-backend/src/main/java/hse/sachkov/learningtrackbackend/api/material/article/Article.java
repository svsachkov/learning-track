package hse.sachkov.learningtrackbackend.api.material.article;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hse.sachkov.learningtrackbackend.api.material.Material;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.*;

import org.hibernate.Hibernate;

import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "articles")
public class Article extends Material {

    @Column(length = 10000)
    String title;

    @Column(length = 10000)
    String description;

    @Transient
    @JsonIgnore
    String dateCsv;

    Date date;

    @Column(length = 100000)
    String content;

    String category;
    String tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Article article = (Article) o;
        return getId() != null && Objects.equals(getId(), article.getId());
    }
}
