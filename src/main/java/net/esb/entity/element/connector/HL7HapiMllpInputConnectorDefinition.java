/**
 * Copyright (c) 2016-2017 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package net.esb.entity.element.connector;

import static net.esb.entity.common.ElementStandardIcon.*;
import static net.esb.entity.common.EntityConfigurationProperty.ElementPropertyType.*;
import static net.esb.entity.element.common.ElementHL7Constants.*;
import static net.esb.entity.element.common.ElementNetworkConstants.*;


//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_DESC;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_KEYSTORE;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_KEYSTORE_DESC;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_KEYSTORE_PASSWORD;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_KEYSTORE_PASSWORD_DESC;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_TRUSTSTORE;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_TRUSTSTORE_DESC;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_TRUSTSTORE_PASSWORD;
//import static net.esb.entity.element.common.ElementNetworkConstants.PROP_TLS_TRUSTSTORE_PASSWORD_DESC;
//import static net.esb.entity.map.EntityConfigurationProperty.ElementPropertyType.PASSWORD;

import org.springframework.stereotype.Component;

import net.esb.build.BuildInfo;
import net.esb.entity.common.Colors;
import net.esb.entity.common.EntityConfigurationProperty;
import net.esb.entity.element.common.ElementHL7Constants;
import net.esb.plugin.IPluginInfo;
import net.esb.plugin.PluginInfo;

@Component
public class HL7HapiMllpInputConnectorDefinition extends AbstractConnectorDefinition <HL7HapiMllpInputConnectorDefinition, HL7HapiMllpInputConnector>{
	
	private static final long serialVersionUID = 1L;
	
	final String name = "HL7 Hapi MLLP InputConnector";

	final String description = "HL7v2 input connector";
	final String url = SMILEY_INPUT;
	
	IPluginInfo pluginInfo = null;
	
	public HL7HapiMllpInputConnectorDefinition() {
		super();
		registerProperties();
		resolveEntityBeanProperties();
	}
	
	private void registerProperties(){
		// TODO https://cloud21.atlassian.net/browse/ESB-101
		//configurationProperties.add(new EntityConfigurationProperty(PROP_HOST, PROP_HOST_DESC, STRING, NOTREADONLY, 100));
		
		configurationProperties.add(new EntityConfigurationProperty(PROP_PORT, PROP_PORT_DESC, INTEGER, NOTREADONLY, 50));
		
		configurationProperties.add(new EntityConfigurationProperty(PROP_ACK, PROP_ACK_DESC, BOOLEAN, NOTREADONLY, 51));

		EntityConfigurationProperty operation = new EntityConfigurationProperty(PROP_OUTPUT_TYPE, PROP_OUTPUT_TYPE_DESC, CHOICE, NOTREADONLY, 52);
		operation.setChoices(choicesMapFromEnum(ElementHL7Constants.OUTPUT_TYPE.class, null));
		configurationProperties.add(operation); 
		
		configurationProperties.add(new EntityConfigurationProperty(PROP_DISABLEVALIDATION, PROP_DISABLEVALIDATION_DESC, BOOLEAN, NOTREADONLY, 53));
		
		
		// TLS not yet implemented
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS, PROP_TLS_DESC, BOOLEAN, NOTREADONLY, 54));
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS_KEYSTORE, PROP_TLS_KEYSTORE_DESC, STRING, NOTREADONLY, 55, 1, "PROP_TLS==true"));
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS_KEYSTORE_PASSWORD, PROP_TLS_KEYSTORE_PASSWORD_DESC, PASSWORD, NOTREADONLY, 56, 1, "PROP_TLS==true"));
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS_TRUSTSTORE, PROP_TLS_TRUSTSTORE_DESC, STRING, NOTREADONLY, 57, 1, "PROP_TLS==true"));
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS_TRUSTSTORE_PASSWORD, PROP_TLS_TRUSTSTORE_PASSWORD_DESC, PASSWORD, NOTREADONLY, 58, 1, "PROP_TLS==true"));
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public String getUrl(){
		return url;
	}
	
	@Override
	public String getDescription(){
		return description;
	}
	
	@Override
	public IPluginInfo getPluginInfo() {
		if(null==pluginInfo){
			pluginInfo = new PluginInfo(
					"Intamerge developers",
					getName(),
					BuildInfo.buildInfo().getVersion(),
					"Intamerge license",
					"Intamerge"
					);
			
			((PluginInfo)pluginInfo).setPluginIcon(getUrl());
			((PluginInfo)pluginInfo).setPluginColor(Colors.bg_inputgreen);

		}
		return pluginInfo;
	}


}
