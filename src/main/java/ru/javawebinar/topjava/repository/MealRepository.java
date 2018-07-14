package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface MealRepository {
    Meal save(Meal meal, int userId);

    void delete(int userMealId, int userId);

    // null if not found
    Meal get(int userMealId, int userId);

    Collection<Meal> getAll(int userId);

    List<Meal> getDateFiltered(int userId, LocalDate startDate, LocalDate endDate);
}
