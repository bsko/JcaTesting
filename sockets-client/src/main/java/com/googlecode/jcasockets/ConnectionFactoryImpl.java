package com.googlecode.jcasockets;

/**
 * Created by atemnov on 11.04.2016.
 */
public class ConnectionFactoryImpl implements ConnectionFactory {

    @Override
    public Connection createCollection() {
        return new ConnectionImpl();
    }
}
