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
        assertMatch(service.get(MEAL1_ID, USER_ID), MEAL1);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() {
        service.get(MEAL1_ID, ADMIN_ID);
    }

    @Test
    public void delete() {
        service.delete(MEAL1_ID, USER_ID);
        assertMatch(service.getAll(USER_ID), MEAL6, MEAL5, MEAL4, MEAL3, MEAL2);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() {
        service.delete(MEAL1_ID, ADMIN_ID);
    }

    @Test
    public void getBetweenDateTimes() {
        assertMatch(service.getBetweenDateTimes(
                LocalDateTime.of(2015, Month.MAY, 30, 20, 0),
                LocalDateTime.of(2015, Month.MAY, 31, 13, 0), USER_ID), MEAL5, MEAL4, MEAL3);
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.getAll(USER_ID), MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, updated);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFound() {
        service.update(MEAL1, ADMIN_ID);
    }

    @Test
    public void create() {
        Meal created = getCreated();
        service.create(created, USER_ID);
        assertMatch(service.getAll(USER_ID), created, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test(expected = DataAccessException.class)
    public void createWithSameDateTime() {
        Meal newMeal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "newMeal", 1111);
        Meal newMealSameDT = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "newMealSameDT", 1112);
        service.create(newMeal, ADMIN_ID);
        service.create(newMealSameDT, ADMIN_ID);
    }
}

