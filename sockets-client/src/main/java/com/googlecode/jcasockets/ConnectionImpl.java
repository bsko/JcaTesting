package com.googlecode.jcasockets;

/**
 * Created by atemnov on 11.04.2016.
 */
public class ConnectionImpl implements Connection {

    @Override
    public CustomClient connect(String hostName, int port) {
        return new CustomClient(hostName, port);
    }

}
