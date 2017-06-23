/**
 * Copyright (c) 2016-2016 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package net.esb.entity.element.component;

import static net.esb.entity.common.ElementStandardIcon.*;

import org.springframework.stereotype.Component;

import net.esb.build.BuildInfo;
import net.esb.entity.common.Colors;
import net.esb.entity.common.EntityCategory;
import net.esb.plugin.IPluginInfo;
import net.esb.plugin.PluginInfo;

@Component
public class HL7HapiAckComponentDefinition extends AbstractComponentDefinition<HL7HapiAckComponentDefinition, HL7HapiAckComponent> {
	
	private static final long serialVersionUID = 1L;
	
	final String name = "HL7 Hapi Ack";
	final String url = SMILEY;

	final String description = "Generates an HL7 ACK message as HAPI Object.  Takes an HL7 String or HAPI Object as input.";
	
	IPluginInfo pluginInfo =  null;
	
	
	public HL7HapiAckComponentDefinition() {
		super();
		registerProperties();
		resolveEntityBeanProperties();
	}
	
	void registerProperties(){
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
	public String getCategory(){
		return EntityCategory.HEALTH;
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
			((PluginInfo)pluginInfo).setPluginColor(Colors.bg_softsmileylemon);

		}
		return pluginInfo;
	}
}
