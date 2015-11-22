package com.couchmate.teamcity.phabricator.conduit;

import com.couchmate.teamcity.phabricator.HttpRequestBuilder;
import com.couchmate.teamcity.phabricator.TCPhabException;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.*;

import java.io.IOException;
import java.net.URISyntaxException;

public final class ConduitClient {

    private final String conduitURL;
    private final String apiKey;
    private final String CONDUIT_PATH = "/api";
    private Gson gson;

    private ConduitClient(){
        this.conduitURL = null;
        this.apiKey = null;
    }

    public ConduitClient(
            final String conduitURL,
            final String apiKey
    ){
        this.conduitURL = conduitURL;
        this.apiKey = apiKey;
        this.gson = new Gson();
    }

    public Result ping() {
        final String PING_PATH = "/conduit.ping";

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try(CloseableHttpResponse response =
                httpClient.execute(
                        new HttpRequestBuilder()
                                .get()
                                .setHost(conduitURL)
                                .setPath(CONDUIT_PATH + PING_PATH)
                                .setBody(gson.toJson(
                                        new MessageBase(this.apiKey)
                                ))
                                .build()
                )
            ){
                return handleResponse(response);
            }
        } catch (TCPhabException | URISyntaxException | IOException e) { return null; }
    }

    public Result submitDifferentialComment(String diffId, String comment){
        final String DIFF_COMMENT_PATH = "/differential.createcomment";

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            try(CloseableHttpResponse response =
                httpClient.execute(
                        new HttpRequestBuilder()
                            .post()
                            .setHost(this.conduitURL)
                            .setPath(DIFF_COMMENT_PATH)
                            .setBody(
                                    gson.toJson(
                                            new DifferentialComment(
                                                    this.apiKey,
                                                    diffId,
                                                    comment
                                            )
                                    )
                            )
                            .build()
                )
            ){
                return handleResponse(response);
            }
        } catch (TCPhabException | URISyntaxException | IOException e) { return null; }

    }

    public Result submitHarbormasterMessage(HarbormasterMessage harbormasterMessage){
        final String HARBORMASTER_MESSAGE = "/harbormaster.sendmessage";

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            try(CloseableHttpResponse response =
                httpClient.execute(
                        new HttpRequestBuilder()
                            .post()
                            .setHost(this.conduitURL)
                            .setPath(HARBORMASTER_MESSAGE)
                            .setBody(gson.toJson(
                                    harbormasterMessage
                            )).build()
                )
            ){
                return handleResponse(response);
            }
        } catch (TCPhabException | URISyntaxException | IOException e){
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
