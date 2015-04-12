package com.ttu.roman.controller;

import com.ttu.roman.auth.User;
import com.ttu.roman.dao.ReceiptImageWrapperDAO;
import com.ttu.roman.model.ReceiptImageWrapper;
import com.ttu.roman.ocrservice.ImagesWrapper;
import com.ttu.roman.ocrservice.RecognitionException;
import com.ttu.roman.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

@Controller
@RequestMapping("/ocr")
public class OcrServiceController {

    @Autowired
    Config config;

    @Autowired
    ReceiptImageWrapperDAO receiptImageWrapperDAO;

    @RequestMapping(value = "/postImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String postImage(@RequestBody ImagesWrapper imagesWrapper) throws IOException, RecognitionException {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ReceiptImageWrapper receiptImageWrapper = new ReceiptImageWrapper();

        receiptImageWrapper.setRegNumberPicture(imagesWrapper.regNumberImage.encodedImage);
        receiptImageWrapper.setRegNumberPictureExtension(imagesWrapper.regNumberImage.fileExtension);
        receiptImageWrapper.setTotalCostPicture(imagesWrapper.totalCostImage.encodedImage);
        receiptImageWrapper.setTotalCostPictureExtension(imagesWrapper.totalCostImage.fileExtension);
        receiptImageWrapper.setUserId(user.getGoogleUserId());
        //TODO: add real value after cloud messaging is introduced
        receiptImageWrapper.setRegistrationId("123");
        receiptImageWrapper.setInsertedAt(new Timestamp(new Date().getTime()));
        receiptImageWrapper.setState(ReceiptImageWrapper.STATE_NON_PROCESSED);
        receiptImageWrapperDAO.create(receiptImageWrapper);

        return "";
    }
}