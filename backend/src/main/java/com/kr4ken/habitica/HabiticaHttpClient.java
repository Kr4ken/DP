package com.kr4ken.habitica;

import java.net.URI;

public interface HabiticaHttpClient {
    // Custom headers for Habitica authentification
    public void addHeader(String name, String value);

    public void clearHeaders();

    // TODO add IO exception
//    public <T> T get(String url, Class<T> objectClass, String... params);
    public <Res> Res get(String url, Class<Res> responseClass, String... params);

//    public <T> T postForObject(String url, T requestObject, Class<T> responseClass, String... params);
    public <Req,Res> Res postForObject(String url, Req requestObject, Class<Res> responseClass, String... params);

    public URI postForLocation(String url, Object requestObject, String... params);
//    public URI postForLocation(String url, Object requestObject, String... params);

    public <Req,Res> Res putForObject(String url, Req requestObject, Class<Res> responseClass, String... params);
//    public <Req,Res> Res postForObject(String url, Req requestObject, Class<Res> responseClass, String... params);

    public void delete(String url, String... params);
}
