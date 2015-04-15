package com.ttu.roman.dao;

import com.ttu.roman.auth.User;
import com.ttu.roman.controller.request.ExpenseRequest;
import com.ttu.roman.model.Expense;
import com.ttu.roman.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class ExpenseDAO extends AbstractDao<Expense> {
    public ExpenseDAO() {
        super(Expense.class);
    }

    @Autowired
    Config config;

    public List<Expense> getUserExpensesForPeriod(ExpenseRequest expenseRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String QUERY_NO_OFFSET = "from Expense e where e.state = :state and e.userId = :userId " +
                "and e.insertedAt >= :startDate and e.insertedAt <= :endDate order by e.id desc";

        String QUERY_WITH_OFFSET = "from Expense e where e.state = :state and e.userId = :userId " +
                "and e.insertedAt >= :startDate and e.insertedAt <= :endDate and e.id < :lastId order by e.id desc";

        boolean withLastId = expenseRequest.lastId == 0;
        Query query = em.createQuery(withLastId ? QUERY_NO_OFFSET : QUERY_WITH_OFFSET)
                .setParameter("state", Expense.STATE_ACCEPTED)
                .setParameter("userId", user.getGoogleUserId())
                .setParameter("startDate", expenseRequest.startDate)
                .setParameter("endDate", expenseRequest.endDate);

        if (withLastId) {
            query.setParameter("lastId", expenseRequest.lastId);
        }

        query.setMaxResults(config.getMaxExpensesResultAtOnce());

        return query.getResultList();
    }
}
