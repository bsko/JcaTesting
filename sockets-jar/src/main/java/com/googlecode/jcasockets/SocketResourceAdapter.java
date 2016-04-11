/*
 * Copyright 2009 Mark Jeffrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jcasockets;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

public class SocketResourceAdapter implements ResourceAdapter, SocketResourceAdapterConfiguration{
	private static ConcurrentMap<ActivationSpec, SocketListener> socketListeners = new ConcurrentHashMap<ActivationSpec, SocketListener>();

	private WorkManager workManager;
	private final Logger logger = Logger.getLogger(SocketResourceAdapter.class.getName());
	private String defaultEncoding;
	private int defaultMaximumConnections;
	private int defaultConnectionTimeoutMilliseconds;

	private String defaultIpAddress;
	

	public String getDefaultIpAddress() {
		return defaultIpAddress;
	}

	public SocketResourceAdapter() {
	}

	public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
		logger.info("start");
    	workManager = ctx.getWorkManager();
		for (SocketListener socketListener: socketListeners.values()) {
			socketListener.start();
		}
	}

	public void stop() {
		logger.info("stop");
		for (SocketListener socketListener: socketListeners.values()) {
			socketListener.release();
		}
	}

	public void endpointActivation(MessageEndpointFactory messageEndpointFactory, ActivationSpec activationSpec)
			throws  ResourceException {
		logger.info("endpointActivation");
		if (!(activationSpec instanceof SocketActivationSpec)) {
			throw new NotSupportedException("Invalid spec, Should be a " + SocketActivationSpec.class.getName() + " was: " + activationSpec);
		}
		SocketActivationSpec socketActivationSpec = (SocketActivationSpec) activationSpec;
		createSocketActivationSpec(socketActivationSpec);
		SocketListener socketListener = new SocketListener(workManager, socketActivationSpec, messageEndpointFactory);
		addToKnownListeners(socketActivationSpec, socketListener);
		try {
			socketListener.start();
		} catch (ResourceException e) {
			socketListener.release();
			throw e;
		}
	}

	private void addToKnownListeners(SocketActivationSpec socketActivationSpec, SocketListener socketListener) throws NotSupportedException {
		SocketListener previousValue = socketListeners.putIfAbsent(socketActivationSpec, socketListener);
		if ( previousValue!= null ){
			throw new NotSupportedException( "A socket activation spec already exists with the same port: \n " 
					+ " previous: " + previousValue 
					+ " this: " + socketActivationSpec 
					);
		}
		
	}

	private void createSocketActivationSpec(SocketActivationSpec socketActivationSpec) {
		if ( socketActivationSpec.getIpAddress() == null){
			socketActivationSpec.setIpAddress(defaultIpAddress);
		}
		if ( socketActivationSpec.getEncoding() == null){
			socketActivationSpec.setEncoding(defaultEncoding);
		}
		if ( socketActivationSpec.getMaximumConnections() <= 0){
			socketActivationSpec.setMaximumConnections(defaultMaximumConnections);
		}
		if ( socketActivationSpec.getConnectionTimeoutMilliseconds() <= 0){
			socketActivationSpec.setConnectionTimeoutMilliseconds(defaultConnectionTimeoutMilliseconds);
		}
	}


	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
		stop();
		logger.info("endpointDeactivation");
		// nothing to do.
	}

	public XAResource[] getXAResources(ActivationSpec[] arg0) throws ResourceException {
		return new XAResource[0]; // XA is unsupported
	}

	public void setIpAddress(String defaultIpAddress) {
		logger.info("Default ipAddress (may be overridden when activated later) is: " + defaultIpAddress);
		this.defaultIpAddress = defaultIpAddress;
	}
	
	public void setEncoding(String defaultEncoding) {
		logger.info("Default encoding (may be overridden when activated later) is: " + defaultEncoding);
		this.defaultEncoding = defaultEncoding;
	}
	
	/* Different server behaviour. This is for Glassfish. */
	public void setMaximumConnections(int defaultMaximumConnections) {
		doSetMaximumConnections(defaultMaximumConnections);
	}
	
	/* Different server behaviour. This is for JBoss. */
	public void setMaximumConnections(Integer defaultMaximumConnections) {
		doSetMaximumConnections(defaultMaximumConnections);
	}

	/* Different server behaviour. This is for Glassfish. */
	public void setConnectionTimeoutMilliseconds(int defaultConnectionTimeoutMilliseconds) {
		doSetConnectionTimeoutMilliseconds(defaultConnectionTimeoutMilliseconds);
	}

	/* Different server behaviour. This is for JBoss. */
	public void setConnectionTimeoutMilliseconds(Integer defaultConnectionTimeoutMilliseconds) {
		setConnectionTimeoutMilliseconds(defaultConnectionTimeoutMilliseconds.intValue());
	}

	private void doSetConnectionTimeoutMilliseconds(int defaultConnectionTimeoutMilliseconds) {
		logger.info("Default connectionTimeoutMilliseconds (may be overridden when activated later) is: " + defaultConnectionTimeoutMilliseconds);
		this.defaultConnectionTimeoutMilliseconds = defaultConnectionTimeoutMilliseconds;
	}
	private void doSetMaximumConnections(Integer defaultMaximumConnections) {
		logger.info("Default maximumConnections (may be overridden when activated later) is: " + defaultMaximumConnections);
		this.defaultMaximumConnections = defaultMaximumConnections;
	}


	public String getEncoding() {
		return defaultEncoding;
	}

	public int getMaximumConnections() {
		return defaultMaximumConnections;
	}

	public int getConnectionTimeoutMilliseconds() {
		return defaultConnectionTimeoutMilliseconds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + defaultConnectionTimeoutMilliseconds;
		result = prime * result + ((defaultEncoding == null) ? 0 : defaultEncoding.hashCode());
		result = prime * result + ((defaultIpAddress == null) ? 0 : defaultIpAddress.hashCode());
		result = prime * result + defaultMaximumConnections;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
				return true;
			}
		if (obj == null) {
				return false;
			}
		if (getClass() != obj.getClass()) {
				return false;
			}
		SocketResourceAdapter other = (SocketResourceAdapter) obj;
		if (defaultConnectionTimeoutMilliseconds != other.defaultConnectionTimeoutMilliseconds) {
				return false;
			}
		if (defaultEncoding == null) {
				if (other.defaultEncoding != null) {
						return false;
					}
			} else if (!defaultEncoding.equals(other.defaultEncoding)) {
				return false;
			}
		if (defaultIpAddress == null) {
				if (other.defaultIpAddress != null) {
						return false;
					}
			} else if (!defaultIpAddress.equals(other.defaultIpAddress)) {
				return false;
			}
		if (defaultMaximumConnections != other.defaultMaximumConnections) {
				return false;
			}
		return true;
	}
}
