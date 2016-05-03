package com.couchmate.teamcity.phabricator.conduit;

import com.couchmate.teamcity.phabricator.HttpRequestBuilder;
import com.couchmate.teamcity.phabricator.TCPhabException;
import com.couchmate.teamcity.phabricator.PhabLogger;
import com.couchmate.teamcity.phabricator.StringKeyValue;
import com.google.gson.Gson;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.commons.io.IOUtils;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import org.apache.http.conn.socket.*;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.Registry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URISyntaxException;

public final class ConduitClient {

    private final String conduitURL;
    private final String conduitScheme;
    private final String apiKey;
    private final PhabLogger logger;
    private final String CONDUIT_PATH = "/api";
    private Gson gson;

    private ConduitClient(){
        this.conduitURL = null;
        this.apiKey = null;
        this.logger = null;
        this.conduitScheme = null;
    }

    public ConduitClient(
            final String conduitURL,
            final String conduitScheme,
            final String apiKey,
            final PhabLogger logger
    ){
        this.conduitURL = conduitURL;
        this.conduitScheme = conduitScheme;
        this.apiKey = apiKey;
        this.logger = logger;
        this.gson = new Gson();
    }

    public Result ping() {
        final String PING_PATH = "/conduit.ping";

        try(CloseableHttpClient httpClient = this.createHttpClient()) {
            try(CloseableHttpResponse response =
                httpClient.execute(
                        new HttpRequestBuilder()
                                .get()
                                .setHost(conduitURL)
                                .setScheme(conduitScheme)
                                .setPath(CONDUIT_PATH + PING_PATH)
                                .setBody(gson.toJson(
                                        new MessageBase(this.apiKey)
                                ))
                                .build()
                )
            ){
                return handleResponse(response);
            }
        } catch ( TCPhabException | URISyntaxException | IOException e) {
            this.logger.warn("Ping error", e);
            return null;
        }
    }

    private CloseableHttpClient createHttpClient() {
        try {
             SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
            }).build();
            SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            final Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                                                   .register("https", sslConnectionFactory)
                                                   .register("http", new PlainConnectionSocketFactory())
                                                   .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
            CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            return httpClient;
        } catch (Exception e) {
           this.logger.warn("http client error", e);
           return null;
        }
    }

    public Result submitDifferentialComment(String diffId, String comment){
        final String DIFF_COMMENT_PATH = "/api/differential.createcomment";
        try(CloseableHttpClient httpClient = this.createHttpClient()){
            try(CloseableHttpResponse response =
                httpClient.execute(
                        new HttpRequestBuilder()
                            .post()
                            .setHost(this.conduitURL)
                            .setScheme(this.conduitScheme)
                            .setPath(DIFF_COMMENT_PATH)
                            .addFormParam(new StringKeyValue("api.token", this.apiKey))
                            .addFormParam(new StringKeyValue("message", comment))
                            .addFormParam(new StringKeyValue("revision_id", diffId))
                            .addFormParam(new StringKeyValue("silent", "true"))
                            .addFormParam(new StringKeyValue("action", "none"))
                            .build()
                        )
                ){
                return handleResponse(response);
            }
        } catch ( TCPhabException | URISyntaxException | IOException e) {
            this.logger.warn("createcomment error", e);
            return null;
        }

    }

    public Result submitHarbormasterMessage(HarbormasterMessage harbormasterMessage){
        final String HARBORMASTER_MESSAGE = "/api/harbormaster.sendmessage";

        try(CloseableHttpClient httpClient = this.createHttpClient()){
            try(CloseableHttpResponse response =
                httpClient.execute(
                        new HttpRequestBuilder()
                            .post()
                            .setHost(this.conduitURL)
                            .setScheme(this.conduitScheme)
                            .setPath(HARBORMASTER_MESSAGE)
                            .setBody(gson.toJson(
                                    harbormasterMessage
                            )).build()
                )
            ){
                return handleResponse(response);
            }
        } catch ( TCPhabException | URISyntaxException | IOException e){
            this.logger.warn("harbormaster sendmessage", e);
            return null;
        }
    }

    private Result handleResponse(CloseableHttpResponse response) throws IOException{
        switch (response.getStatusLine().getStatusCode()){
            case 200:
            case 201:
                if(response.getEntity().getContentLength() > 0)
                    return gson.fromJson(IOUtils.toString(response.getEntity().getContent()), Result.class);
            default:
                return null;

        }
    }
}
