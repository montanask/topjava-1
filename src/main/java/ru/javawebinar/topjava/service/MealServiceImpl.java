package ru.javawebinar.topjava.service;


import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealServiceImpl implements MealService {
    private final MealRepository repository;


    public MealServiceImpl(MealRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meal create(Meal meal, int userId) {
        return repository.save(meal, userId);
    }

    @Override
    public void delete(int userMealId, int userId) {
        repository.delete(userMealId, userId);
    }

    @Override
    public Meal get(int userMealId, int userId) throws NotFoundException {
        return checkNotFoundWithId(repository.get(userMealId, userId), userMealId);
    }

    @Override
    public Meal update(Meal meal, int userId) throws NotFoundException {
        return checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    @Override
    public List<Meal> getDateFiltered(int userId, LocalDate startDate, LocalDate endDate) {
        return repository.getDateFiltered(userId, startDate, endDate);
    }
}