package com.ttu.roman.dao;


import com.ttu.roman.store.Store;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
@Transactional
public class StoreDao extends AbstractDao<Store>{
    public StoreDao() {
        super(Store.class);
    }

    public void refresh(List<Store> stores) {
        em.refresh(stores, LockModeType.OPTIMISTIC);
    }

    public List<Store> findActive() {
        return em.createQuery("from Store s where s.status=:status", Store.class).
                setParameter("status", Store.STATUS_ACTIVE).getResultList();
    }
}
