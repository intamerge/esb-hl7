/**
 * Copyright (c) 2016-2016 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package net.esb.entity.element.component;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import net.esb.context.IMapElementContext;
import net.esb.entity.common.ElementStartException;
import net.esb.entity.common.ElementStopException;
import net.esb.entity.common.EntityStartException;
import net.esb.entity.common.EntityStopException;
import net.esb.entity.element.common.HL7Utils;
import net.esb.entity.element.component.AbstractMapComponent;
import net.esb.message.IMapMessage;

@Component
@Scope(value = "prototype")
public class HL7HapiAckComponent extends AbstractMapComponent<HL7HapiAckComponentDefinition, HL7HapiAckComponent>  {
			
	@JsonIgnore
	HapiContext hapiContext = null;
	

	
	
	@Override
	public Object processMessage(IMapMessage message, IMapElementContext context) throws Exception{
		
		ca.uhn.hl7v2.model.Message hapiMessage = HL7Utils.hapiMessageFromEsbMessage(hapiContext, message);
		
		Message responseAck = hapiMessage.generateACK();
		message.setPayload(responseAck);
		
		return message;
	}
	
	@Override
    public void start() throws EntityStartException{
    	
    	if(getStarted()){return;}
    	
		try {
			hapiContext = new DefaultHapiContext();
			HL7Utils.disableValidation(hapiContext);
		    super.start();
		} catch (Exception e) {
		    throw new ElementStartException(e);
		}
        
 
    }
    
    @Override	
    public void stop() throws EntityStopException{

    	if(!getStarted()){return;}
    	
    	try {
    		hapiContext.close();
    		super.stop();
		} catch (Exception e) {
			throw new ElementStopException(e);
		}
        

    }




    
}
