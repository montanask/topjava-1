package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface MealService {
    Meal create(Meal meal, int userId);

    void delete(int userMealId, int userId);

    Meal get(int userMealId, int userId) throws NotFoundException;

    Meal update(Meal meal, int userId);

    Collection<Meal> getAll(int userId);

    List<Meal> getDateFiltered(int userId, LocalDate startDate, LocalDate endDate);
}