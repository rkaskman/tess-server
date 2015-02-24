package com.ttu.roman.service;


import com.ttu.roman.dao.StoreDao;
import com.ttu.roman.dao.StoreMatch;
import com.ttu.roman.store.Store;
import com.ttu.roman.util.Config;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class StoreReceiptMatcherService implements InitializingBean {
    @Autowired
    StoreDao storeDao;
    @Autowired
    Config config;
    private List<Store> stores;

    private long storesCacheExpiryTime;

    @Override
    public void afterPropertiesSet() throws Exception {
        stores = storeDao.findActive();
        setStoreCacheExpiryTime();
    }

    private void setStoreCacheExpiryTime() {
        storesCacheExpiryTime = System.currentTimeMillis() + 1000000000;
    }

    public Collection<StoreMatch> getMatchingStores(String ocrParsedData) throws OcrDataMatchException {
        updateCacheIfNecessary();
        Collection<StoreMatch> bestMatches = new MatchesComputer().findBestMatches(stores, ocrParsedData);
        return bestMatches;
    }

    private void updateCacheIfNecessary() {
        if (System.currentTimeMillis() > storesCacheExpiryTime) {
            storeDao.refresh(stores);
            setStoreCacheExpiryTime();
        }
    }
}
