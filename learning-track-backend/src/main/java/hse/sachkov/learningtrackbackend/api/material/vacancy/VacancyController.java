package hse.sachkov.learningtrackbackend.api.material.vacancy;

import hse.sachkov.learningtrackbackend.api.user.User;
import hse.sachkov.learningtrackbackend.api.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;
    private final UserService userService;

    @GetMapping()
    public Page<Vacancy> getVacancies(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int page_size) {
        log.info("Start processing GET /vacancies request . . .");

        Page<Vacancy> vacanciesPage = vacancyService.getVacancies(PageRequest.of(page, page_size));

        log.info("GET /vacancies request processed!");

        return vacanciesPage;
    }

    @GetMapping("/all")
    public List<Vacancy> getAllVacancies() {
        log.info("Start processing GET /vacancies/all request . . .");

        List<Vacancy> vacancies = vacancyService.getAllVacancies();

        log.info("GET /vacancies/all request processed!");

        return vacancies;
    }

    @GetMapping("/{id}")
    public Vacancy getVacancyById(Principal userRequester, @PathVariable Long id) {
        log.info("Start processing GET /vacancies/{id} request . . .");

        Vacancy vacancy = vacancyService.getVacancyById(id);

        if (userRequester != null) {
            User user = userService.getUserByUsername(userRequester.getName());
            vacancy.setLiked(vacancyService.isVacancyLikedByUser(vacancy, user));
            vacancy.setCompleted(vacancyService.isVacancyCompletedByUser(vacancy, user));
        }

        log.info("GET /vacancies/{id} request processed!");

        return vacancy;
    }
}
