package com.couchmate.teamcity.phabricator.tasks;

import com.couchmate.teamcity.phabricator.AppConfig;
import com.couchmate.teamcity.phabricator.HttpRequestBuilder;
import com.couchmate.teamcity.phabricator.StringKeyValue;
import com.couchmate.teamcity.phabricator.conduit.HarbormasterMessage;
import com.google.gson.Gson;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mjo20 on 10/31/2015.
 */
public class HarbormasterBuildStatus extends Task {

    private AppConfig appConfig;
    private BuildFinishedStatus buildFinishedStatus;
    private HarbormasterMessage harbormasterMessage;
    private Gson gson;
    private HttpPost httpPost = null;

    private HarbormasterBuildStatus() {
    }

    public HarbormasterBuildStatus(
            final AppConfig appConfig,
            final BuildFinishedStatus buildFinishedStatus
    ) {
        this.appConfig = appConfig;
        this.buildFinishedStatus = buildFinishedStatus;
        this.gson = new Gson();
    }

    @Override
    protected void setup() {
        URI uri;
        String scheme = "http";
        String url = this.appConfig.getPhabricatorUrl();
        String host = null;
        try {
            uri = new URI(url);
            scheme = uri.getScheme();
            host = uri.getHost();
        } catch (URISyntaxException e) {
            // Don't die here, just use default values of "http" and the url as the path
            e.printStackTrace();
        }

        try {
            this.httpPost = (HttpPost) new HttpRequestBuilder()
                    .post()
                    .setScheme(scheme)
                    .setHost(host == null ? url : host)
                    .setPath("/api/harbormaster.sendmessage")
                    .addFormParam(new StringKeyValue("api.token", this.appConfig.getConduitToken()))
                    .addFormParam(new StringKeyValue("type", parseTeamCityBuildStatus(this.buildFinishedStatus)))
                    .addFormParam(new StringKeyValue("buildTargetPHID", this.appConfig.getHarbormasterTargetPHID()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void execute() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            httpClient.execute(this.httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void teardown() {

    }

    private String parseTeamCityBuildStatus(BuildFinishedStatus buildFinishedStatus) {
        switch (buildFinishedStatus) {
            case FINISHED_SUCCESS:
                return "pass";
            case FINISHED_FAILED:
            case FINISHED_WITH_PROBLEMS:
            case INTERRUPTED:
                return "fail";
            default:
                return null;
        }
    }
}


