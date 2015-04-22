package com.ttu.roman.ocrservice.gcm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.ttu.roman.auth.TokenInfo;
import com.ttu.roman.model.Expense;
import com.ttu.roman.ocrservice.ImagesProcessingComponent;
import com.ttu.roman.util.Config;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class GcmNotificationSender {

    private static Logger LOG = Logger.getLogger(GcmNotificationSender.class);

    public static final int STATUS_CODE_OK = 200;
    @Autowired
    private Config config;


    private Gson gson;
    private CloseableHttpClient httpClient;
    private URI cloudServiceURI;

    @PostConstruct
    private void init() throws URISyntaxException {
        initGson();
        initHttpClient();
        initCloudServiceUri();
    }

    private void initGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Expense.class, new SuccessfulMessageAdapter())
                .registerTypeAdapter(Response.class, new ResponseAdapter())
                .registerTypeAdapter(ErroneousResponseWrapper.class, new ErrorMessageTypeAdapter());

        gson = gsonBuilder.create();
    }

    private void initCloudServiceUri() throws URISyntaxException {
        cloudServiceURI = new URIBuilder()
                .setScheme("https")
                .setHost("android.googleapis.com")
                .setPath("/gcm/send")
                .build();
    }

    public Response sendSuccessfulMessageToCloud(Expense expense) throws IOException, URISyntaxException {
        HttpPost httpPost = createCloudPostRequest();

        String string = gson.toJson(expense);
        StringEntity entity = new StringEntity(string, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        return handleCloudHttpResponseOnSentMessage(response);
    }

    private Response handleCloudHttpResponseOnSentMessage(CloseableHttpResponse response) throws IOException {
        double statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == STATUS_CODE_OK) {
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            Response parsedResponse = gson.fromJson(jsonResponse, Response.class);
            return parsedResponse;
        } else {
            Response errorResponse = new Response();
            errorResponse.error = String.valueOf(statusCode);
            return errorResponse;
        }
    }

    private HttpPost createCloudPostRequest() {
        HttpPost httpPost = new HttpPost(cloudServiceURI);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
        httpPost.setHeader("Authorization", "key=" + config.getGoogleAPIKey());
        return httpPost;
    }

    private void initHttpClient() {
        httpClient = HttpClients.createDefault();
    }

    public Response sendErroneousNotification(ImagesProcessingComponent.OcrResultHolder ocrResultHolder, String message, String regId) throws IOException {
        HttpPost httpPost = createCloudPostRequest();
        ErroneousResponseWrapper erroneousResponseWrapper = new ErroneousResponseWrapper(ocrResultHolder, message, regId);

        String string = gson.toJson(erroneousResponseWrapper);
        StringEntity entity = new StringEntity(string, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        return handleCloudHttpResponseOnSentMessage(response);
    }

    private class ErrorMessageTypeAdapter extends TypeAdapter<ErroneousResponseWrapper> {
        @Override
        public void write(JsonWriter out, ErroneousResponseWrapper erroneousResponseWrapper) throws IOException {
            ImagesProcessingComponent.OcrResultHolder ocrResultHolder = erroneousResponseWrapper.ocrResultHolder;
            String message = erroneousResponseWrapper.message;

            out.beginObject();

            out.name("data").beginObject();
            out.name("recognizedRegNr").value(ocrResultHolder.getRegNumber());
            out.name("recognizedTotalCost").value(ocrResultHolder.getTotalCost());
            out.name("message").value(message);
            out.endObject();

            out.name("registration_ids").beginArray();
            out.value(erroneousResponseWrapper.regId);
            out.endArray();

            out.endObject();
        }

        @Override
        public ErroneousResponseWrapper read(JsonReader jsonReader) throws IOException {
            return null;
        }
    }

    private class ResponseAdapter extends TypeAdapter<Response> {
        @Override
        public void write(JsonWriter jsonWriter, Response response) throws IOException {
        }

        @Override
        public Response read(JsonReader in) throws IOException {
            Response response = new Response();
            in.beginObject();

            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "multicast_id":
                        response.multicastId = in.nextString();
                        break;
                    case "success":
                        response.success = in.nextInt();
                        break;
                    case "failure":
                        response.failure = in.nextInt();
                        break;
                    case "canonical_ids":
                        response.canonicalIs = in.nextString();
                        break;
                    case "results":
                        in.beginArray();
                        in.beginObject();
                        while (in.hasNext()) {
                            switch (in.nextName()) {
                                case "message_id":
                                    response.messageId = in.nextString();
                                    break;
                                case "error":
                                    response.error = in.nextString();
                                    break;
                                case "registration_id":
                                    //not needed
                                    in.nextString();
                                    break;
                            }
                        }
                        in.endObject();
                        in.endArray();

                }
            }
            in.endObject();

            return response;
        }
    }

    private class SuccessfulMessageAdapter extends TypeAdapter<Expense> {
        @Override
        public void write(JsonWriter out, Expense expense) throws IOException {
            out.beginObject();

            out.name("data").beginObject();
            out.name("id").value(expense.getId());
            out.name("companyName").value(expense.getCompanyName());
            out.name("companyRegNumber").value(expense.getCompanyRegNumber());
            out.name("totalCost").value(expense.getTotalCost());
            out.name("currency").value(expense.getCurrency());
            out.endObject();

            out.name("registration_ids").beginArray();
            out.value(expense.getRegistrationId());
            out.endArray();

            out.endObject();
        }

        @Override
        public Expense read(JsonReader jsonReader) throws IOException {
            return null;
        }
    }

    public class Response {
        private String multicastId;
        private int success;
        private int failure;
        private String canonicalIs;
        private String error;
        private String messageId;

        public String getMulticastId() {
            return multicastId;
        }

        public int getSuccess() {
            return success;
        }

        public int getFailure() {
            return failure;
        }

        public String getCanonicalIs() {
            return canonicalIs;
        }

        public String getError() {
            return error;
        }

        public String getMessageId() {
            return messageId;
        }
    }

    private class ErroneousResponseWrapper {
        private ImagesProcessingComponent.OcrResultHolder ocrResultHolder;
        private String message;
        private String regId;

        public ErroneousResponseWrapper(ImagesProcessingComponent.OcrResultHolder ocrResultHolder, String message, String regId) {
            this.ocrResultHolder = ocrResultHolder;
            this.message = message;
            this.regId = regId;
        }
    }
}