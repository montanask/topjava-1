package ru.javawebinar.topjava.repository.jpa;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepositoryImpl implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        if (meal.isNew() && meal.getUser().getId() == userId) {
            em.persist(meal);
            return meal;
        }
        List<Meal> resultList = em.createQuery(Meal.UPDATE, Meal.class)
                .setParameter("meal", meal)
                .setParameter("id", meal.getId())
                .setParameter("user_id", userId)
                .getResultList();
        return DataAccessUtils.singleResult(resultList);
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createQuery(Meal.DELETE)
                .setParameter("id", id)
                .setParameter("user_id", userId)
                .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> resultList = em.createQuery(Meal.GET, Meal.class)
                .setParameter("id", id)
                .setParameter("userId", userId)
                .getResultList();
        return DataAccessUtils.singleResult(resultList);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createQuery(Meal.GET_ALL_SORTED, Meal.class)
                .setParameter("user_id", userId)
                .getResultList();
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return em.createQuery(Meal.GET_ALL_FILTERED, Meal.class)
                .setParameter("user_id",userId)
                .setParameter("startDT",startDate)
                .setParameter("endDT",endDate)
                .getResultList();
    }
}