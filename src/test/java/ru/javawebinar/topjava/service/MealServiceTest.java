package ru.javawebinar.topjava.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;


import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    MealService service;

    @Test
    public void get() {
        Meal actual = service.get(MEAL1.getId(), USER_ID);
        assertThat(actual).isEqualToComparingFieldByField(MEAL1);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() {
        service.get(MEAL1.getId(), ADMIN_ID);
    }

    @Test
    public void delete() {
        service.delete(MEAL2.getId(), USER_ID);
        assertMatch(service.getAll(USER_ID), MEAL6, MEAL5, MEAL4, MEAL3, MEAL1);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() {
        service.delete(MEAL2.getId(), ADMIN_ID);
    }

    @Test
    public void getBetweenDateTimes() {
        List<Meal> actual = service.getBetweenDateTimes(
                LocalDateTime.of(2015, Month.MAY, 30, 20, 0),
                LocalDateTime.of(2015, Month.MAY, 31, 13, 0), USER_ID);
        assertMatch(actual, MEAL5, MEAL4, MEAL3);
    }

    @Test
    public void getAll() {
        List<Meal> actual = service.getAll(USER_ID);
        assertMatch(actual, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void update() {
        Meal meal = new Meal(ADMIN_MEAL1);
        meal.setDescription("Updated meal");
        meal.setCalories(666);
        service.update(meal, ADMIN_ID);
        assertMatch(service.getAll(ADMIN_ID), ADMIN_MEAL2, meal);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFound() {
        service.update(MEAL1, ADMIN_ID);
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(LocalDateTime.now(), "newMeal", 1111);
        Meal actual = service.create(newMeal, ADMIN_ID);
        newMeal.setId(actual.getId());
        assertThat(actual).isEqualToComparingFieldByField(newMeal);
    }

    @Test(expected = DataAccessException.class)
    public void createWithSameDateTime() {
        Meal newMeal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "newMeal", 1111);
        Meal newMealSameDT = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "newMealSameDT", 1112);
        service.create(newMeal, ADMIN_ID);
        service.create(newMealSameDT, ADMIN_ID);
    }
}

