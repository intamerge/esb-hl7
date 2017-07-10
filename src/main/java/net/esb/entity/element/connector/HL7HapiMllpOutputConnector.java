/**
 * Copyright (c) 2016-2017 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package net.esb.entity.element.connector;

import static net.esb.entity.element.common.ElementHL7Constants.*;
import static net.esb.entity.element.common.ElementNetworkConstants.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.model.Message;
import net.esb.context.IMapElementContext;
import net.esb.entity.common.ElementStartException;
import net.esb.entity.common.ElementStopException;
import net.esb.entity.common.EntityStartException;
import net.esb.entity.common.EntityStopException;
import net.esb.entity.element.common.ElementHL7Constants;
import net.esb.entity.element.common.HL7Utils;
import net.esb.message.IMapMessage;

/**
 * Class initializes and starts the echo server, based on Grizzly 2.3
 */

@Component
@Scope(value = "prototype")
public class HL7HapiMllpOutputConnector extends AbstractMapOutputConnector<HL7HapiMllpOutputConnectorDefinition, HL7HapiMllpOutputConnector> {
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_OUTPUT_TYPE) 
    public ElementHL7Constants.OUTPUT_TYPE output = ElementHL7Constants.OUTPUT_TYPE.PIPEHAT;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_HOST) 
    public String host = "0.0.0.0";
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_DISABLEVALIDATION)
	public String disableValidation = "" + Boolean.TRUE;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_PORT) 
    public String port = "" + -1;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_TLS) 
	public String tls = "" + Boolean.FALSE;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_TLS_TRUSTSTORE) 
	public String truststore;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_TLS_KEYSTORE) 
	public String keystore;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_TLS_TRUSTSTORE_PASSWORD) 
	public String truststorePassword;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_TLS_KEYSTORE_PASSWORD) 
	public String keystorePassword;
	
	@Override
	public boolean isSynchronous() {
		return true;
	}

	@JsonIgnore
	transient Connection connection = null;
	
	@JsonIgnore
	transient HapiContext hapiContext = null;
	
	@JsonIgnore
	// used to detect restart
	boolean startedBefore = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void sinkAsynchronousOutputMapRequest(IMapMessage message, IMapElementContext context){}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IMapMessage sinkSynchronousOutputMapRequest(IMapMessage requestMessage, IMapElementContext context) throws Exception{
		 connect(); // apparently you can call this multiple times
		 
		 // The initiator is used to transmit unsolicited messages
		 Initiator initiator = connection.getInitiator();
		 Message payload = HL7Utils.hapiMessageFromEsbMessage(hapiContext, requestMessage);
		 Message hapiResponseMessage = initiator.sendAndReceive(payload);
		 
		 IMapMessage responseMessage = HL7Utils.esbMessageFromHapiMessage(output, getMap(), hapiContext, hapiResponseMessage);
		 
		 getMap().transferProperties(requestMessage, responseMessage);

		 return responseMessage;
	}
	
	protected void connect() throws Exception{
		
		String _host = getMap().<String>parseProperty(String.class, getHost());
		Integer _port = getMap().<Integer>parseProperty(Integer.class, getPort());
		Boolean _tls = getMap().<Boolean>parseProperty(Boolean.class, getTls());
		
		if(null==connection){
			// Note that connections are pooled by the HapiContext by default. 
			// If multiple concurrent connections to the same server are required, 
			// the easiest way to accomplish this is currently to create multiple HapiContext instances.
			//

			connection = hapiContext.newClient(_host, _port, _tls);
		}
	}

	public String getHost() {
		return host;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setPort(String port) {
		this.port = port;
	}
	
    public void start() throws EntityStartException{
    	
    	if(getStarted()){return;}
    	
		try {
			if(startedBefore){
				// here we can put a restart delay if needed
			}
			hapiContext = new DefaultHapiContext();
			Boolean _disableValidation = getMap().<Boolean>parseProperty(Boolean.class, getDisableValidation());
			if(_disableValidation){HL7Utils.disableValidation(hapiContext);}
			connect();
			super.start();
			startedBefore = true;
		} catch (Exception e) {
		    throw new ElementStartException(e);
		}
		
 
    }
    
    public void stop() throws EntityStopException{

    	if(!getStarted()){return;}
    	
    	
		try {
			connection.close();
			connection = null;
			super.stop();
		} catch (Exception e) {
		    throw new ElementStopException(e);
		}
        

    }


	public String getTls() {
		return tls;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setTls(String tls) {
		this.tls = tls;
	}

	public String getTruststore() {
		return truststore;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setTruststore(String truststore) {
		this.truststore = truststore;
	}

	public String getKeystore() {
		return keystore;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}

	public String getTruststorePassword() {
		return truststorePassword;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public ElementHL7Constants.OUTPUT_TYPE getOutput() {
		return output;
	}
	
	@net.esb.entity.common.ElementPropertySetter
	public void setOutput(ElementHL7Constants.OUTPUT_TYPE output) {
		this.output = output;
	}

	public String getDisableValidation() {
		return disableValidation;
	}
	
	@net.esb.entity.common.ElementPropertySetter
	public void setDisableValidation(String disableValidation) {
		this.disableValidation = disableValidation;
	}



}

