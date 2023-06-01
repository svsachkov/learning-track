package hse.sachkov.learningtrackbackend.api.material.vacancy;

import com.google.common.collect.Lists;

import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    public Page<Vacancy> getVacancies(Pageable pageable) {
        return vacancyRepository.findAll(pageable);
    }

    public List<Vacancy> getAllVacancies() {
        return Lists.newArrayList(vacancyRepository.findAll());
    }

    public Vacancy getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id).orElse(null);

        if (vacancy == null) {
            throw new EntityNotFoundException("No vacancy found with provided id!");
        }

        return vacancy;
    }

    public boolean isVacancyLikedByUser(Vacancy vacancy, User user) {
        return vacancy.getLikedUsers().contains(user);
    }

    public boolean isVacancyCompletedByUser(Vacancy vacancy, User user) {
        return user.getMaterialsCompleted().contains(vacancy);
    }
}
