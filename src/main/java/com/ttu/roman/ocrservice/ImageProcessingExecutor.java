package com.ttu.roman.ocrservice;

import com.ttu.roman.dao.ReceiptImageWrapperDAO;
import com.ttu.roman.model.ReceiptImageWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
public class ImageProcessingExecutor extends Thread {
    private static final double COMPUTATION_INTENSIVE_TASK_COEFFICIENT = 0.9;
    private static final int SLEEP_TIME_MILLIS = 5000;
    public static final int EXECUTOR_TIMEOUT = 20;

    static Logger LOG = Logger.getLogger(ImageProcessingExecutor.class);

    @Autowired
    ImagesProcessingComponent imagesProcessingComponent;
    @Autowired
    ReceiptImageWrapperDAO receiptImageWrapperDAO;

    private ExecutorService executorService;

    public ImageProcessingExecutor() {
        super();
        int poolSize = Runtime.getRuntime().availableProcessors()  + 1;
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void run() {
        for (;;) {
            List<ReceiptImageWrapper> imagesToProcess = receiptImageWrapperDAO.findBatchForProcessing();
            if (!imagesToProcess.isEmpty()) {
                try {
                    process(imagesToProcess);
                } catch (Exception e) {
                    LOG.error("Exception occurred", e);
                } finally {
                    for (ReceiptImageWrapper image : imagesToProcess) {
                        receiptImageWrapperDAO.delete(image);
                    }
                }
            } else {
                try {
                    join(SLEEP_TIME_MILLIS);
                    LOG.info("No images to process found. Sleeping...");
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private void process(final List<ReceiptImageWrapper> imagesToProcess) throws InterruptedException, ExecutionException {
        List<Callable<Void>> processingTask = new ArrayList<>();
        for (final ReceiptImageWrapper imageToProcess : imagesToProcess) {
            processingTask.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    imagesProcessingComponent.process(imageToProcess);
                    return null;
                }
            });
        }
        List<Future<Void>> processResults = executorService.invokeAll(processingTask, EXECUTOR_TIMEOUT, TimeUnit.SECONDS);
        for (Future<Void> processResult : processResults) {
            processResult.get();
        }
    }
}
