package com.ttu.roman.ocrservice;

import com.ttu.roman.util.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CompanyRetrievingService {
    @Autowired
    Config config;

    public String retrieveCompanyName(String companyRegNumber) throws UnknownCompanyException, IOException {
            Document doc = Jsoup.connect(config.getBusinessRegisterURL())
                    .data("rkood", companyRegNumber)
                    .data("search", "1")
                    .data("lang", "est")
                    .userAgent("Mozilla")
                    .timeout(3000)
                    .post();

            Elements elements = doc.select("a[href^=javascript: navigate2('/ettevotja.py']");

            if (elements.isEmpty()) {
                throw new UnknownCompanyException("Could not retrieve company for regNumber=" +companyRegNumber);
            }
            Element element = elements.get(0);
            return element.html();
    }
}
