package com.couchmate.teamcity;

import jetbrains.buildServer.serverSide.BuildFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhabricatorBuildFeature extends BuildFeature {

    @NotNull
    @Override
    public String getType() {
        return null;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return null;
    }
}