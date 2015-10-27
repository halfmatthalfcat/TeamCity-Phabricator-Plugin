package com.couchmate.teamcity.conduit;

import com.couchmate.teamcity.TCPhabException;
import com.couchmate.teamcity.models.DifferentialComment;
import com.couchmate.teamcity.models.Result;
import com.couchmate.teamcity.utils.HttpRequestBuilder;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.*;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by mjo20 on 10/10/2015.
 */
public class ConduitClient {

    private final String conduitURL;
    private final String conduitToken;
    private final String CONDUIT_PATH = "/api";
    private Gson gson;

    private ConduitClient(){
        this.conduitURL = null;
        this.conduitToken = null;
    }

    private ConduitClient(
            final String conduitURL,
            final String conduitToken
    ){
        this.conduitURL = conduitURL;
        this.conduitToken = conduitToken;
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
                            .setHost(conduitURL)
                            .setPath(DIFF_COMMENT_PATH)
                            .setBody(
                                    gson.toJson(
                                            new DifferentialComment(
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
