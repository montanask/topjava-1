package ru.javawebinar.topjava.web;

import org.junit.*;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.mock.InMemoryUserRepositoryImpl;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.user.AdminRestController;

import java.util.*;

import static ru.javawebinar.topjava.UserTestData.*;

public class InMemoryAdminRestControllerTest {
    private static ConfigurableApplicationContext appCtx;
    private static AdminRestController controller;

    @BeforeClass
    public static void beforeClass() {
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml", "spring/spring-db.xml");
        System.out.println("\n" + Arrays.toString(appCtx.getBeanDefinitionNames()) + "\n");
        controller = appCtx.getBean(AdminRestController.class);
    }

    @AfterClass
    public static void afterClass() {
        appCtx.close();
    }

    @Before
    public void setUp() throws Exception {
        // re-initialize
        InMemoryUserRepositoryImpl repository = appCtx.getBean(InMemoryUserRepositoryImpl.class);
        repository.init();
    }

    @Test
    public void update() {
        User updated = new User(ADMIN);
        updated.setName("Boss");
        updated.setCaloriesPerDay(10);
        controller.update(updated, ADMIN_ID);
        assertMatch(updated, controller.get(ADMIN_ID));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateWithWrongId() {
        User updated = new User(ADMIN);
        updated.setName("Boss");
        controller.update(ADMIN, USER_ID);
    }

    @Test
    public void testCreate() {
        User newUser = new User(null, "New", "new@gmail.com", "newPass", 1555,
                false, new Date(), Collections.singleton(Role.ROLE_USER));
        User created = controller.create(newUser);
        newUser.setId(created.getId());
        assertMatch(controller.getAll(), ADMIN, newUser, USER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateExistingMeal() {
        User existingUser = new User(10, "New", "new@gmail.com", "newPass", 1555,
                false, new Date(), Collections.singleton(Role.ROLE_USER));
        controller.create(existingUser);
    }

    @Test
    @Ignore
    public void duplicateMailCreate() {
        User duplicatedMealUser = new User(null, "New", "admin@gmail.com", "newPass", 1555,
                false, new Date(), Collections.singleton(Role.ROLE_USER));
        //pass, cause no implementation for this functionality
        controller.create(duplicatedMealUser);
    }

    @Test
    public void testGetAll() {
        List<User> allActual = controller.getAll();
        assertMatch(allActual, ADMIN, USER);
    }

    @Test
    public void testGet() {
        User actual = controller.get(ADMIN_ID);
        assertMatch(actual, ADMIN);
    }

    @Test(expected = NotFoundException.class)
    public void testGetNotFound() {
        controller.get(10);
    }

    @Test
    public void testGetByMail() {
        User actual = controller.getByMail(USER.getEmail());
        assertMatch(actual, USER);
    }

    @Test(expected = NotFoundException.class)
    public void testGetByMailNotFound() {
        controller.getByMail("notExist@mail.com");
    }

    @Test
    public void testDelete() throws Exception {
        controller.delete(UserTestData.USER_ID);
        Collection<User> users = controller.getAll();
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.iterator().next(), ADMIN);
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteNotFound() throws Exception {
        controller.delete(10);
    }
}