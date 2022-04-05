/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.bus.runner.config;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.xd.dirt.integration.bus.BusUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Dave Syer
 *
 */
@ConfigurationProperties("spring.bus")
@JsonInclude(Include.NON_DEFAULT)
public class MessageBusProperties {

	private String name = "module";

	private String group = "group";

	private int index = 0;

	private String outputChannelName;

	private String inputChannelName;

	private String type = "processor";

	private Properties consumerProperties = new Properties();

	private Properties producerProperties = new Properties();

	private Tap tap;

	private Discovery discovery = new Discovery();

	private boolean autoStartup = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getInputChannelName() {
		if (isTap()) {
			return String.format("%s.%s.%s", BusUtils.constructTapPrefix(tap.getGroup()),
					tap.getName(), tap.getIndex());
		}
		return (inputChannelName != null) ? inputChannelName : BusUtils
				.constructPipeName(group, index > 0 ? index - 1 : index);
	}

	public String getOutputChannelName() {
		return (outputChannelName != null) ? outputChannelName : BusUtils
				.constructPipeName(group, index);
	}

	public String getTapChannelName() {
		return getTapChannelName(group);
	}

	public String getTapChannelName(String prefix) {
		Assert.isTrue(!type.equals("job"), "Job module type not supported.");
		// for Stream return channel name with indexed elements
		return String
				.format("%s.%s.%s", BusUtils.constructTapPrefix(prefix), name, index);
	}

	public void setOutputChannelName(String outputChannelName) {
		this.outputChannelName = outputChannelName;
	}

	public void setInputChannelName(String inputChannelName) {
		this.inputChannelName = inputChannelName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Properties getConsumerProperties() {
		return consumerProperties;
	}

	public void setConsumerProperties(Properties consumerProperties) {
		this.consumerProperties = consumerProperties;
	}

	public Properties getProducerProperties() {
		return producerProperties;
	}

	public void setProducerProperties(Properties producerProperties) {
		this.producerProperties = producerProperties;
	}

	public boolean isAutoStartup() {
		return autoStartup;
	}

	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	public Tap getTap() {
		return tap;
	}

	public void setTap(Tap tap) {
		this.tap = tap;
	}

	private boolean isTap() {
		if (tap != null) {
			Assert.state(tap.getName() != null, "Tap name not provided");
			Assert.state(!tap.getGroup().equals(group),
					"Tap group cannot be the same as module group");
		}
		return tap != null;
	}

	public Discovery getDiscovery() {
		return discovery;
	}

	public static class Discovery {

		private boolean enabled = false;

		private String inputServiceId;

		private String outputServiceId;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getInputServiceId() {
			return inputServiceId;
		}

		public void setInputServiceId(String inputServiceId) {
			this.inputServiceId = inputServiceId;
		}

		public String getOutputServiceId() {
			return outputServiceId;
		}

		public void setOutputServiceId(String outputServiceId) {
			this.outputServiceId = outputServiceId;
		}

	}

	public static class Tap {

		private String group = "group";

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		private int index = 0;

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

	}

}
