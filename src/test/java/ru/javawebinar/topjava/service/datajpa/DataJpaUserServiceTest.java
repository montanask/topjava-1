package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static ru.javawebinar.topjava.MealTestData.MEALS;
import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.assertMatch;

@ActiveProfiles(DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {
    @Test
    public void getWithMeals() {
        User withMeals = service.getWithMeals(USER_ID);
        assertMatch(withMeals, USER);
        assertMatch(withMeals.getMeals(), MEALS);
    }

    @Test
    public void getWithMealsNotFound() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found entity with id=" + 1);
        service.getWithMeals(1);
    }
}
