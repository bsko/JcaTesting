package com.googlecode.jcasockets;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by atemnov on 11.04.2016.
 */
public class ManagedConnection implements javax.resource.spi.ManagedConnection {

    private PrintWriter logWriter;
    private Connection connection;
    private List<ConnectionEventListener> listeners;

    public ManagedConnection() {
        listeners = Collections.synchronizedList(new ArrayList<ConnectionEventListener>());
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
        if (connection == null) {
            connection = new ConnectionImpl();
        }
        return connection;
    }

    @Override
    public void destroy() throws ResourceException {

    }

    @Override
    public void cleanup() throws ResourceException {

    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (connection != null && connection instanceof Connection) {
            this.connection = (Connection) connection;
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener connectionEventListener) {
        if (connectionEventListener != null) {
            listeners.add(connectionEventListener);
        }
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener connectionEventListener) {
        if (connectionEventListener != null) {
            listeners.remove(connectionEventListener);
        }
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new NotSupportedException();
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        throw new NotSupportedException();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws ResourceException {
        this.logWriter = printWriter;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return this.logWriter;
    }
}
