package com.couchmate.teamcity.phabricator;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.BuildStatisticsOptions;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.STestRun;
import com.couchmate.teamcity.phabricator.HttpClient;
import com.couchmate.teamcity.phabricator.HttpRequestBuilder;
import jetbrains.buildServer.tests.TestInfo;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildTracker implements Runnable {

    private SRunningBuild build;
    private AppConfig appConfig;
    private Map<String, STest> tests;

    public BuildTracker(SRunningBuild build){
        this.build = build;
        this.appConfig = new AppConfig();
        this.tests = new HashMap<>();
        Loggers.SERVER.info("Tracking build" + build.getBuildNumber());
    }

    public void run() {
        if(!appConfig.isEnabled()){
            try{
                Map<String, String> params = new HashMap<>();
                params.putAll(this.build.getBuildOwnParameters());
                params.putAll(this.build.getBuildFeaturesOfType("phabricator").iterator().next().getParameters());
                for(String param : params.keySet())
                    if(param != null) Loggers.AGENT.info(String.format("Found %s", param));
                this.appConfig.setParams(params);
                this.appConfig.parse();
            } catch (Exception e) { Loggers.SERVER.error("BuildTracker Param Parse", e); }
        }

        while (!build.isFinished()){
            if(!appConfig.isEnabled())
                    return;
            try {
            Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        } 
        build.getBuildStatistics(BuildStatisticsOptions.ALL_TESTS_NO_DETAILS)
                .getAllTests()
                .forEach(
                        testRun -> {
                            if(!this.tests.containsKey(testRun.getTest().getName().getAsString())) {
                                this.tests.put(testRun.getTest().getName().getAsString(),
                                        testRun.getTest());
                                sendTestReport(testRun.getTest().getName().getAsString(),
                                        testRun);
                            }
                        }
                );
         Loggers.SERVER.info(this.build.getBuildNumber() + " finished");
    }

    private CloseableHttpClient createHttpClient() {
        HttpClient client = new HttpClient(true);
        return client.getCloseableHttpClient();
    }

    private void sendTestReport(String testName, STestRun test) {
        HttpRequestBuilder httpPost = new HttpRequestBuilder()
                .post()
                .setHost(this.appConfig.getPhabricatorUrl())
                .setScheme(this.appConfig.getPhabricatorProtocol())
                .setPath("/api/harbormaster.sendmessage")
                .addFormParam(new StringKeyValue("api.token", this.appConfig.getConduitToken()))
                .addFormParam(new StringKeyValue("buildTargetPHID", this.appConfig.getHarbormasterTargetPHID()))
                .addFormParam(new StringKeyValue("type", "work"))
                .addFormParam(new StringKeyValue("unit[0][name]", test.getTest().getName().getTestMethodName()))
                .addFormParam(new StringKeyValue("unit[0][namespace]", test.getTest().getName().getClassName()));

        if(test.getStatus().isSuccessful()){
            httpPost.addFormParam(new StringKeyValue("unit[0][result]", "pass"));
        } else if (test.getStatus().isFailed()){
            httpPost.addFormParam(new StringKeyValue("unit[0][result]", "fail"));
        }
        try(CloseableHttpResponse response = createHttpClient().execute(httpPost.build())){
            Loggers.SERVER.warn(String.format("Test Response: %s\nTest Body: %s\n",
                    response.getStatusLine().getStatusCode(),
                    IOUtils.toString(response.getEntity().getContent())));
        } catch (Exception e) { Loggers.SERVER.error("Send error", e); }
    }
}
