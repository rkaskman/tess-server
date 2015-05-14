package com.ttu.roman.ocrservice;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.ttu.roman.dao.ReceiptImageWrapperDAO;
import com.ttu.roman.model.ReceiptImageWrapper;
import com.ttu.roman.ocrservice.ReceiptTextRepresentation.TextLine;
import com.ttu.roman.util.Config;
import net.java.frej.Regex;
import net.java.frej.fuzzy.Fuzzy;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.postgresql.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class ImagesProcessingComponent {

    private static Logger LOG = Logger.getLogger(ImagesProcessingComponent.class);

    @Autowired
    CompanyRetrievingService companyRetrievingService;
    @Autowired
    ReceiptImageWrapperDAO receiptImageWrapperDAO;
    @Autowired
    Config config;
    @Autowired
    OCRResultHandler ocrResultHandler;

    private static Collection<String> totalSumLineKeywords = Arrays.asList("summa", "kokku", "maksta", "tasuda", "vahesumma");
    private Collection<Regex> totalSumLineRegexes;

    @PostConstruct
    public void initTotalSumLineRegexes() {
        totalSumLineRegexes = Collections2.transform(totalSumLineKeywords, new Function<String, Regex>() {
            @Nullable
            @Override
            public Regex apply(String s) {
                return new Regex(s);
            }
        });
    }

    public void process(ReceiptImageWrapper imageToProcess) throws IOException, ExecutionException, URISyntaxException {
        OcrResultHolder ocrResultHolder = doOcr(imageToProcess);
        if (ocrResultHolder != null) {
            try {
                new OCRResultParser().validateAndFormat(ocrResultHolder);
                String companyName = companyRetrievingService.retrieveCompanyName(ocrResultHolder.regNumber);
                ocrResultHandler.sendValidNotification(imageToProcess, ocrResultHolder, companyName);
            } catch (InvalidOCRResultException | ExecutionException e) {
                handleInvalidOCRResult(ocrResultHolder, e, imageToProcess.getRegistrationId());
            }
        }
    }

    private void handleInvalidOCRResult(OcrResultHolder ocrResultHolder, Exception e, String regId) throws IOException {
        ocrResultHandler.sendErrorNotification(ocrResultHolder, e.getMessage(), regId);
    }

    private OcrResultHolder doOcr(ReceiptImageWrapper imageToProcess) throws IOException {
        File imageFile = readAndPreprocess(imageToProcess.getRegNumberPicture(),
                imageToProcess.getRegNumberPictureExtension());

        Tesseract1 tesseract = new Tesseract1();
        tesseract.setLanguage("eng+est");
        String ocrString;

        try {
            ocrString = tesseract.doOCR(imageFile);
            ocrString = ocrString.replaceAll("[\n]+", "\n")
                    .toLowerCase()
                    .replaceAll(",", ".")
                    .replaceAll(":", " ")
                    .replaceAll("-", ".")
                    .replaceAll("_", ".")
                    .replaceAll("( )+", " ")
                    .replaceAll("[^a-z0-9\\.\\n ]", "");

            ReceiptTextRepresentation receiptTextRepresentation = createReceiptTextRepresentation(ocrString);
            return parseOcrResult(receiptTextRepresentation);
        } catch (TesseractException e) {
            LOG.error("Images recognition failed", e);
            return null;

        } finally {
            if (imageFile != null) {
                imageFile.delete();
            }
        }
    }

    private OcrResultHolder parseOcrResult(ReceiptTextRepresentation receiptTextRepresentation) {
        BigDecimal totalCost = null;
        List<TextLine> textLines = receiptTextRepresentation.getTextLines();

        Set<RegNumber> regNumbers = new LinkedHashSet<>();

        int lineNumber = 0;
        for (TextLine textLine : textLines) {
            int wordNumber = 0;
            for (String string : textLine.getWords()) {
                checkIsRegNumber(string, lineNumber, wordNumber, regNumbers);
                wordNumber++;
            }
            BigDecimal totalCostCheckResult = checkTotalCost(textLine);

            if (totalCostCheckResult != null) {
                totalCost = totalCostCheckResult;
            }

            lineNumber++;
        }

        String regNumber = "";
        String totalCostString = "";
        if (!regNumbers.isEmpty()) {
            regNumber = regNumbers.iterator().next().value;
        }
        if (totalCost != null) {
            totalCostString = totalCost.toString();
        }
        return new OcrResultHolder(regNumber, totalCostString);
    }

    private BigDecimal checkTotalCost(TextLine textLine) {
        BigDecimal totalCost = null;
        List<String> words = textLine.getWords();
        int i = 0;
        for (String word : words) {
            if (i < 2 && isTotalCostTag(word) && i + 1 < words.size()) {
                for (int j = i + 1; j < words.size(); j++) {
                    String currentWord = words.get(j);
                    if (currentWord.matches("[0-9]+\\.[0-9]{2}")) {
                        totalCost = new BigDecimal(currentWord);
                    }
                }
            }
            i++;
        }
        return totalCost;
    }

    private boolean isTotalCostTag(String word) {
        final String alphaDecimalWord = word.replaceAll("[^a-z0-9]", "");
        return Iterables.any(totalSumLineRegexes, new Predicate<Regex>() {
            @Override
            public boolean apply(@Nullable Regex regex) {
                return regex.match(alphaDecimalWord) || regex.presentInSequence(alphaDecimalWord) != -1;
            }
        });
    }

    private void checkIsRegNumber(String string, int lineNumber, int wordNumber, Set<RegNumber> regNumbers) {
        if (string.matches("([^0-9a-z]{1})?[0-9]{8}([^0-9a-z]{1})?")) {
            StringBuilder sb = new StringBuilder();
            for (char c : string.toCharArray()) {
                if (Character.isDigit(c)) {
                    sb.append(c);
                }
            }
            String regNumber = sb.toString();
            regNumbers.add(new RegNumber(regNumber, lineNumber, wordNumber));

        }
    }

    private ReceiptTextRepresentation createReceiptTextRepresentation(String ocrString) {
        ReceiptTextRepresentation receiptTextRepresentation = new ReceiptTextRepresentation();
        ArrayList<TextLine> textLines = new ArrayList<>();
        for (String line : ocrString.split("\n")) {
            TextLine textLine = new TextLine();
            textLine.add(line.split(" "));
            textLines.add(textLine);
        }
        receiptTextRepresentation.addTextLines(textLines);
        return receiptTextRepresentation;
    }

    private File readAndPreprocess(String image, String imageExtension) throws IOException {
        File imageFile = readIntoFile(image,
                imageExtension);
        Mat source = Highgui.imread(imageFile.getAbsolutePath(),
                Highgui.CV_LOAD_IMAGE_COLOR);

        Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);

        Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(19, 19));
        Mat temporaryMatrix = new Mat();


        Imgproc.resize(source, temporaryMatrix, new Size(source.cols() / 4, source.rows() / 4));
        Imgproc.morphologyEx(temporaryMatrix, temporaryMatrix, Imgproc.MORPH_CLOSE, structuringElement);
        Imgproc.resize(temporaryMatrix, temporaryMatrix, new Size(source.cols(), source.rows()));

        Core.divide(source, temporaryMatrix, temporaryMatrix, 1, CvType.CV_32F);
        Core.normalize(temporaryMatrix, source, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);

        Imgproc.threshold(source, source, -1, 255,
                Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        Mat invertColorMatrix = new Mat(source.rows(), source.cols(), source.type(), new Scalar(255, 255, 255));

        Core.subtract(invertColorMatrix, source, source);

        File resultImageFile = Files.createTempFile(Paths.get(config.getTempFileDir()), null,
                "." + imageExtension).toFile();

        Highgui.imwrite(resultImageFile.getAbsolutePath(), source);
        imageFile.delete();

        return resultImageFile;
    }

    private File readIntoFile(String encodedImageData, String imageExtension) throws IOException {
        byte[] decodedImageBytes = Base64.decode(encodedImageData);
        File imageFile = Files.createTempFile(Paths.get(config.getTempFileDir()), null,
                "." + imageExtension).toFile();
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(decodedImageBytes);
        fos.close();
        return imageFile;
    }

    public static class OcrResultHolder {
        private String regNumber;
        private String totalCost;

        public String getRegNumber() {
            return regNumber;
        }

        void setRegNumber(String regNumber) {
            this.regNumber = regNumber;
        }

        public String getTotalCost() {
            return totalCost;
        }

        void setTotalCost(String totalCost) {
            this.totalCost = totalCost;
        }

        private OcrResultHolder(String regNumber, String totalCost) {
            this.regNumber = regNumber;
            this.totalCost = totalCost;
        }
    }
}