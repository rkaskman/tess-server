package com.ttu.roman.dao;

import com.ttu.roman.model.Expense;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@Repository
public class ExpenseDAO extends AbstractDao<Expense> {
    public ExpenseDAO() {
        super(Expense.class);
    }

    public List<Expense> getUserExpensesForPeriod(String userId, Date startDate, Date endDate) {
        return em.createQuery("from Expense e where e.state = :state and e.userId = :userId " +
                "and e.insertedAt >= :startDate and e.insertedAt <= :endDate order by e.id desc")
                .setParameter("state", Expense.STATE_ACCEPTED)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }


}
