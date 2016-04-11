package com.googlecode.jcasockets;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by atemnov on 11.04.2016.
 */
public class ManagedConnectionFactory implements javax.resource.spi.ManagedConnectionFactory {

    private PrintWriter logwriter;

    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager) throws ResourceException {
        return new ManagedConnectionFactoryImpl(this, connectionManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new ConnectionFactoryImpl();
    }

    @Override
    public javax.resource.spi.ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
        return new ManagedConnection();
    }

    @Override
    public javax.resource.spi.ManagedConnection matchManagedConnections(Set set, Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
        javax.resource.spi.ManagedConnection result = null;
        Iterator it = set.iterator();
        while (result == null && it.hasNext()) {
            javax.resource.spi.ManagedConnection mc = (javax.resource.spi.ManagedConnection) it.next();
            if (mc instanceof ManagedConnection) {
                result = mc;
            }
        }
        return result;
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws ResourceException {
        this.logwriter = printWriter;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return this.logwriter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManagedConnectionFactory that = (ManagedConnectionFactory) o;

        return logwriter != null ? logwriter.equals(that.logwriter) : that.logwriter == null;

    }

    @Override
    public int hashCode() {
        return logwriter != null ? logwriter.hashCode() : 0;
    }
}
