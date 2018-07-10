package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealRepositoryImpl implements MealRepository {
    private final Map<Integer,Meal> meals = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500));
        save(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510));
    }

    @Override
    public Meal get(int id) {
        return meals.get(id);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()){
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(),meal);
            return meal;
        }
        //returns newMeal, if oldMeal is present, null - if not
        return meals.computeIfPresent(meal.getId(),(key, oldMeal)->meal);
    }

    @Override
    public boolean delete(int id) {
        return meals.remove(id)!=null;
    }

    @Override
    public Collection<Meal> getAll() {
        return meals.values();
    }
}
