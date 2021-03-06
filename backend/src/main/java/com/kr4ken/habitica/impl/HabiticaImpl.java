package com.kr4ken.habitica.impl;

import com.kr4ken.habitica.Habitica;
import com.kr4ken.habitica.HabiticaHttpClient;
import com.kr4ken.habitica.domain.*;
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
        this.httpClient.addHeader("x-api-user", apiUser);
        this.httpClient.addHeader("x-api-key", apiKey);
    }

    /**
     * Tasks
     */
    @Override
    public List<Task> getUserTasks(Argument... args) {
        HabiticaResponse response = get(createUrl(GET_USER_TASKS).params(args).asString(), HabiticaResponse.class);
        for (Task task : response.getData()) {
            task.setInternalHabitica(this);
        }
        return response.getData();
    }

    @Override
    public Task getTask(String taskId, Argument... args) {
        HabiticaResponseTask response = get(createUrl(GET_TASK).params(args).asString(), HabiticaResponseTask.class, taskId);
        if (!response.getSuccess()) return null;
        response.getData().setInternalHabitica(this);
        return response.getData();
    }

    @Override
    public Task createTask(Task task) {
        HabiticaResponseTask response = postForObject(createUrl(CREATE_USER_TASK).asString(), task, HabiticaResponseTask.class);
        if (!response.getSuccess()) return null;
        response.getData().setInternalHabitica(this);
        return response.getData();
    }

    @Override
    public Task updateTask(Task task) {
        HabiticaResponseTask response = put(createUrl(UPDATE_TASK).asString(), task, HabiticaResponseTask.class,task.getAlias());
        if (!response.getSuccess()) return null;
        response.getData().setInternalHabitica(this);
        return response.getData();
    }

    @Override
    public void deleteTask(String taskId) {
        delete(createUrl(DELETE_TASK).asString(),taskId);
    }

    /**
     * Tags
     *
     */

    @Override
    public List<Tag> getUserTags() {
        HabiticaResponseTags response = get(createUrl(GET_USER_TAGS).asString(), HabiticaResponseTags.class);
        for (Tag tag : response.getData()) {
            tag.setInternalHabitica(this);
        }
        return response.getData();
    }

    @Override
    public Tag getTag(String tagId) {
        HabiticaResponseTag response = get(createUrl(GET_TAG).asString(), HabiticaResponseTag.class, tagId);
        if (!response.getSuccess()) return null;
        response.getData().setInternalHabitica(this);
        return response.getData();
    }

    @Override
    public Tag createTag(Tag tag) {
        HabiticaResponseTag response = postForObject(createUrl(CREATE_TAG).asString(), tag, HabiticaResponseTag.class);
        if (!response.getSuccess()) return null;
        response.getData().setInternalHabitica(this);
        return response.getData();
    }

    @Override
    public Tag updateTag(Tag tag) {
        HabiticaResponseTag response = put(createUrl(UPDATE_TAG).asString(), tag, HabiticaResponseTag.class,tag.getId());
        if (!response.getSuccess()) return null;
        response.getData().setInternalHabitica(this);
        return response.getData();
    }

    @Override
    public void deleteTag(String tagId) {
        delete(createUrl(DELETE_TAG).asString(),tagId);
    }

    /**
     * Internal methods
     */
//    private <T> T postForObject(String url, T object, Class<T> objectClass, String... params) {
    public <Req, Res> Res postForObject(String url, Req requestObject, Class<Res> responseClass, String... params) {
        logger.debug("PostForObject request on Habitica API at url {} for class {} with params {}", url, responseClass.getCanonicalName(), params);
        return httpClient.postForObject(url, requestObject, responseClass, params);
    }

    private void postForLocation(String url, Object object, String... params) {
        logger.debug("PostForLocation request on Habitica API at url {} for class {} with params {}", url, object.getClass().getCanonicalName(), params);
        httpClient.postForLocation(url, object, params);
    }

    //    private <T> T get(String url, Class<T> objectClass, String... params) {
    public <Res> Res get(String url, Class<Res> responseClass, String... params) {
        logger.debug("Get request on Habitica API at url {} for class {} with params {}", url, responseClass.getCanonicalName(), params);
        return httpClient.get(url, responseClass, params);
    }

    private void delete(String url, String... params) {
        logger.debug("Delete request on Habitica API at url {} for class {} with params {}", url, params);
        httpClient.delete(url, params);
    }

    //    private <T> T put(String url, T object, Class<T> objectClass, String... params) {
    public <Req, Res> Res put(String url, Req requestObject, Class<Res> responseClass, String... params) {
        logger.debug("Put request on Habitica API at url {} for class {} with params {}", url, responseClass.getClass().getCanonicalName(), params);
        return httpClient.putForObject(url, requestObject, responseClass, params);
    }
}

