<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="authz" tagdir="/WEB-INF/tags/authz" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="requestUrl" type="java.lang.String" scope="request"/>
<jsp:useBean id="buildTypeId" type="java.lang.String" scope="request"/>

<c:set var="phabricatorUrl" value="${propertiesBean.properties['tcphab.phabricatorUrl']}" />
<c:set var="conduitToken" value="${propertiesBean.properties['tcphab.conduitToken']}" />
<c:set var="conduitApiKey" value="${propertiesBean.properties['tcphab.conduitApiKey']}" />

<tr><td colspan="2">Report build status in real-time to your Phabricator instance.</td></tr>
<tr><th>Phabricator URL:</th><td><props:textProperty name="tcphab.phabricatorUrl"/></td></tr>
<tr><th>Conduit Token:</th><td><props:textProperty name="tcphab.conduitToken"/></td></tr>
<tr><th>Conduit API Key:</th><td><props:textProperty name="tcphab.conduitApiKey"/></td></tr>