package com.example.trx.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class AJPConfig {

    @Value("${tomcat.ajp.port}")
    private int port;
    @Value("${tomcat.ajp.protocol}")
    private String protocol;
    @Value("${tomcat.ajp.enabled}")
    private boolean enabled;


    @Bean
    public ServletWebServerFactory serverFactory(){
        TomcatServletWebServerFactory tomcat=new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createAjpConnector());
        return tomcat;
    }

    private Connector createAjpConnector(){
        Connector connector=new Connector(protocol);
        connector.setPort(port);
        connector.setSecure(false);
        connector.setAllowTrace(false);
        connector.setScheme("http");
        ((AbstractAjpProtocol)connector.getProtocolHandler()).setSecretRequired(false);
        return connector;
    }

}
