package ru.javawebinar.topjava.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NamedQueries({@NamedQuery(name = Meal.GET, query = "SELECT m FROM Meal m WHERE m.id=:id AND m.user.id =:userId"),
        @NamedQuery(name = Meal.DELETE, query = "DELETE FROM Meal m WHERE m.id=:id AND m.user.id=:user_id"),
        @NamedQuery(name = Meal.UPDATE, query = "UPDATE Meal m SET m=:meal WHERE m.id=:id AND m.user.id=:user_id"),
        @NamedQuery(name = Meal.GET_ALL_SORTED, query = "SELECT m FROM Meal m WHERE m.user.id=:user_id ORDER BY m.dateTime DESC")
})
@NamedNativeQueries(@NamedNativeQuery(name = Meal.GET_ALL_FILTERED, query = "SELECT * FROM meals WHERE user_id=:user_id AND date_time BETWEEN :startDT AND :endDT ORDER BY date_time DESC"))
@Entity
@Table(name = "meals", uniqueConstraints = @UniqueConstraint(name = "meals_unique_user_datetime_idx", columnNames = {"user_id", "date_time"}))
public class Meal extends AbstractBaseEntity {
    public static final String GET = "Meal.get";
    public static final String UPDATE = "Meal.update";
    public static final String DELETE = "Meal.delete";
    public static final String GET_ALL_SORTED = "Meal.getAllSorted";
    public static final String GET_ALL_FILTERED = "Meal.getAllFiltered";

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "description", nullable = false)
    @NotBlank
    @Min(2)
    private String description;

    @Column(name = "calories", nullable = false)
    @Range(min = 1, max = 7000)
    private int calories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
