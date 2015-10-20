package com.couchmate.teamcity.phabricator;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PhabricatorPlugin extends BuildFeature {
    public static final String FEATURE_TYPE = "phabricator";

    private final String myEditUrl;

    public PhabricatorPlugin(
            @NotNull final PluginDescriptor pluginDescriptor,
            @NotNull final WebControllerManager webControllerManager){
        final String jsp = pluginDescriptor.getPluginResourcesPath("tcPhabSettings.jsp");
        final String html = pluginDescriptor.getPluginResourcesPath("tcPhabSettings.html");

        webControllerManager.registerController(html, new BaseController() {
            @Nullable
            @Override
            protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
                final ModelAndView mv = new ModelAndView(jsp);
                return mv;
            }
        });

        this.myEditUrl = html;
    }

    @NotNull
    @Override
    public String getType() {
        return FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Phabricator Plugin";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return this.myEditUrl;
    }
}