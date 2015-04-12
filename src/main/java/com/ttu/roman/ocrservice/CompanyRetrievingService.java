package com.ttu.roman.ocrservice;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ttu.roman.util.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class CompanyRetrievingService {
    private static final int SERVICE_REQUEST_TIMEOUT_MILLIS = 3000;

    @Autowired
    Config config;

    LoadingCache<String, String> companyNameCache;

    @PostConstruct
    void init() {
        companyNameCache = CacheBuilder.newBuilder()
                .maximumSize(config.getCompanyNameCacheMaxSize())
                .expireAfterWrite(config.getCompanyNameCacheExpiryHours(), TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, String>() {
                            public String load(String companyRegNumber) throws IOException, InvalidOCRResultException {
                                return findCompanyName(companyRegNumber);
                            }
                        });
    }

    public String retrieveCompanyName(String companyRegNumber) throws InvalidOCRResultException, IOException, ExecutionException {
        return companyNameCache.get(companyRegNumber);
    }

    private String findCompanyName(String companyRegNumber) throws IOException, InvalidOCRResultException {
        Document doc = Jsoup.connect(config.getBusinessRegisterURL())
                .data("rkood", companyRegNumber)
                .data("search", "1")
                .data("lang", "est")
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .timeout(SERVICE_REQUEST_TIMEOUT_MILLIS)
                .post();

        Elements elements = doc.select("a[href^=javascript: navigate2('/ettevotja.py']");

        if (elements.isEmpty()) {
            throw new InvalidOCRResultException("Could not retrieve company for regNumber=" + companyRegNumber);
        }
        Element element = elements.get(0);
        return element.html();
    }
}