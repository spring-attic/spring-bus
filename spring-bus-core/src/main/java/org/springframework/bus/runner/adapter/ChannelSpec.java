/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.bus.runner.adapter;

/**
 * @author Dave Syer
 * @author Ilayaperumal Gopinathan
 */
public class ChannelSpec {

	private String name;
	private String localName;

	public static final String DEFAULT_INPUT_CHANNEL_NAME = "input";

	public static final String DEFAULT_OUTPUT_CHANNEL_NAME = "output";

	public static final String QUEUE_CHANNEL_PREFIX = "queue";

	protected ChannelSpec() {
		this(null);
	}

	public ChannelSpec(String localName) {
		this.localName = localName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalName() {
		return this.localName;
	}

}
