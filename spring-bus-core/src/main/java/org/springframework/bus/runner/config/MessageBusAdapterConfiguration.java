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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.bus.runner.adapter.ChannelLocator;
import org.springframework.bus.runner.adapter.Input;
import org.springframework.bus.runner.adapter.InputChannelBinding;
import org.springframework.bus.runner.adapter.MessageBusAdapter;
import org.springframework.bus.runner.adapter.Output;
import org.springframework.bus.runner.adapter.OutputChannelBinding;
import org.springframework.bus.runner.endpoint.ChannelsEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;
import org.springframework.xd.dirt.integration.bus.MessageBus;
import org.springframework.xd.dirt.integration.bus.MessageBusAwareRouterBeanPostProcessor;

/**
 * @author Dave Syer
 *
 */
@Configuration
@ImportResource("classpath*:/META-INF/spring-xd/bus/codec.xml")
@EnableConfigurationProperties(MessageBusProperties.class)
public class MessageBusAdapterConfiguration {

	@Autowired
	private MessageBusProperties module;

	@Autowired
	private ListableBeanFactory beanFactory;

	@Autowired(required=false)
	@Input
	private ChannelLocator inputChannelLocator;

	@Autowired(required=false)
	@Output
	private ChannelLocator outputChannelLocator;

	@Bean
	public MessageBusAdapter messageBusAdapter(MessageBusProperties module,
			MessageBus messageBus) {
		MessageBusAdapter adapter = new MessageBusAdapter(module, messageBus);
		adapter.setOutputChannels(getOutputChannels());
		adapter.setInputChannels(getInputChannels());
		if (this.inputChannelLocator!=null) {
			adapter.setInputChannelLocator(this.inputChannelLocator);
		}
		if (this.outputChannelLocator!=null) {
			adapter.setOutputChannelLocator(this.outputChannelLocator);
		}
		return adapter;
	}

	@Bean
	public ChannelsEndpoint channelsEndpoint(MessageBusAdapter adapter) {
		return new ChannelsEndpoint(adapter);
	}

	protected Collection<OutputChannelBinding> getOutputChannels() {
		Set<OutputChannelBinding> channels = new LinkedHashSet<OutputChannelBinding>();
		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				this.beanFactory, MessageChannel.class);
		for (String name : names) {
			if (name.startsWith("output")) {
				channels.add(new OutputChannelBinding(name));
			}
		}
		return channels;
	}

	protected Collection<InputChannelBinding> getInputChannels() {
		Set<InputChannelBinding> channels = new LinkedHashSet<InputChannelBinding>();
		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				this.beanFactory, MessageChannel.class);
		for (String name : names) {
			if (name.startsWith("input")) {
				channels.add(new InputChannelBinding(name));
			}
		}
		return channels;
	}

	// Nested class to avoid instantiating all of the above early
	@Configuration
	protected static class MessageBusAwareRouterConfiguration {

		@Autowired
		private ListableBeanFactory beanFactory;

		@Bean
		public MessageBusAwareRouterBeanPostProcessor messageBusAwareRouterBeanPostProcessor() {

			return new MessageBusAwareRouterBeanPostProcessor(createLazyProxy(
					this.beanFactory, MessageBus.class), new Properties());
		}

		private <T> T createLazyProxy(ListableBeanFactory beanFactory, Class<T> type) {
			ProxyFactory factory = new ProxyFactory();
			LazyInitTargetSource source = new LazyInitTargetSource();
			source.setTargetClass(type);
			source.setTargetBeanName(getBeanNameFor(beanFactory, MessageBus.class));
			source.setBeanFactory(beanFactory);
			factory.setTargetSource(source);
			factory.addAdvice(new PassthruAdvice());
			factory.setInterfaces(new Class<?>[] { type });
			@SuppressWarnings("unchecked")
			T proxy = (T) factory.getProxy();
			return proxy;
		}

		private String getBeanNameFor(ListableBeanFactory beanFactory, Class<?> type) {
			String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
					beanFactory, type, false, false);
			Assert.state(names.length == 1, "No unique MessageBus (found " + names.length
					+ ": " + Arrays.asList(names) + ")");
			return names[0];
		}

		private class PassthruAdvice implements MethodInterceptor {

			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				return invocation.proceed();
			}

		}

	}

}
