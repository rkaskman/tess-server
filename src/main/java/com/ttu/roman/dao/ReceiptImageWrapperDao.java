package com.ttu.roman.dao;

import com.ttu.roman.model.ReceiptImageWrapper;
import com.ttu.roman.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class ReceiptImageWrapperDAO extends AbstractDao<ReceiptImageWrapper>{
    @Autowired
    Config config;

    public ReceiptImageWrapperDAO() {
        super(ReceiptImageWrapper.class);
    }

    public List<ReceiptImageWrapper> findBatchForProcessing() {
        return em.createQuery("from ReceiptImageWrapper wr where wr.state = :state order by wr.id", ReceiptImageWrapper.class)
                .setParameter("state", ReceiptImageWrapper.STATE_NON_PROCESSED)
                .setMaxResults(config.getProcessedImagesBatchSize())
                .getResultList();
    }
}
