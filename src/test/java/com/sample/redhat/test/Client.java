/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sample.redhat.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import redhat.sample.com.ws.Echo;
import javax.xml.ws.BindingProvider;
import redhat.sample.com.ws.EchoService;
import org.apache.cxf.ws.security.SecurityConstants;

/**
 * Sample client that connects to RH-SSO and then calls a SOAP endpoint using
 * the token. The token is just sent as a bearer authentication header.
 *
 * In the environment 8080 is the rh-sso server and 8081 is the WS application
 * protected by keycloak.
 *
 * @author rmartinc
 */
public class Client {

    public static void main(String... args) throws Exception {
        // get the bearer token from another application that provides login
        // if the application is a bearer-only the user should login to another application
        Response res = ClientBuilder.newBuilder()
                    .build()
                    .target("http://rhsso.sample.com:8080/auth/realms/master/protocol/openid-connect/token")
                    .request(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(new Form()
                            .param("username", "ricky")
                            .param("password", "Kiosko_03")
                            .param("grant_type", "password")
                            .param("client_id", "cxf-rhsso")));
        if (res.getStatus() != 200) {
            throw new IllegalStateException("Invalid login: " + res.getStatusInfo());
        }
        // parse the JSON token manually using jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readValue(res.readEntity(String.class), JsonNode.class);
        //System.err.println(node);
        String token = node.get("access_token").asText();
        String refresh_token = node.get("refresh_token").asText();
        
        // do a refresh, keycloack give tokens only for certain time so
        // it should have to be refreshed if the access token is expired
        // the expiration is defined in the response: "expires_in" (acces_token, short time)
        // and in "refresh_expires_in" (refresh_token, longer time)
        res = ClientBuilder.newBuilder()
                    .build()
                    .target("http://rhsso.sample.com:8080/auth/realms/master/protocol/openid-connect/token")
                    .request(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(new Form()
                            .param("refresh_token", refresh_token)
                            .param("grant_type", "refresh_token")
                            .param("client_id", "cxf-rhsso")));
        if (res.getStatus() != 200) {
            throw new IllegalStateException("Invalid refresh: " + res.getStatusInfo());
        }
        // parse the JSON token manually using jackson
        node = objectMapper.readValue(res.readEntity(String.class), JsonNode.class);
        //System.err.println(node);
        token = node.get("access_token").asText();
        refresh_token = node.get("refresh_token").asText();
         
        // construct the jax-ws client
        // the WSDL is added to jax-ws-catalog.xml to avoid accesing to protected WSDL endpoint
        URL url = new URL("http://rhsso.sample.com:8081/cxf-ws-rhsso/echo-service/echo?wsdl");
        EchoService service = new EchoService(url);
        Echo echo = service.getEchoPort();
        // use WSS security to add a token to the SOAP message
        ((BindingProvider)echo).getRequestContext().put(SecurityConstants.USERNAME, "ricky");
        ((BindingProvider)echo).getRequestContext().put(SecurityConstants.PASSWORD, token);
        
        System.err.println(echo.echo(args.length > 0 ? args[0] : "empty..."));
    }
}
