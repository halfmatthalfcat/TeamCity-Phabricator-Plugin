package com.couchmate.teamcity.phabricator.conduit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjo20 on 10/14/2015.
 */
public final class HarbormasterMessage extends MessageBase {

    public enum HarbormasterMessageType {
        PASS,
        FAIL,
        WORK
    }

    public enum HarbormasterUnitResultType{
        PASS,
        FAIL,
        SKIP,
        BROKEN,
        UNSOUND
    }

    private HarbormasterMessage(){
        super(null);
        this.buildPHID = null;
        this.messageType = null;
    }

    private HarbormasterMessage(
            final String apiKey,
            final String buildPHID,
            final HarbormasterMessageType messageType,
            final List<HarbormasterUnitReport> unitReports
    ){
        super(apiKey);
        this.buildPHID = buildPHID;
        this.messageType = convertMessageType(messageType);
        this.unitReports = unitReports;
    }

    @SerializedName("buildTargetPHID")
    private final String buildPHID;
    @SerializedName("type")
    private final String messageType;
    @SerializedName("unit")
    private List<HarbormasterUnitReport> unitReports;
    @SerializedName("lint")
    private List<HarbormasterLintReport> lintReports;

    private String convertMessageType(HarbormasterMessageType messageType){
        switch (messageType){
            case PASS:
                return "pass";
            case FAIL:
                return "fail";
            case WORK:
                return "work";
            default:
                return null;
        }
    }

    public class HarbormasterUnitReport {

        @SerializedName("name")
        private final String testName;
        @SerializedName("result")
        private final String resultType;
        @SerializedName("namespace")
        private final String testNamespace;
        @SerializedName("runner")
        private final String testRunner;
        @SerializedName("duration")
        private final int testDuration;

        private HarbormasterUnitReport() {
            this.testName = null;
            this.resultType = null;
            this.testNamespace = null;
            this.testRunner = null;
            this.testDuration = 0;
        }

        public HarbormasterUnitReport(
                final String testName,
                final HarbormasterUnitResultType resultType,
                final String testNamespace,
                final String testRunner,
                final int testDuration
        ){
            this.testName = testName;
            this.resultType = convertUnitType(resultType);
            this.testNamespace = testNamespace;
            this.testRunner = testRunner;
            this.testDuration = testDuration;
        }

        private String convertUnitType(HarbormasterUnitResultType resultType){
            switch (resultType){
                case PASS:
                    return "pass";
                case FAIL:
                    return "fail";
                case SKIP:
                    return "skip";
                case BROKEN:
                    return "broken";
                case UNSOUND:
                    return "unsound";
                default:
                    return null;
            }
        }

    }

    public class HarbormasterLintReport {
        //TODO
        private HarbormasterLintReport(){}
    }

}
