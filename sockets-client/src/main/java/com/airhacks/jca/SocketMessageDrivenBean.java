package com.airhacks.jca;

import com.googlecode.jcasockets.SocketMessage;
import com.googlecode.jcasockets.SocketMessageEndpoint;
import org.jboss.ejb3.annotation.ResourceAdapter;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

/**
 * Created by atemnov on 10.04.2016.
 */
@MessageDriven(
        name = "SocketMessageDrivenBean",
        messageListenerInterface = com.googlecode.jcasockets.SocketMessageEndpoint.class,
        activationConfig = { @ActivationConfigProperty(propertyName = "port", propertyValue = "9001") }
)
@ResourceAdapter(value = "sockets-rar-1.2.4-SNAPSHOT.rar")
public class SocketMessageDrivenBean implements SocketMessageEndpoint {

    @PostConstruct
    private void postConstruct() {
        System.out.println("--- SocketMessageDrivenBean --- created!");
    }


    @Override
    public void onMessage(SocketMessage socketMessage) throws Exception {
        System.out.println(socketMessage);
    }

}
