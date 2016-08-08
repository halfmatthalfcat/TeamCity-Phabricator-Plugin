package com.couchmate.teamcity.phabricator;

import jetbrains.buildServer.log.Loggers;

import java.io.*;
import java.net.*;
import java.util.Map;
import static com.couchmate.teamcity.phabricator.CommonUtils.isNullOrEmpty;

public final class AppConfig {

    private Map<String, String> params;

    private PhabLogger logger;

    private String phabricatorUrl;
    private String phabricatorProtocol;
    private String conduitToken;
    private String codeLocation;
    private String arcPath;
    private String diffId;
    private String revisionId;
    private String harbormasterTargetPHID;
    private String workingDir;
    private String errormsg;
    private Boolean enabled = false;

    public AppConfig(){
        this.logger = new PhabLogger();
    }

    //CONFIG VARS
    private final String PHAB_URL = "tcphab.phabricatorUrl";
    private final String CONDUIT_TOKEN = "tcphab.conduitToken";
    private final String CODE_LOCATION = "tcphab.pathToCode";
    private final String ARC_PATH = "tcphab.pathToArc";
    private final String ERROR_MSG = "tcphab.errorInfo";
    private final String DIFF_ID = "diffId";
    private final String ENV_DIFF_ID = "env.diffId";
    private final String HARBORMASTER_PHID = "harbormasterTargetPHID";
    private final String ENV_HARBORMASTER_PHID = "env.harbormasterTargetPHID";
    //TODO used for commenting on diffs eventually
    private final String REVISION_ID = "revisionId";
    private final String ENV_REVISION_ID = "env.revisionId";

    public void parse(){
        enabled = false;
        logger.info(params);
        for(String value : this.params.keySet()){
            if(!isNullOrEmpty(value)){
                switch(value){
                    case PHAB_URL:
                        logger.info(String.format("Found phabricatorUrl: %s", params.get(PHAB_URL)));
                        try {
                          URL aURL = new URL(params.get(PHAB_URL));
                          this.phabricatorProtocol = aURL.getProtocol();
                          this.phabricatorUrl = aURL.getHost();
                        } catch (IOException e) {
                           logger.info(String.format("phabricator url could not be parsed: %s", e.getStackTrace()[0].toString()));
                        }
                        break;
                    case CONDUIT_TOKEN:
                        logger.info(String.format("Found conduitToken: %s", params.get(CONDUIT_TOKEN)));
                        this.conduitToken = params.get(CONDUIT_TOKEN);
                        break;
                    case CODE_LOCATION: 
                        logger.info(String.format("Found Code Location: %s", params.get(CODE_LOCATION)));
                        this.codeLocation = params.get(CODE_LOCATION);
                        break;
                    case ARC_PATH:
                        logger.info(String.format("Found arcPath: %s", params.get(ARC_PATH)));
                        this.arcPath = params.get(ARC_PATH);
                    case ENV_DIFF_ID:
                        logger.info(String.format("Found env diffId: %s", params.get(ENV_DIFF_ID)));
                        this.diffId = params.get(ENV_DIFF_ID);
                        break;
                    case REVISION_ID:
                        logger.info(String.format("Found revisionId: %s", params.get(REVISION_ID)));
                        this.revisionId = params.get(REVISION_ID);
                        break;
                    case DIFF_ID:
                        logger.info(String.format("Found diffId: %s", params.get(DIFF_ID)));
                        this.diffId = params.get(DIFF_ID);
                        break;
                    case ERROR_MSG:
                        logger.info(String.format("Found Error Msg: %s", params.get(ERROR_MSG)));
                        this.errormsg = params.get(ERROR_MSG);
                        break;
                    case ENV_HARBORMASTER_PHID:
                        logger.info(String.format("Found harbormasterTargetPHID: %s", params.get(ENV_HARBORMASTER_PHID)));
                        this.harbormasterTargetPHID = params.get(ENV_HARBORMASTER_PHID);
                        break;
                    case HARBORMASTER_PHID:
                        logger.info(String.format("Found harbormasterTargetPHID: %s", params.get(HARBORMASTER_PHID)));
                        this.harbormasterTargetPHID = params.get(HARBORMASTER_PHID);
                        break;
                }
            }
        }
        if(
                !isNullOrEmpty(conduitToken) &&
                !isNullOrEmpty(arcPath) &&
                !isNullOrEmpty(phabricatorUrl) &&
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
        if(!isNullOrEmpty(this.codeLocation)) {
            this.workingDir = workingDir + "/" + this.codeLocation;
        } else {
            this.workingDir = workingDir;
        }
    }

    public String getHarbormasterTargetPHID() {
        return this.harbormasterTargetPHID;
    }

    public String getPhabricatorProtocol() {
        return this.phabricatorProtocol;
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

    public String getErrorMsg() {
        return this.errormsg;
    }

    public String getRevisionId() {
        return this.revisionId;
    }

    public String getArcPath() { return this.arcPath; }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
