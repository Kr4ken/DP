package com.kr4ken.habitica.impl;

import com.kr4ken.habitica.Habitica;
import com.kr4ken.habitica.HabiticaHttpClient;
import com.kr4ken.habitica.domain.Argument;
import com.kr4ken.habitica.domain.HabiticaEntity;
import com.kr4ken.habitica.domain.HabiticaResponse;
import com.kr4ken.habitica.domain.Task;
import com.kr4ken.habitica.impl.http.ApacheHttpClient;
import com.kr4ken.habitica.impl.http.RestTemplateHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kr4ken.habitica.impl.HabiticaUrl.*;

public class HabiticaImpl implements Habitica {
    private HabiticaHttpClient httpClient;
    private String apiUser;
    private String apiKey;

    private static Logger logger = LoggerFactory.getLogger(HabiticaImpl.class);

    // FIXME : remove me
    public HabiticaImpl(String applicationKey, String accessToken) {
        this(applicationKey, accessToken, new RestTemplateHttpClient());
//        this(applicationKey, accessToken, new ApacheHttpClient());
    }
    public HabiticaImpl(String apiUser, String apiKey, HabiticaHttpClient httpClient) {
        this.apiUser = apiUser;
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.httpClient.addHeader("x-api-user",apiUser);
        this.httpClient.addHeader("x-api-key",apiKey);
    }
    /**
     * Tasks
     */
    @Override
    public List<Task> getUserTasks(Argument... args) {
        HabiticaResponse response  =  get(createUrl(GET_USER_TASKS).params(args).asString(), HabiticaResponse.class);
        for (Task task : response.getData()) {
            task.setInternalHabitica(this);
        }
        return response.getData();
    }

    /**
     * Internal methods
     */
    private <T> T postForObject(String url, T object, Class<T> objectClass, String... params) {
        logger.debug("PostForObject request on Habitica API at url {} for class {} with params {}", url, objectClass.getCanonicalName(), params);
        return httpClient.postForObject(url, object, objectClass, params);
    }

    private void postForLocation(String url, Object object, String... params) {
        logger.debug("PostForLocation request on Habitica API at url {} for class {} with params {}", url, object.getClass().getCanonicalName(), params);
        httpClient.postForLocation(url, object, params);
    }

    private <T> T get(String url, Class<T> objectClass, String... params) {
        logger.debug("Get request on Habitica API at url {} for class {} with params {}", url, objectClass.getCanonicalName(), params);
        return httpClient.get(url, objectClass, params);
    }

    private void delete(String url, String... params) {
        logger.debug("Delete request on Habitica API at url {} for class {} with params {}", url, params);
        httpClient.delete(url, params);
    }

    private <T> T put(String url, T object, Class<T> objectClass, String... params) {
        logger.debug("Put request on Habitica API at url {} for class {} with params {}", url, object.getClass().getCanonicalName(), params);
        return httpClient.putForObject(url, object, objectClass, params);
    }
}

