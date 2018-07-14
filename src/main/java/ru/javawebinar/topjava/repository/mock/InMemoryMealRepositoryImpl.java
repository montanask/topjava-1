package ru.javawebinar.topjava.repository.mock;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private static final Logger log = getLogger(InMemoryMealRepositoryImpl.class);
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            repository.putIfAbsent(userId, new HashMap<>());
            meal.setId(counter.incrementAndGet());
            log.info("create {} for user with id={}", meal, userId);
            repository.get(userId).put(meal.getId(), meal);

            return meal;
        }
        Map<Integer, Meal> userMeals = repository.get(userId);
        log.info("update {} for user with id={}", meal, userId);
        // null: if update meal, but user absent in a storage, or if update other user's meal
        return userMeals != null ? userMeals.computeIfPresent(meal.getId(), (mealId, oldMeal) -> meal)
                : null;
    }

    @Override
    public void delete(int userMealId, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals != null) {
            log.info("delete {} for user with id={}", userMeals.remove(userMealId), userId);
        }
    }

    @Override
    public Meal get(int userMealId, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        Meal meal = null;
        if (userMeals != null) {
            meal = userMeals.get(userMealId);
            log.info("get {} for user with id={}", meal, userId);
        }
        return meal;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("getAll for user with id={}", userId);
        return repository.get(userId).values();
    }

    @Override
    public List<Meal> getDateFiltered(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getDateFiltered for user with id={}", userId);
        return getFiltered(userId, meal -> DateTimeUtil.isBetween(meal.getDate(), startDate, endDate));
    }

    private List<Meal> getFiltered(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null ? userMeals.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime, Comparator.reverseOrder()))
                .collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }
}

