<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="handlePresent" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="requestUrl" type="java.lang.String" scope="request"/>
<jsp:useBean id="buildTypeId" type="java.lang.String" scope="request"/>

<c:set var="phabricatorUrl" value="${propertiesBean.properties['tcphab.phabricatorUrl']}" />
<c:set var="conduitToken" value="${propertiesBean.properties['tcphab.conduitToken']}" />
<c:set var="conduitApiKey" value="${propertiesBean.properties['tcphab.conduitApiKey']}" />

<table>
    <tr><td colspan="2">Report build status in real-time to your Phabricator instance.</td></tr>
    <tr><th>Phabricator URL:</th><td><props:textProperty name="tcphab.phabricatorUrl"/></td></tr>
    <tr><th>Conduit Token:</th><td><props:textProperty name="tcphab.conduitToken"/></td></tr>
    <tr><th>Conduit API Key:</th><td><props:textProperty name="tcphab.conduitApiKey"/></td></tr>
</table>