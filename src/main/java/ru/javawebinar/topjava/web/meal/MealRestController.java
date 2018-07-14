package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealWithExceed;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private static final Logger log = getLogger(MealRestController.class);

    private MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        log.info("create {} for user with id={}", meal, authUserId());
        checkNew(meal);
        return service.create(meal, authUserId());
    }

    public void delete(int userMealId) {
        log.info("delete meal with id={} for user with id={}", userMealId, authUserId());
        service.delete(userMealId, authUserId());
    }

    public Meal get(int userMealId) {
        log.info("get meal with id={} for user with id={}", userMealId, authUserId());
        return service.get(userMealId, authUserId());
    }

    public Meal update(Meal meal, int mealId) {
        log.info("update {} for user with id={}", meal, authUserId());
        checkNotFound(meal, "meal reference is null");

        assureIdConsistent(meal, mealId);
        return service.update(meal, authUserId());
    }

    public List<MealWithExceed> getAll() {
        log.info("getAll for user with id={}", authUserId());
        return MealsUtil.getWithExceeded(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealWithExceed> getFilteredByDateTimeWithExceeded(String startDate, String endDate, String startTime, String endTime) {
        log.info("getAll filtered for user with id={}", authUserId());

        LocalDate startD = startDate == null || startDate.isEmpty() ? LocalDate.MIN : LocalDate.parse(startDate);
        LocalDate endD = endDate == null || endDate.isEmpty() ? LocalDate.MAX : LocalDate.parse(endDate);
        LocalTime startT = startTime == null || startTime.isEmpty() ? LocalTime.MIN : LocalTime.parse(startTime);
        LocalTime endT = endTime == null || endTime.isEmpty() ? LocalTime.MAX : LocalTime.parse(endTime);

        List<Meal> dateFiltered = service.getDateFiltered(authUserId(), startD, endD);

        return MealsUtil.getFilteredWithExceeded(dateFiltered, authUserCaloriesPerDay(), startT, endT);
    }
}