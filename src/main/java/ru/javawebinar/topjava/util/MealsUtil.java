package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> mealList = Arrays.asList(
                new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        //getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        getFilteredWithExceededByCycle(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
        System.out.println();
        getFilteredWithExceededByStream(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
        System.out.println();
        getFilteredWithExceededInOnePass(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
        System.out.println();
        getFilteredWithExceededInOnePass2(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<MealWithExceed> getFilteredWithExceeded(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return null;
    }

    public static List<MealWithExceed> getFilteredWithExceededInOnePass2(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        final class Aggregate {
            private final List<Meal> daylyMeals = new ArrayList<>();
            private int daylySumOfCalories;

            private void accumulate(Meal meal) {
                daylySumOfCalories += meal.getCalories();
                if (TimeUtil.isBetween(meal.getTime(), startTime, endTime))
                    daylyMeals.add(meal);
            }

            //never invoked if upstream is sequential
            private Aggregate combine(Aggregate that) {
                this.daylySumOfCalories += that.daylySumOfCalories;
                this.daylyMeals.addAll(that.daylyMeals);
                return this;
            }

            private Stream<MealWithExceed> finisher() {
                final boolean exceeded = daylySumOfCalories > caloriesPerDay;
                return daylyMeals.stream().map(meal -> new MealWithExceed(meal.getDateTime(), meal.getDescription(), meal.getCalories(), exceeded));
            }
        }
        Collection<Stream<MealWithExceed>> values = meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate, Collector.of(Aggregate::new, Aggregate::accumulate, Aggregate::combine, Aggregate::finisher)))
                .values();

        return values.stream().flatMap(identity()).collect(Collectors.toList());
    }

    public static List<MealWithExceed> getFilteredWithExceededInOnePass(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(Collectors.groupingBy(Meal::getDate)).values()
                .stream().flatMap(daylyMeals -> {
                    boolean exceeded = daylyMeals.stream().mapToInt(Meal::getCalories).sum() > caloriesPerDay;
                    return daylyMeals.stream().filter(um -> TimeUtil.isBetween(um.getTime(), startTime, endTime))
                            .map(um -> new MealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(), exceeded));
                }).collect(Collectors.toList());
    }

    public static List<MealWithExceed> getFilteredWithExceededByStream(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream().collect(Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories)));
        return meals.stream()
                .filter(um -> TimeUtil.isBetween(um.getTime(), startTime, endTime))
                .map(um -> new MealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(), caloriesSumByDate.get(um.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<MealWithExceed> getFilteredWithExceededByCycle(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumPerDay = new HashMap<>();
        meals.forEach(um -> caloriesSumPerDay.merge(um.getDate(), um.getCalories(), (oldVal, newVal) -> oldVal + newVal));

        List<MealWithExceed> mealExceeded = new ArrayList<>();
        meals.forEach(um -> {
            if (TimeUtil.isBetween(um.getTime(), startTime, endTime))
                mealExceeded.add(new MealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(), caloriesSumPerDay.get(um.getDate()) > caloriesPerDay));
        });
        return mealExceeded;
    }
}
