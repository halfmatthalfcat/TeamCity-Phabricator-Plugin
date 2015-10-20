package com.couchmate.teamcity.phabricator;

import com.couchmate.teamcity.phabricator.TCPhabException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mjo20 on 10/10/2015.
 */
public final class HttpRequestBuilder {

    private HttpRequestBase httpRequest = null;
    private URI uri = null;
    private String host = null;
    private Integer port = null;
    private String path = null;
    private String scheme = null;
    private List<KeyValue> params = new ArrayList<KeyValue>();
    private StringEntity body = null;

    public HttpRequestBuilder get(){
        this.httpRequest = new HttpGet();
        return this;
    }

    public HttpRequestBuilder post() {
        this.httpRequest = new HttpPost();
        return this;
    }

    public HttpRequestBuilder setScheme(String scheme){
        if(CommonUtils.isNullOrEmpty(scheme)) throw new IllegalArgumentException("Must provide a valid scheme");
        else if (!scheme.equals("http") || !scheme.equals("https")) throw new IllegalArgumentException(String.format("Scheme %s is not supported", scheme));
        else this.scheme = scheme;
        return this;
    }

    public HttpRequestBuilder setHost(String host){
        if(CommonUtils.isNullOrEmpty(host)) throw new IllegalArgumentException("Must provide a valid host");
        else this.host = host;
        return this;
    }

    public HttpRequestBuilder setPort(int port){
        this.port = port;
        return this;
    }

    public HttpRequestBuilder setPath(String path){
        if(CommonUtils.isNullOrEmpty(path)) throw new IllegalArgumentException("Must provide a valid path");
        else this.path = path;
        return this;
    }

    public HttpRequestBuilder setParam(String key, String value){
        if(CommonUtils.isNullOrEmpty(key)) throw new IllegalArgumentException("Must provide a valid param");
        else this.params.add(new KeyValue(key, value));
        return this;
    }

    public HttpRequestBuilder setParams(List<KeyValue> params){
        for(KeyValue param : params){
            this.params.add(param);
        }
        return this;
    }

    public HttpRequestBuilder setBody(String body) throws UnsupportedEncodingException {
        if(CommonUtils.isNullOrEmpty(body)) throw new IllegalArgumentException("Must provide a valid body");
        else this.body = new StringEntity(body);
        return this;
    }

    public HttpRequestBase build() throws TCPhabException, URISyntaxException {
        if(httpRequest == null) throw new TCPhabException("Must provide a method");
        if(CommonUtils.isNullOrEmpty(scheme)) throw new TCPhabException("Must provide a scheme");
        if(CommonUtils.isNullOrEmpty(host)) throw new TCPhabException("Must provide a host");
        if(port == null) this.port = 80;
        if(CommonUtils.isNullOrEmpty(path)) throw new TCPhabException("Must provide a path");

        this.uri = new URI(
                this.scheme,
                null,
                this.host,
                this.port,
                this.path,
                paramBuilder(),
                null
        );

        if(httpRequest instanceof HttpGet) httpRequest.setURI(this.uri);
        if(httpRequest instanceof HttpPost) {
            httpRequest.setURI(this.uri);
            if(this.body != null) ((HttpPost) httpRequest).setEntity(this.body);
        }

        return httpRequest;
    }

    private String paramBuilder(){
        StringBuilder stringBuilder = new StringBuilder();
        if(this.params.isEmpty()) return null;
        else {
            for(int i = 0; i < this.params.size() ; i++){
                KeyValue param = this.params.get(i);
                if(i == this.params.size() - 1) stringBuilder.append(String.format("%s=%s", param.getKey(), param.getValue()));
                else stringBuilder.append(String.format("%s=%s&", param.getKey(), param.getValue()));
            }
            return stringBuilder.toString();
        }
    }
}
