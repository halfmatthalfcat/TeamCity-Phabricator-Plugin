package com.couchmate.teamcity.phabricator;

import java.io.File;
import java.util.Map;
import static com.couchmate.teamcity.phabricator.CommonUtils.isNullOrEmpty;

/**
 * Created by mjo20 on 10/15/2015.
 */
public final class AppConfig {

    private Map<String, String> params;

    private String phabricatorUrl;
    private String conduitToken;
    private String conduitApiKey;
    private String diffId;
    private String harbormasterTargetPHID;
    private String workingDir;
    private Boolean enabled;

    public AppConfig(Map<String, String> params){
        this.params = params;
    }

    public void parse(){
        for(String value : params.keySet()){
            switch(value){
                case "phabricatorUrl":
                    this.phabricatorUrl = params.get("phabricatorUrl");
                    break;
                case "conduitToken":
                    this.conduitToken = params.get("conduitToken");
                    break;
                case "conduitApiKey":
                    this.conduitApiKey = params.get("conduitApiKey");
                    break;
                case "diffId":
                    this.diffId = params.get("diffId");
                    break;
                case "harbormasterTargetPHID":
                    this.harbormasterTargetPHID = params.get("harbormasterTargetPHID");
                    break;
            }
        }
        if(
                !isNullOrEmpty(conduitToken) &&
                !isNullOrEmpty(phabricatorUrl) &&
                !isNullOrEmpty(conduitApiKey) &&
                !isNullOrEmpty(diffId) &&
                !isNullOrEmpty(harbormasterTargetPHID)){
            this.enabled = true;
        }
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getHarbormasterTargetPHID() {
        return this.harbormasterTargetPHID;
    }

    public void setHarbormasterTargetPHID(String harbormasterTargetPHID) {
        this.harbormasterTargetPHID = harbormasterTargetPHID;
    }

    public String getPhabricatorUrl() {
        return this.phabricatorUrl;
    }

    public void setConduitURL(String conduitURL) {
        this.phabricatorUrl = conduitURL;
    }

    public String getConduitToken() {
        return this.conduitToken;
    }

    public void setConduitToken(String conduitToken) {
        this.conduitToken = conduitToken;
    }

    public String getDiffId() {
        return this.diffId;
    }

    public void setDiffId(String diffId) {
        this.diffId = diffId;
    }

    public Boolean isEnabled() {
        return this.enabled;
    }
}
