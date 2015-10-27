package com.couchmate.teamcity.models;

/**
 * Created by mjo20 on 10/13/2015.
 */
public class DifferentialComment {

    public String diffId;
    public String comment;

    public DifferentialComment(
            String diffId,
            String comment
    ){
        this.diffId = diffId;
        this.comment = comment;
    }

}
