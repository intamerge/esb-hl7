/**
 * Copyright (c) 2016-2016 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package net.esb.entity.element.connector;

import static net.esb.entity.common.EntityConfigurationProperty.ElementPropertyType.BOOLEAN;
import static net.esb.entity.common.EntityConfigurationProperty.ElementPropertyType.CHOICE;
import static net.esb.entity.common.EntityConfigurationProperty.ElementPropertyType.INTEGER;
import static net.esb.entity.common.EntityConfigurationProperty.ElementPropertyType.STRING;
import static net.esb.entity.common.ElementStandardIcon.SMILEY_OUTPUT;
import static net.esb.entity.element.common.ElementHL7Constants.PROP_DISABLEVALIDATION;
import static net.esb.entity.element.common.ElementHL7Constants.PROP_DISABLEVALIDATION_DESC;
import static net.esb.entity.element.common.ElementHL7Constants.PROP_OUTPUT_TYPE;
import static net.esb.entity.element.common.ElementHL7Constants.PROP_OUTPUT_TYPE_DESC;
import static net.esb.entity.element.common.ElementNetworkConstants.PROP_HOST;
import static net.esb.entity.element.common.ElementNetworkConstants.PROP_HOST_DESC;
import static net.esb.entity.element.common.ElementNetworkConstants.PROP_PORT;
import static net.esb.entity.element.common.ElementNetworkConstants.PROP_PORT_DESC;

import org.springframework.stereotype.Component;

import net.esb.build.BuildInfo;
import net.esb.entity.common.Colors;
import net.esb.entity.common.EntityConfigurationProperty;
import net.esb.entity.element.common.ElementHL7Constants;
import net.esb.entity.element.connector.AbstractConnectorDefinition;
import net.esb.plugin.IPluginInfo;
import net.esb.plugin.PluginInfo;

@Component
public class HL7HapiMllpOutputConnectorDefinition extends AbstractConnectorDefinition<HL7HapiMllpOutputConnectorDefinition, HL7HapiMllpOutputConnector>{
	
	private static final long serialVersionUID = 1L;
	
	final String name = "HL7 Hapi MLLP OutputConnector";
	final String description = "HL7 output connector";
	final String url = SMILEY_OUTPUT;
	
	IPluginInfo pluginInfo =  null;
	
	public HL7HapiMllpOutputConnectorDefinition() {
		super();
		registerProperties();
		resolveEntityBeanProperties();
	}
	
	void registerProperties(){
		configurationProperties.add(new EntityConfigurationProperty(PROP_HOST, PROP_HOST_DESC, STRING, NOTREADONLY, 50));
		configurationProperties.add(new EntityConfigurationProperty(PROP_PORT, PROP_PORT_DESC, INTEGER, NOTREADONLY, 51));
		
		EntityConfigurationProperty operation = new EntityConfigurationProperty(PROP_OUTPUT_TYPE, PROP_OUTPUT_TYPE_DESC, CHOICE, READONLY, 52);
		operation.setChoices(choicesMapFromEnum(ElementHL7Constants.OUTPUT_TYPE.class, null));
		configurationProperties.add(operation); 

		configurationProperties.add(new EntityConfigurationProperty(PROP_DISABLEVALIDATION, PROP_DISABLEVALIDATION_DESC, BOOLEAN, NOTREADONLY, 53));
		
		// tls not yet implemented
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS, PROP_TLS_DESC, BOOLEAN, NOTREADONLY, 54));
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS_KEYSTORE, PROP_TLS_KEYSTORE_DESC, STRING, NOTREADONLY, 55, 1, "PROP_TLS==true"));
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS_KEYSTORE_PASSWORD, PROP_TLS_KEYSTORE_PASSWORD_DESC, PASSWORD, NOTREADONLY, 56, 1, "PROP_TLS==true"));
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS_TRUSTSTORE, PROP_TLS_TRUSTSTORE_DESC, STRING, NOTREADONLY, 57, 1, "PROP_TLS==true"));
//		configurationProperties.add(new EntityConfigurationProperty(PROP_TLS_TRUSTSTORE_PASSWORD, PROP_TLS_TRUSTSTORE_PASSWORD_DESC, PASSWORD, NOTREADONLY, 58, 1, "PROP_TLS==true"));
//		
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
			((PluginInfo)pluginInfo).setPluginColor(Colors.bg_outputmustard);

		}
		return pluginInfo;
	}
	

}
