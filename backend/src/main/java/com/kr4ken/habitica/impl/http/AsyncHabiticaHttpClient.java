package com.kr4ken.habitica.impl.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr4ken.habitica.exception.HabiticaHttpException;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Response;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncHabiticaHttpClient extends AbstractHttpClient {

    private AsyncHttpClient asyncHttpClient;
    private ObjectMapper mapper;
    private FluentCaseInsensitiveStringsMap headers;

    public AsyncHabiticaHttpClient() {
        this(new AsyncHttpClient());
    }

    public AsyncHabiticaHttpClient(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
        this.mapper = new ObjectMapper();
        this.headers = new FluentCaseInsensitiveStringsMap();
    }

    @Override
    public void addHeader(String name, String value) {
        ArrayList<String> listValues = new ArrayList<>();
        listValues.add(value);
        headers.add(name,listValues);
    }

    @Override
    public void clearHeaders() {
        headers.clear();
    }

    @Override
//    public <T> T get(String url, final Class<T> objectClass, String... params) {
      public <Res> Res get(String url, Class<Res> responseClass, String... params){
        Future<Res> f;
        try {
            f = asyncHttpClient.prepareGet(expandUrl(url, params)).setHeaders(headers).execute(
                    new AsyncCompletionHandler<Res>() {

                        @Override
                        public Res onCompleted(Response response) throws Exception {
                            return mapper.readValue(response.getResponseBody(), responseClass);
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            throw new HabiticaHttpException(t);
                        }
                    });
            return f.get();
//        } catch (IOException | InterruptedException | ExecutionException e) {
        } catch ( InterruptedException | ExecutionException e) {
            throw new HabiticaHttpException(e);
        }
    }

    @Override
    public void delete(String url,String... params) {
        try {
            asyncHttpClient.prepareDelete(expandUrl(url, params)).setHeaders(headers).execute();
        } catch ( Exception  e) {
            throw new HabiticaHttpException(e);
        }
    }


    @Override
//    public <T> T postForObject(String url, T object, final Class<T> objectClass, String... params) {
    public <Req,Res> Res postForObject(String url, Req requestObject, Class<Res> responseClass, String... params){
        Future<Res> f;
        try {
            byte[] body = this.mapper.writeValueAsBytes(requestObject);
            f = asyncHttpClient.preparePost(expandUrl(url, params)).setBody(body).setHeaders(headers).execute(
                    new AsyncCompletionHandler<Res>() {

                        @Override
                        public Res onCompleted(Response response) throws Exception {
                            return mapper.readValue(response.getResponseBody(), responseClass);
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            throw new HabiticaHttpException(t);
                        }
                    });
            return f.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new HabiticaHttpException(e);
        }
    }

    @Override
    public URI postForLocation(String url, Object object, String... params) {
        Future<URI> f;
        try {
            byte[] body = this.mapper.writeValueAsBytes(object);
            f = asyncHttpClient.preparePost(expandUrl(url, params)).setBody(body).setHeaders(headers).execute(
                    new AsyncCompletionHandler<URI>() {

                        @Override
                        public URI onCompleted(Response response) throws Exception {
                            String location = response.getHeader("Location");
                            if (location != null) {
                                return URI.create(location);
                            } else {
                                throw new HabiticaHttpException("Location header not set");
                            }
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            throw new HabiticaHttpException(t);
                        }
                    });
            return f.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new HabiticaHttpException(e);
        }
    }

    @Override
//    public <T> T putForObject(String url, T object, final Class<T> objectClass, String... params) {
    public <Req,Res> Res putForObject(String url, Req requestObject, Class<Res> responseClass, String... params){
        Future<Res> f;
        try {
            byte[] body = this.mapper.writeValueAsBytes(requestObject);
            f = asyncHttpClient.preparePut(expandUrl(url, params)).setBody(body).setHeaders(headers).execute(
                    new AsyncCompletionHandler<Res>() {

                        @Override
                        public Res onCompleted(Response response) throws Exception {
                            return mapper.readValue(response.getResponseBody(), responseClass);
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            throw new HabiticaHttpException(t);
                        }
                    });
            return f.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new HabiticaHttpException(e);
        }
    }
}
