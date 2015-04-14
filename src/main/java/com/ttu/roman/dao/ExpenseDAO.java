package com.ttu.roman.dao;

import com.ttu.roman.controller.request.ExpenseRequest;
import com.ttu.roman.model.Expense;
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

    public List<Expense> getUserExpensesForPeriod(ExpenseRequest expenseRequest) {
        String QUERY_NO_OFFSET = "from Expense e where e.state = :state and e.userId = :userId " +
                "and e.insertedAt >= :startDate and e.insertedAt <= :endDate order by e.id desc";

        String QUERY_WITH_OFFSET = "from Expense e where e.state = :state and e.userId = :userId " +
                "and e.insertedAt >= :startDate and e.insertedAt <= :endDate and e.id < :lastId order by e.id desc";

        boolean withLastId = expenseRequest.lastId == 0;
        Query query = em.createQuery(withLastId ? QUERY_NO_OFFSET : QUERY_WITH_OFFSET)
                .setParameter("state", Expense.STATE_ACCEPTED)
                .setParameter("userId", expenseRequest.userId)
                .setParameter("startDate", expenseRequest.startDate)
                .setParameter("endDate", expenseRequest.endDate);

        if (withLastId) {
            query.setParameter("lastId", expenseRequest.lastId);
        }

        query.setMaxResults(expenseRequest.maxResults);

        return query.getResultList();
    }
}
