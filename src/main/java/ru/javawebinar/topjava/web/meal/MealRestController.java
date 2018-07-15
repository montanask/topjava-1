package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealWithExceed;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);
    private final MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        int userId = authUserId();
        log.info("create {} for user with id={}", meal, userId);
        checkNew(meal);
        return service.create(meal, userId);
    }

    public void delete(int id) {
        int userId = authUserId();
        log.info("delete meal with id={} for user with id={}", id, userId);
        service.delete(id, userId);
    }

    public Meal get(int id) {
        int userId = authUserId();
        log.info("get meal with id={} for user with id={}", id, userId);
        return service.get(id, userId);
    }

    public Meal update(Meal meal, int mealId) {
        int userId = authUserId();
        log.info("update {} for user with id={}", meal, userId);
        checkNotFound(meal, "meal reference is null");

        assureIdConsistent(meal, mealId);
        return service.update(meal, userId);
    }

    public List<MealWithExceed> getAll() {
        int userId = authUserId();
        log.info("getAll for user with id={}", userId);
        return MealsUtil.getWithExceeded(service.getAll(userId), authUserCaloriesPerDay());
    }

    /**
     * <ol>Filter separately
     * <li>by date</li>
     * <li>by time for every date</li>
     * </ol>
     */
    public List<MealWithExceed> getBetweenWithExceeded(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        int userId = authUserId();
        log.info("get between dates({} - {}) and times ({} - {}) for user with id={}", startDate, endDate, startTime, endTime, userId);

        List<Meal> dateFiltered = service.getBetweenDates(
                startDate == null ? DateTimeUtil.MIN_DATE : startDate,
                endDate == null ? DateTimeUtil.MAX_DATE : endDate, userId);

        return MealsUtil.getFilteredWithExceeded(dateFiltered, authUserCaloriesPerDay(),
                startTime == null ? LocalTime.MIN : startTime,
                endTime == null ? LocalTime.MAX : endTime);
    }
}