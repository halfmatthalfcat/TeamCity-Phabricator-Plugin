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
import java.util.HashMap;
import java.util.Map;

public class PhabricatorPlugin extends BuildFeature {
    public static final String FEATURE_TYPE = "phabricator";

    private final String myEditUrl;

    public PhabricatorPlugin(
            @NotNull final PluginDescriptor pluginDescriptor,
            @NotNull final WebControllerManager webControllerManager
    ){
        final String jsp = pluginDescriptor.getPluginResourcesPath("tcPhabSettings.jsp");
        final String html = pluginDescriptor.getPluginResourcesPath("tcPhabSettings.html");

        webControllerManager.registerController(html, new BaseController() {
            @Override
            protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
                System.out.println("jsp: " + jsp);
                System.out.println("html: " + html);
                ModelAndView mv = new ModelAndView(jsp);
                mv.getModel().put("requestUrl", html);
                mv.getModel().put("buildTypeId", getBuildTypeIdParameter(httpServletRequest));
                return mv;
            }
        });

        this.myEditUrl = html;
    }

    private String getBuildTypeIdParameter(final HttpServletRequest request) {
        return request.getParameter("id");
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

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed() {
        return false;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull final Map<String, String> params) {
        String url = "Not Set";

        for(String key : params.keySet()){
            if(key.equals("tcphab.phabricatorUrl")){
                url = params.get(key);
            }
        }

        return String.format("Phabricator URL: %s", url);
    }

    @Override
    public Map<String, String> getDefaultParameters() {
        return new HashMap<>();
    }
}