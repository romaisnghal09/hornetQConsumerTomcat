package com.demo.hornetq.consumer.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.autoconfigure.jms.JmsProperties.AcknowledgeMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class ConsumerMessageListenerConfig {

	private static final Logger LOGGER = LogManager.getLogger(ConsumerMessageListenerConfig.class);

	/**
	 * jmsProperties
	 */
	@Autowired
	private JmsProperties jmsProperties;

	/**
	 * connectionFactory
	 */
	@Autowired
	private CachingConnectionFactory connectionFactory;
	
	@Value("${concurrency.min.max.range}")
	private String concurrencyMinMaxRange;
	
	
	@Bean(name = "jmsListenerContainerFactory")
	public JmsListenerContainerFactory<?> jmsContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer) {
		LOGGER.debug("jmsListenerContainerFactory  +");

		Integer minThread = jmsProperties.getListener().getConcurrency();
		Integer maxThread = jmsProperties.getListener().getMaxConcurrency();
		AcknowledgeMode acknowledgeMode = jmsProperties.getListener().getAcknowledgeMode();

		if (minThread == null) {
			minThread = 1;
		}
		if (maxThread == null) {
			maxThread = minThread;
		}

		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setSessionTransacted(true);
		factory.setConcurrency(minThread.toString() + "-" + maxThread.toString());
		if (acknowledgeMode != null) {
			factory.setSessionAcknowledgeMode(acknowledgeMode.getMode());
		}
		LOGGER.debug("jmsListenerContainerFactory  -");
		return factory;
	}

	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		LOGGER.debug("jacksonJmsMessageConverter  ===>");
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
	
}
