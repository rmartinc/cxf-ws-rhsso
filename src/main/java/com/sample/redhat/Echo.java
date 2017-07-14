/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sample.redhat;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import org.jboss.ejb3.annotation.SecurityDomain;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import org.apache.cxf.interceptor.InInterceptors;
import org.jboss.ws.api.annotation.EndpointConfig;

/**
 * 
 * Simple stateless EJB web service.
 * http://www.superglobals.net/java-ee-soap-example/
 * 
 * WS-Security:
 * https://github.com/rareddy/ws-security-examples/tree/master/jaxws-usernametoken
 * https://docs.jboss.org/author/display/WFLY10/WS-Security
 * 
 * @author rmartinc
 */
@Stateless
@WebService(name = "echo", 
        targetNamespace = "http://com.sample.redhat/ws", 
        serviceName = "echo-service",
        wsdlLocation = "WEB-INF/wsdl/echo-security.wsdl")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@SecurityDomain("JBossWS")
@DeclareRoles("static")
@EndpointConfig(configFile = "WEB-INF/jaxws-endpoint-config.xml", configName = "Custom WS-Security Endpoint")
@InInterceptors(interceptors = {
    "org.jboss.wsf.stack.cxf.security.authentication.SubjectCreatingPolicyInterceptor"
})
public class Echo {

    @Resource SessionContext ctx;

    @WebMethod
    @RolesAllowed("static") // restrict access to static group
    //@PermitAll // permit the WS to everybody
    public String echo(String input) {
        System.err.println("RICKY: principal: " + ctx.getCallerPrincipal());
        System.err.println("RICKY: principal: " + ctx.getCallerPrincipal().getClass());
        System.err.println("RICKY: isCallerInRole: " + ctx.isCallerInRole("static"));
        return ctx.getCallerPrincipal() + " -> " +  input;
    }
}
