package com.kr4ken.habitica.impl.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr4ken.habitica.exception.HabiticaHttpException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BufferedHeader;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class ApacheHttpClient extends AbstractHttpClient {

    private DefaultHttpClient httpClient;
    private ObjectMapper mapper;
    private List<Header> headers;

    public ApacheHttpClient() {
        this(new DefaultHttpClient());
    }

    public ApacheHttpClient(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
        this.mapper = new ObjectMapper();
        this.headers = new LinkedList<Header>();
    }

    @Override
    public void addHeader(String name, String value) {
       headers.add(new BasicHeader(name,value));
    }

    @Override
    public void clearHeaders() {
        headers.clear();
    }

    @Override
    public <T> T get(String url, Class<T> objectClass, String... params) {
        HttpGet httpGet = new HttpGet(expandUrl(url, params));
        httpGet.setHeaders(headers.toArray(new Header[0]));
        return getEntityAndReleaseConnection(objectClass, httpGet);
    }

    @Override
    public void delete(String url, String... params) {
        HttpDelete httpDelete = new HttpDelete(expandUrl(url, params));
        httpDelete.setHeaders(headers.toArray(new Header[0]));
        try {
            HttpResponse httpResponse = this.httpClient.execute(httpDelete);
        } catch (IOException e) {
            throw new HabiticaHttpException(e);
        } finally {
            httpDelete.releaseConnection();
        }
    }

    @Override
    public <T> T postForObject(String url, T object, Class<T> objectClass, String... params) {
        HttpPost httpPost = new HttpPost(expandUrl(url, params));
        httpPost.setHeaders(headers.toArray(new Header[0]));
        try {
            HttpEntity entity = new ByteArrayEntity(this.mapper.writeValueAsBytes(object), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            return getEntityAndReleaseConnection(objectClass, httpPost);
        } catch (JsonProcessingException e) {
            // TODO : custom exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T putForObject(String url, T object, Class<T> objectClass, String... params) {
        HttpPut put = new HttpPut(expandUrl(url, params));
        put.setHeaders(headers.toArray(new Header[0]));
        try {
            HttpEntity entity = new ByteArrayEntity(this.mapper.writeValueAsBytes(object), ContentType.APPLICATION_JSON);
            put.setEntity(entity);

            return getEntityAndReleaseConnection(objectClass, put);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URI postForLocation(String url, Object object, String... params) {
        HttpPost httpPost = new HttpPost(expandUrl(url, params));
        httpPost.setHeaders(headers.toArray(new Header[0]));
        try {
            HttpEntity entity = new ByteArrayEntity(this.mapper.writeValueAsBytes(object), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            HttpResponse httpResponse = this.httpClient.execute(httpPost);

            Header location = httpResponse.getFirstHeader("Location");
            if (location != null) {
                return URI.create(location.getValue());
            } else {
                // TODO : error
                throw new NullPointerException();
            }
        } catch (JsonProcessingException e) {
            // TODO : custom exception
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new HabiticaHttpException(e);
        } finally {
            httpPost.releaseConnection();
        }
    }

    private <T> T getEntityAndReleaseConnection(Class<T> objectClass, HttpRequestBase httpRequest) {
        try {
            HttpResponse httpResponse = this.httpClient.execute(httpRequest);

            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                return this.mapper.readValue(httpEntity.getContent(), objectClass);
            } else {
                // TODO : error
                throw new NullPointerException();
            }
        } catch (IOException e) {
            throw new HabiticaHttpException(e);
        } finally {
            httpRequest.releaseConnection();
        }
    }
}
