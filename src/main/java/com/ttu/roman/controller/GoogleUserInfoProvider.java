package com.ttu.roman.controller;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.ttu.roman.util.Config;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

@Component
public class GoogleUserInfoProvider {
    @Autowired
    private Config config;

    private Gson gson;
    private CloseableHttpClient httpClient;

    @PostConstruct
    private void init() {
        initGson();
        initHttpClient();
    }

    private void initHttpClient() {
        httpClient = HttpClients.createDefault();
    }

    private void initGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TokenInfo.class, new TokenInfoAdapter())
                .registerTypeAdapter(User.class, new UserInfoAdapter());
        gson = gsonBuilder.create();
    }

    public User getUserBy(String token) throws URISyntaxException, IOException {
        TokenInfo tokenInfo = getTokenInfo(token);
        return getUser(tokenInfo);
    }

    private User getUser(TokenInfo tokenInfo) throws URISyntaxException, IOException {
        URI userInfoUri = new URIBuilder()
                .setScheme("https")
                .setHost("www.googleapis.com")
                .setPath("/plus/v1/people/" + tokenInfo.getUserId())
                .setParameter("key", config.getGoogleAPIKey())
                .build();

        HttpGet httpGet = new HttpGet(userInfoUri);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String userInfoJson = EntityUtils.toString(response.getEntity(), "UTF-8");
            User user = gson.fromJson(userInfoJson, User.class);
            user.setTokenInfo(tokenInfo);
            return user;
        }
    }

    private TokenInfo getTokenInfo(String token) throws URISyntaxException, IOException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("www.googleapis.com")
                .setPath("/oauth2/v1/tokeninfo")
                .setParameter("access_token", token)
                .build();

        HttpPost httpPost = new HttpPost(uri);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String tokenInfoJson = EntityUtils.toString(response.getEntity(), "UTF-8");
            return gson.fromJson(tokenInfoJson, TokenInfo.class);
        }
    }

    static class TokenInfoAdapter extends TypeAdapter<TokenInfo> {
        @Override
        public void write(JsonWriter jsonWriter, TokenInfo tokenInfo) throws IOException {
        }

        @Override
        public TokenInfo read(JsonReader in) throws IOException {
            TokenInfo tokenInfo = new TokenInfo();
            in.beginObject();

            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "user_id":
                        tokenInfo.setUserId(in.nextString());
                        break;
                    case "audience":
                        tokenInfo.setAudience(in.nextString());
                        break;
                    case "expires_in":
                        int expiresIn = in.nextInt();
                        Date expiryTime = DateUtils.addSeconds(new Date(), expiresIn);
                        tokenInfo.setExpiresIn(expiryTime);
                        break;
                    default:
                        in.nextString();
                }
            }
            in.endObject();
            return tokenInfo;
        }
    }

    static class UserInfoAdapter implements com.google.gson.JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            User user = new User();
            JsonObject object = jsonElement.getAsJsonObject();
            user.setGoogleUserId(object.get("id").getAsString());
            user.setDisplayName(object.get("displayName").getAsString());
            return user;
        }
    }
}
