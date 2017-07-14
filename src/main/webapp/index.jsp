<%-- 
    Document   : index.jsp
    Created on : May 17, 2017, 10:24:29 AM
    Author     : rmartinc
--%>

<%@page import="javax.ws.rs.client.Entity"%>
<%@page import="javax.ws.rs.core.MediaType"%>
<%@page import="javax.ws.rs.core.Form"%>
<%@page import="javax.ws.rs.client.ClientBuilder"%>
<%@page import="org.keycloak.KeycloakSecurityContext"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.security.acl.Group"%>
<%@page import="java.security.Principal"%>
<%@page import="java.util.Set"%>
<%@page import="javax.security.jacc.PolicyContext"%>
<%@page import="javax.security.auth.Subject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sample RH-SSO</title>
    </head>
    <body>
        <h1>Sample RH-SSO</h1>
        
        <h2>JavaEE principals</h2>
        
        <p>remote user: <%= request.getRemoteUser() %></p>
        <p>remote principal: <%= request.getUserPrincipal()%></p>
        <%
            Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        %>
        <p>javax.security.auth.Subject.container <%= subject %></p>
        <%
            if (subject != null) {
                Set<Principal> principals = subject.getPrincipals();
                for (Principal p: principals) {
                    %>
                    <p>Principal: <%= p %></p>
                    <%
                    if (p instanceof Group) {
                        Enumeration children = ((Group)p).members();
                        while (children.hasMoreElements()) {
                            Principal child = (Principal) children.nextElement();
                            %>
                            <p>Group: <%= child %></p>
                            <%
                        }
                    }
                }
            }
        %>
        <p>IsUserInRole("static"): <%= request.isUserInRole("static") %></p>
        
        <h2>Keycloak Token Info</h2>
        
        <%
            KeycloakSecurityContext keycloakSession = (KeycloakSecurityContext)
                    request.getAttribute(KeycloakSecurityContext.class.getName());
        %>
        <% if (keycloakSession != null) { %>
            <p>Token: <%= keycloakSession.getTokenString()  %></p>
            <p>Token Preferrer Username: <%= keycloakSession.getToken().getPreferredUsername() %></p>
            <p>Token Issuer: <%= keycloakSession.getToken().getIssuer() %></p>
            <p>Token Name: <%= keycloakSession.getToken().getName() %></p>
            <p>Token e-mail: <%= keycloakSession.getToken().getEmail()  %></p>
        
            <!--
            <h2>UserInfo endpoint</h2>
            
            <%= ClientBuilder.newBuilder()
                    .build()
                    .target("http://rhsso.sample.com:8080/")
                    .path("auth/realms")
                    .path(keycloakSession.getRealm())
                    .path("protocol/openid-connect/userinfo")
                    .request(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(new Form().param("access_token", keycloakSession.getTokenString())))
                    .readEntity(String.class)
            %>
            -->
        <% } %>
        
    </body>
</html>