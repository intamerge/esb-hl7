/**
 * Copyright (c) 2016-2016 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package net.esb.entity.element.connector;

import static net.esb.entity.element.common.ElementHL7Constants.*;
import static net.esb.entity.element.common.ElementNetworkConstants.*;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import ca.uhn.hl7v2.protocol.ReceivingApplicationExceptionHandler;
import net.esb.context.IMapElementContext;
import net.esb.entity.common.ElementStopException;
import net.esb.entity.common.EntityStartException;
import net.esb.entity.common.EntityStopException;
import net.esb.entity.common.IMapElement;
import net.esb.entity.element.common.ElementHL7Constants;
import net.esb.entity.element.common.HL7Utils;
import net.esb.message.IMapMessage;

@Component
@Scope(value = "prototype")
public class HL7HapiMllpInputConnector extends AbstractMapInputConnector<HL7HapiMllpInputConnectorDefinition, HL7HapiMllpInputConnector> {
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_DISABLEVALIDATION)
	public String disableValidation = "" + Boolean.TRUE;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_HOST) 
    public String host = "0.0.0.0";
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_PORT) 
    public String port = "" + 8888;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_ACK) 
	public String autoAck = "" + Boolean.FALSE;
	
	@net.esb.entity.common.ElementDefinitionProperty(PROP_OUTPUT_TYPE) 
    public ElementHL7Constants.OUTPUT_TYPE output = ElementHL7Constants.OUTPUT_TYPE.PIPEHAT;
	
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
	
	transient Logger logger = LoggerFactory.getLogger(HL7HapiMllpInputConnector.class);
	
	@JsonIgnore
	HapiContext hapiContext = null;
	
	Boolean _autoAck;
	
	@JsonIgnore
	HL7Service server = null;
	
	class ReceiverApplication implements ReceivingApplication{
		
		IMapInputConnector<?,?> inputConnector;
		
		public ReceiverApplication(IMapInputConnector<?,?> inputConnector){
			super();
			this.inputConnector = inputConnector;
		}

		@Override
		public Message processMessage(Message hapiMessage, Map<String, Object> theMetadata) throws ReceivingApplicationException, HL7Exception {
			
			Object requestPayload = null;
			
			switch(output){
			case PIPEHAT:{
				String encodedMessage = hapiContext.getPipeParser().encode(hapiMessage);
				requestPayload = encodedMessage;
				}
				break;
			case XML:{
				String encodedMessage = hapiContext.getXMLParser().encode(hapiMessage);
				requestPayload = encodedMessage;
				}
				break;
			case HAPI_OBJECT:
			default:				
				requestPayload = hapiMessage;
			}

			IMapMessage requestMessage = getMap().buildMessage(requestPayload);
			
			if(_autoAck){ // not using latched
				try {
					// async post
					IMapElementContext context = getMap().buildContext(inputConnector);
					postRequestToMap(context, requestMessage);
				   	Message responseHapi = hapiMessage.generateACK();
				   	return responseHapi;
				} catch (Exception e) {
				   throw new HL7Exception(e);
				}
			}else{
				// using latched blocked 
				IMapMessage responseMessage;
				try {
					IMapElementContext context = getMap().buildContext(inputConnector);
					responseMessage = postRequestToMap(context, requestMessage);
				} catch (Exception e) {
					throw new ReceivingApplicationException("postRequestToMap: "+e);
				}
				Object responsePayload = responseMessage.getPayload();
				
				if(responsePayload instanceof ca.uhn.hl7v2.model.Message){					
					return (ca.uhn.hl7v2.model.Message)responsePayload;
				}else if(responsePayload instanceof String){
					if(((String)responsePayload).startsWith("MSH")){
						return hapiContext.getPipeParser().parse(responsePayload.toString());
					}else if(((String)responsePayload).startsWith("<?xml")){// is XML
						return hapiContext.getXMLParser().parse(responsePayload.toString());
					}else {
						throw new ReceivingApplicationException("Payload is unrecognised type: "+responsePayload.getClass().getName());
					}
				}else{
					throw new ReceivingApplicationException("Payload is unrecognised type: "+responsePayload.getClass().getName());
				}
			}
		}

		@Override
		public boolean canProcess(Message theMessage) {
			return true;
		}
		
	}
    
	@Override
    public void start() throws EntityStartException{
		
		//ServerSocket.setReuseAddress(true);
		
		_autoAck = this.<Boolean>parseStartProperty(Boolean.class, getAutoAck());
		Integer _port = null;
		
		try{
			_port = getMap().<Integer>parseProperty(Integer.class, getPort());
		}catch (Exception e) {
		    throw new EntityStartException("Cant parse port "+getPort()+" "+e);
		}
    	
    	if(getStarted()){return;}
    	
    	
		try {
			boolean useTls = false; // Should we use TLS/SSL?
			hapiContext = new DefaultHapiContext();
			Boolean _disableValidation = getMap().<Boolean>parseProperty(Boolean.class, getDisableValidation());
			if(_disableValidation){HL7Utils.disableValidation(hapiContext);}
			
			ReceivingApplication handler = new ReceiverApplication(this);
			ReceivingApplicationExceptionHandler exceptionHandler = new MyExceptionHandler(this);
			
			serverStart(_port, useTls, handler, exceptionHandler);

		    super.start();
		}catch (Exception e) {
		    throw new EntityStartException(e);
		}
        
 
    }
	
	private void serverStart(Integer _port, boolean useTls, ReceivingApplication handler, ReceivingApplicationExceptionHandler exceptionHandler) throws EntityStartException, InterruptedException {
		boolean bindFault = true;
			
    	try {
    		if(null!=server){
    			// server might be stopped but do it again anyway
    			server.stop();
    		}
    		if(null!=hapiContext){hapiContext.close();}
    		super.stop();
		} catch (Exception e) {
			// ignored
		}
    	
//		for(int i=0; i<30 && (false==org.apache.mina.util.AvailablePortFinder.available(_port)); i++){
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		// try 40 times 100ms (ie 4 seconds) in case we have a port that is in the process of being released
		for(int i=0; i<40 && (bindFault==true); i++){
			logger.debug("iteration "+(i+1)+" startup attempt");
			

			server = hapiContext.newServer(_port, useTls);
			
			server.registerApplication("*", "*", handler);

			
			/*
			* If you want to be notified any time a new connection comes in or is
			* lost, you might also want to register a connection listener
			*/
			//server.registerConnectionListener(new MyConnectionListener());
			
			/*
			* If you want to be notified any processing failures when receiving,
			* processing, or responding to messages with the server, you can 
			* also register an exception handler. 
			*/
			server.setExceptionHandler(exceptionHandler);

			 
			// Start the server in the background listening for messages
			// wait for latch so we can check the service
			server.startAndWait();

			if(null!=server.getServiceExitedWithException()){
				bindFault = true;

				logger.warn("bind exception, backing off.  "+server.getServiceExitedWithException());
				server.stopAndWait();
				Thread.sleep(100);

			}else{
				bindFault = false;
			}

		}
		if(bindFault){
			throw new EntityStartException(server.getServiceExitedWithException());
		}
	}

	class MyExceptionHandler implements ReceivingApplicationExceptionHandler{
		
		private IMapElement<?, ?> element;

		public MyExceptionHandler(IMapElement<?,?> element){
			this.element = element;
		}

		@Override
		public String processException(String incomingMessage, Map<String, Object> incomingMetadata, String outgoingMessage, Exception e) throws HL7Exception {
			//TODO need to async dispatch the handleException
			getMap().handleException(element, e);
			return outgoingMessage;
		}
	}
    
    @Override
    public void stop() throws EntityStopException{

    	if(!getStarted()){return;}
    	
        // stop the transport	
    	try {
    		if(null!=server){server.stop();}
    		// dont set server to null because we use that to identify a restart
    		if(null!=hapiContext){hapiContext.close();}
    		super.stop();
    		
		} catch (Exception e) {
			throw new ElementStopException(e);
		}
        

    }

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setHost(String host) {
		this.host = host;
	}

	public String getAutoAck() {
		return autoAck;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setAutoAck(String autoAck) {
		this.autoAck = autoAck;
	}

	@net.esb.entity.common.ElementPropertySetter
	public void setPort(String port) {
		this.port = port;
	}

	public ElementHL7Constants.OUTPUT_TYPE getOutput() {
		return output;
	}
	
	@net.esb.entity.common.ElementPropertySetter
	public void setOutput(ElementHL7Constants.OUTPUT_TYPE output) {
		this.output = output;
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

	@Override
	public boolean isSynchronous() {
		return !_autoAck;
	}

	
	public String getDisableValidation() {
		return disableValidation;
	}
	
	@net.esb.entity.common.ElementPropertySetter
	public void setDisableValidation(String disableValidation) {
		this.disableValidation = disableValidation;
	}

	
}

