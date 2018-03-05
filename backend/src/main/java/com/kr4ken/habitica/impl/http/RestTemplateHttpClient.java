package com.kr4ken.habitica.impl.http;

import com.kr4ken.habitica.HabiticaHttpClient;
import com.kr4ken.habitica.exception.HabiticaHttpException;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class RestTemplateHttpClient implements HabiticaHttpClient {

    private RestTemplate restTemplate;
    private MultiValueMap<String,String> headers;

    public RestTemplateHttpClient() {
        headers = new LinkedMultiValueMap<>();
        restTemplate = new RestTemplate();

    }

    @Override
    public void addHeader(String name, String value) {
       headers.add(name,value);
    }

    @Override
    public void clearHeaders() {
        headers.clear();

    }

    @Override
    public <Req,Res> Res postForObject(String url, Req requestObject, Class<Res> responseClass, String... params){
        try {
            return restTemplate.exchange(url,HttpMethod.POST,new HttpEntity<Req>(requestObject,headers), responseClass,params).getBody();
        } catch (RestClientException e) {
            throw new HabiticaHttpException(e);
        }

    }

    @Override
    public URI postForLocation(String url, Object object, String... params) {
        try {
            return restTemplate.exchange(url,HttpMethod.POST,new HttpEntity<>(headers),URI.class, params).getBody();
        } catch (RestClientException e) {
            throw new HabiticaHttpException(e);
        }
    }

    @Override
//    public <T> T get(String url, Class<T> objectClass, String... params) {
    public <Res> Res get(String url, Class<Res> responseClass, String... params){
        try {
//            return restTemplate.getForObject(url, objectClass, params);
            return restTemplate.exchange(url,HttpMethod.GET,new HttpEntity<>(headers), responseClass,params).getBody();
        } catch (RestClientException e) {
            throw new HabiticaHttpException(e);
        }
    }

    @Override
    public void delete(String url,  String... params) {
        try {
//            restTemplate.delete(url, params);
            restTemplate.exchange(url,HttpMethod.DELETE,new HttpEntity<>(headers),Object.class,params);
            return;
        } catch (RestClientException e) {
            throw new HabiticaHttpException(e);
        }
    }

    @Override
//    public <T> T putForObject(String url, T object, Class<T> objectClass, String... params) {
    public <Req,Res> Res putForObject(String url, Req requestObject, Class<Res> responseClass, String... params){
        try {
//            return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(object), objectClass, params).getBody();
            return restTemplate.exchange(url,HttpMethod.PUT,new HttpEntity<Req>(requestObject,headers), responseClass,params).getBody();
        } catch (RestClientException e) {
            throw new HabiticaHttpException(e);
        }
    }

}

