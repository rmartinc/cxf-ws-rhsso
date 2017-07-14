package redhat.sample.com.ws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.1.8
 * 2017-07-13T20:05:17.552+02:00
 * Generated source version: 3.1.8
 * 
 */
@WebServiceClient(name = "echo-service", 
                  wsdlLocation = "file:/home/rmartinc/NetBeansProjects/cxf-ws-rhsso/src/main/webapp/WEB-INF/wsdl/echo-security.wsdl",
                  targetNamespace = "http://com.sample.redhat/ws") 
public class EchoService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://com.sample.redhat/ws", "echo-service");
    public final static QName EchoPort = new QName("http://com.sample.redhat/ws", "echoPort");
    static {
        URL url = null;
        try {
            url = new URL("file:/home/rmartinc/NetBeansProjects/cxf-ws-rhsso/src/main/webapp/WEB-INF/wsdl/echo-security.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(EchoService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/home/rmartinc/NetBeansProjects/cxf-ws-rhsso/src/main/webapp/WEB-INF/wsdl/echo-security.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public EchoService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public EchoService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EchoService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    public EchoService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public EchoService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public EchoService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }    




    /**
     *
     * @return
     *     returns Echo
     */
    @WebEndpoint(name = "echoPort")
    public Echo getEchoPort() {
        return super.getPort(EchoPort, Echo.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns Echo
     */
    @WebEndpoint(name = "echoPort")
    public Echo getEchoPort(WebServiceFeature... features) {
        return super.getPort(EchoPort, Echo.class, features);
    }

}
