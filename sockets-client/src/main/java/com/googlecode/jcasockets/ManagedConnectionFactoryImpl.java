package com.googlecode.jcasockets;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

/**
 * Created by atemnov on 11.04.2016.
 */
public class ManagedConnectionFactoryImpl implements ConnectionFactory {

    private ManagedConnectionFactory factory;
    private ConnectionManager manager;

    public ManagedConnectionFactoryImpl(ManagedConnectionFactory factory, ConnectionManager manager) {
        super();
        this.factory = factory;
        this.manager = manager;
    }

    @Override
    public Connection createCollection() {
        try {
            return (Connection) manager.allocateConnection(factory, null);
        } catch (ResourceException e) {
            return null;
        }
    }
}
