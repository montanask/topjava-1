package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(DATAJPA)
public class DataJpaMealServiceTest extends MealServiceTest {
    @Test
    public void getWithUser() {
        Meal withUser = service.getWithUser(MEAL1_ID, USER_ID);
        assertMatch(withUser, MEAL1);
        assertMatch(withUser.getUser(), USER);
    }

    @Test
    public void getWithUserNotFound() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found entity with id=" + MEAL1_ID);
        service.getWithUser(MEAL1_ID, ADMIN_ID);
    }
}
