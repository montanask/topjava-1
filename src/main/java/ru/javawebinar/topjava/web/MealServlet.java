package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.InMemoryMealRepositoryImpl;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private final MealRepository repository;

    public MealServlet() {
        repository = new InMemoryMealRepositoryImpl();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal mealFromForm = collectEntity(request);
        log.debug("{} meal, {}", request.getParameter("action"), mealFromForm);

        repository.save(mealFromForm);

        response.sendRedirect("meals");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String id = null;
        String path = null;
        switch (action == null ? "list" : action) {
            case "list":
                log.debug("getAll");
                request.setAttribute("meals", MealsUtil.getWithExceeded(repository.getAll(), MealsUtil.DEFAULT_CALORIES_PER_DAY));
                path = "/meals.jsp";
                break;
            case "update":
                id = getId(request);
            case "create":
                request.setAttribute("meal", "update".equals(action) ? repository.get(Integer.parseInt(id)) : createDefaultEntity());
                path = "/mealForm.jsp";
                break;
            case "delete":
                id = getId(request);
                log.debug("delete meal with id={}", id);
                repository.delete(Integer.parseInt(id));
                //two lines below, for "hiding" deleted meal's id
                response.sendRedirect("meals");
                return;
            default:
                log.debug("not supported action");
        }
        request.getRequestDispatcher(path).forward(request, response);
    }

    private String getId(HttpServletRequest request) {
        return Objects.requireNonNull(request.getParameter("id"), "The request have null meal's id parameter");
    }

    private Meal collectEntity(HttpServletRequest request) {
        String dateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String id = getId(request);
        return new Meal(id.isEmpty() ? null : Integer.parseInt(id), LocalDateTime.parse(dateTime), description, calories);
    }

    private Meal createDefaultEntity() {
        return new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", MealsUtil.DEFAULT_MEAL_CALORIES);
    }
}
