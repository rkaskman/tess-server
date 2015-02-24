package com.ttu.roman.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.ttu.roman.dao.StoreMatch;
import com.ttu.roman.store.Store;
import com.ttu.roman.store.StoreReceiptTag;
import net.java.frej.Regex;

import java.util.*;
import java.util.concurrent.*;

public class MatchesComputer {
    public static final double COMPUTATION_INTENSIVE_TASK_COEFFICIENT = 0.9;

    public Collection<StoreMatch> findBestMatches(Collection<Store> stores, final String parsedOcrResult) throws OcrDataMatchException {
        int poolSize = (int) (Runtime.getRuntime().availableProcessors() / COMPUTATION_INTENSIVE_TASK_COEFFICIENT);
        List<Callable<StoreMatch>> storeMatchTasks = new ArrayList<>();
        for (final Store store : stores) {
            storeMatchTasks.add(new Callable<StoreMatch>() {
                @Override
                public StoreMatch call() throws Exception {
                    return findMatchesFor(store, parsedOcrResult);
                }
            });
        }
        final List<StoreMatch> storeMatches = getStoreMatches(poolSize, storeMatchTasks);
        Collections.sort(storeMatches, new Comparator<StoreMatch>() {
            @Override
            public int compare(StoreMatch o1, StoreMatch o2) {
                return Integer.valueOf(o2.getNumberOfMatches()).
                        compareTo(Integer.valueOf(o1.getNumberOfMatches()));
            }
        });
        Collection<StoreMatch> bestMatches = Collections2.filter(storeMatches, new Predicate<StoreMatch>() {
            @Override
            public boolean apply(StoreMatch storeMatch) {
                return storeMatch.getNumberOfMatches() == storeMatches.get(0).getNumberOfMatches()
                        && storeMatch.getNumberOfMatches() != 0;
            }
        });

        return bestMatches;
    }

    private List<StoreMatch> getStoreMatches(int poolSize, List<Callable<StoreMatch>> storeMatchTasks) throws OcrDataMatchException {
        List<StoreMatch> storeMatches = new ArrayList<>();
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
            List<Future<StoreMatch>> matchResults = executorService.invokeAll(storeMatchTasks, 5000, TimeUnit.SECONDS);
            for (Future<StoreMatch> matchResult : matchResults) {
                storeMatches.add(matchResult.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new OcrDataMatchException("failed to parse data");
        }
        return storeMatches;
    }

    private StoreMatch findMatchesFor(Store store, String parsedOcrResult) {
        String[] tokens = parsedOcrResult.split(" ");
        Collection<Regex> regexes = Collections2.transform(store.getStoreReceiptTags(), new Function<StoreReceiptTag, Regex>() {
            @Override
            public Regex apply(StoreReceiptTag storeReceiptTag) {
                return new Regex("[^(" + storeReceiptTag.getTag() + ")]");
            }
        });

        int numberOfMatches = 0;
        for (String token : tokens) {
            for (Regex regex : regexes) {
                if (regex.match(token)) {
                    numberOfMatches++;
                }
            }
        }
        return new StoreMatch(store, numberOfMatches);
    }
}
