package com.couchmate.teamcity.phabricator;

import jetbrains.buildServer.log.Loggers;

import java.io.File;
import java.util.Map;
import static com.couchmate.teamcity.phabricator.CommonUtils.isNullOrEmpty;

/**
 * Created by mjo20 on 10/15/2015.
 */
public final class AppConfig {

    private Map<String, String> params;

    private PhabLogger logger;

    private String phabricatorUrl;
    private String conduitToken;
    private String conduitApiKey;
    private String diffId;
    private String harbormasterTargetPHID;
    private String workingDir;
    private String revisionId;
    private Boolean enabled = false;

    public AppConfig(){
        this.logger = new PhabLogger();
    }

    //CONFIG VARS
    private final String PHAB_URL = "tcphab.phabricatorUrl";
    private final String CONDUIT_TOKEN = "tcphab.conduitToken";
    private final String CONDUIT_API = "tcphab.conduitApiKey";
    private final String DIFF_ID = "diffId";
    private final String ENV_DIFF_ID = "env.diffId";
    private final String HARBORMASTER_PHID = "harbormasterTargetPHID";
    private final String ENV_HARBORMASTER_PHID = "env.harbormasterTargetPHID";
    private final String REVISION_ID = "revisionId";
    private final String ENV_REVISION_ID = "env.revisionId";

    public void parse(){
        logger.info(String.format("Parsing %s parameters", this.params.size()));
        for(String value : this.params.keySet()){
            if(!isNullOrEmpty(value)){
                switch(value){
                    case PHAB_URL:
                        logger.info(String.format("Found phabricatorUrl: %s", params.get(PHAB_URL)));
                        this.phabricatorUrl = params.get(PHAB_URL);
                        break;
                    case CONDUIT_TOKEN:
                        logger.info(String.format("Found conduitToken: %s", params.get(CONDUIT_TOKEN)));
                        this.conduitToken = params.get(CONDUIT_TOKEN);
                        break;
                    case CONDUIT_API:
                        logger.info(String.format("Found conduitApiKey: %s", params.get(CONDUIT_API)));
                        this.conduitApiKey = params.get(CONDUIT_API);
                        break;
                    case ENV_DIFF_ID:
                        logger.info(String.format("Found diffId: %s", params.get(ENV_DIFF_ID)));
                        this.diffId = params.get(ENV_DIFF_ID);
                        break;
                    case DIFF_ID:
                        logger.info(String.format("Found diffId: %s", params.get(DIFF_ID)));
                        this.diffId = params.get(DIFF_ID);
                        break;
                    case ENV_HARBORMASTER_PHID:
                        logger.info(String.format("Found harbormasterTargetPHID: %s", params.get(ENV_HARBORMASTER_PHID)));
                        this.harbormasterTargetPHID = params.get(ENV_HARBORMASTER_PHID);
                        break;
                    case HARBORMASTER_PHID:
                        logger.info(String.format("Found harbormasterTargetPHID: %s", params.get(HARBORMASTER_PHID)));
                        this.harbormasterTargetPHID = params.get(HARBORMASTER_PHID);
                        break;
                    case ENV_REVISION_ID:
                        logger.info(String.format("Found revisionId: %s", params.get(ENV_REVISION_ID)));
                        this.revisionId = params.get(ENV_REVISION_ID);
                        break;
                    case REVISION_ID:
                        logger.info(String.format("Found revisionId: %s", params.get(REVISION_ID)));
                        this.revisionId = params.get(REVISION_ID);
                        break;
                }
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

    public void setParams(Map<String, String> params){
        this.params = params;
    }

    public void setLogger(PhabLogger logger){
        this.logger = logger;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getConduitApiKey() { return this.conduitApiKey; }

    public String getHarbormasterTargetPHID() {
        return this.harbormasterTargetPHID;
    }

    public String getPhabricatorUrl() {
        return this.phabricatorUrl;
    }

    public String getConduitToken() {
        return this.conduitToken;
    }

    public String getDiffId() {
        return this.diffId;
    }

    public String getRevisionId() { return this.revisionId; }

    public Boolean isEnabled() {
        return this.enabled;
    }
}
