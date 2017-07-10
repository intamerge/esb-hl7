/**
 * Copyright (c) 2016-2017 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package net.esb.entity.element.common;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import net.esb.entity.element.common.ElementHL7Constants;
import net.esb.entity.map.IMap;
import net.esb.message.IMapMessage;

public class HL7Utils {

	public static Message hapiMessageFromEsbMessage(HapiContext hapiContext, IMapMessage message) throws Exception{
		ca.uhn.hl7v2.model.Message hapiMessage = null;
		
		if(null==message.getPayload()){
			throw new Exception("Payload is null");
		}
		else if(message.getPayload() instanceof ca.uhn.hl7v2.model.Message){
			hapiMessage = (ca.uhn.hl7v2.model.Message)message.getPayload();
		}else if(message.getPayload() instanceof String){
			hapiMessage = hapiContext.getPipeParser().parse(message.getPayload().toString());
		}else{
			throw new Exception("Payload is unrecognised type: "+message.getPayload().getClass().getName());
		}
		
		return hapiMessage;
	}
	
	public static void disableValidation(HapiContext hapiContext){
		hapiContext.getParserConfiguration().setValidating(false);
		//hapiContext.setValidationContext(ValidationContextFactory.noValidation().getClass().getCanonicalName());
		//hapiContext.setValidationRuleBuilder(new NoValidationBuilder());
	}
	
	public static IMapMessage esbMessageFromHapiMessage(ElementHL7Constants.OUTPUT_TYPE output, IMap<?,?> map, HapiContext hapiContext, Message hapiMessage) throws HL7Exception {
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

		IMapMessage requestMessage = map.buildMessage(requestPayload);
		return requestMessage;
	}
}
