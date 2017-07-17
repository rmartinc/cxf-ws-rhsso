package com.sample.redhat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.BindingProvider;
import org.apache.cxf.rt.security.SecurityConstants;
import org.keycloak.KeycloakSecurityContext;
import redhat.sample.com.ws.Echo;
import redhat.sample.com.ws.EchoService;

/**
 * Common use of the keycloak behind an application. The access token is got 
 * from the keycloak session and the call to the WS is done using the access
 * token. The servlet is calling the WS inside this same machine using
 * standard WSS parameters.
 * 
 * @author rmartinc
 */
@WebServlet(name = "EchoServlet", urlPatterns = {"/EchoServlet"})
public class EchoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // recover the keycloak session
            KeycloakSecurityContext keycloakSession = (KeycloakSecurityContext)
                    request.getAttribute(KeycloakSecurityContext.class.getName());
            // the echo service is recovered using the "jax-ws-catalog.xml"
            // this way the request to the WSDL is avoided (the WSDL is also protected)
            URL url = new URL(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + 
                    request.getServletContext().getContextPath() + "/echo-service/echo?wsdl");
            EchoService service = new EchoService(url);
            Echo echo = service.getEchoPort();
            // use WSS security to add a token to the SOAP message
            BindingProvider bp = (BindingProvider) echo;
            System.err.println(((BindingProvider) echo).getBinding().getClass());
            ((BindingProvider) echo).getRequestContext().put(SecurityConstants.USERNAME, keycloakSession.getToken().getId());
            ((BindingProvider) echo).getRequestContext().put(SecurityConstants.PASSWORD, keycloakSession.getTokenString());
            // call the WS eith the header
            out.println(echo.echo(request.getParameter("input") == null? "nothing...":request.getParameter("input")));
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Echo Servlet";
    }

}
