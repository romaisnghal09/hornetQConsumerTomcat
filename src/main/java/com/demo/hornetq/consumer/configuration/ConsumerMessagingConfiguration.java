/***************************************************************************
 *  Copyright (C) Proximus 2018
 *
 *  The reproduction, transmission or use of this document  or its contents
 *  is not  permitted without  prior express written consent of Proximus.
 *  Offenders will be liable for damages. All rights, including  but not 
 *  limited to rights created by patent grant or registration of a utility
 *  model or design, are reserved.
 *
 *  Proximus reserves the right to modify technical specifications and features.
 *
 *  Technical specifications and features are binding only in so far as they
 *  are specifically and expressly agreed upon in a written contract.
 *
 **************************************************************************/

package com.demo.hornetq.consumer.configuration;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;


@Configuration
@EnableJms
public class ConsumerMessagingConfiguration {

	private static final Logger LOGGER = LogManager.getLogger(ConsumerMessagingConfiguration.class);

	private static final String javaNamingFactoryInitial = "org.jboss.naming.remote.client.InitialContextFactory";

	@Value("${jms.naming.provider.url}")
	private String jmsNamingProviderUrl;

	@Value("${jms.naming.provider.port}")
	private String jmsNamingProviderPort;

	@Value("${spring.jms.jndi-name}")
	private String jmsConnectionFactoryName;

	@Value("${jms.naming.security.principal}")
	private String securityPrincipal;

	@Value("${jms.naming.security.credentials}")
	private String securityCredentials;

	/**
	 * This method is used to configure the JNDI factory using JNDI template.
	 * @return
	 */
	@Bean(name = "jndiConnectionFactory")
	public JndiObjectFactoryBean jndiConnectionFactory() {
		LOGGER.info("jndiConnectionFactory start .....");
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		Properties environment = new Properties();
		JndiTemplate jndiTemplate = new JndiTemplate();
		try {
			jndiObjectFactoryBean.getJndiTemplate().lookup(jmsNamingProviderUrl);
			if (StringUtils.isNotBlank(javaNamingFactoryInitial)) {
				environment.put(Context.INITIAL_CONTEXT_FACTORY, javaNamingFactoryInitial.trim());
			}

			if (StringUtils.isNotBlank(jmsNamingProviderUrl)) {
				environment.put(Context.PROVIDER_URL, jmsNamingProviderUrl.trim());
			}
			environment.put(Context.SECURITY_PRINCIPAL, securityPrincipal.trim());
			environment.put(Context.SECURITY_CREDENTIALS, securityCredentials.trim());
			jndiTemplate.setEnvironment(environment);
			jndiObjectFactoryBean.setJndiTemplate(jndiTemplate);
			LOGGER.info("Remote jms naming provider url : " + jmsNamingProviderUrl);
		} catch (NamingException e) {
			LOGGER.error("Remote is not available trying to connect with local. " + e.getMessage());
			if (StringUtils.isNotBlank(javaNamingFactoryInitial)) {
				environment.put(Context.INITIAL_CONTEXT_FACTORY, javaNamingFactoryInitial.trim());
			}
			LOGGER.info("jmsNamingProviderPort-----" + jmsNamingProviderPort);
			if (StringUtils.isNotBlank(jmsNamingProviderUrl)) {
				environment.put(Context.PROVIDER_URL, "http-remoting://localhost:" + jmsNamingProviderPort);
				LOGGER.info("Local jms naming provider url : : http-remoting://localhost:" + jmsNamingProviderPort);

			}

			environment.put(Context.SECURITY_PRINCIPAL, securityPrincipal.trim());
			environment.put(Context.SECURITY_CREDENTIALS, securityCredentials.trim());
			jndiTemplate.setEnvironment(environment);
			jndiObjectFactoryBean.setJndiTemplate(jndiTemplate);
		}
		jndiObjectFactoryBean.setJndiName(jmsConnectionFactoryName);
		jndiObjectFactoryBean.setResourceRef(true);
		LOGGER.info("jndiConnectionFactory end .....");
		return jndiObjectFactoryBean;
	}

	/**
	 * This method is used to configure the JMS CachingConnectionFactory using
	 * jndiConnectionFactory.
	 * 
	 * @param jndiConnectionFactory
	 * @return
	 */
	@Bean(name = "jmsConnectionFactory")
	@Primary
	public CachingConnectionFactory jmsConnectionFactory(
			@Qualifier("jndiConnectionFactory") JndiObjectFactoryBean jndiConnectionFactory) {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		ConnectionFactory connectionFactory = (ConnectionFactory) jndiConnectionFactory.getObject();
		cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
		cachingConnectionFactory.afterPropertiesSet();
		return cachingConnectionFactory;
	}

	/**
	 * This method is used to configure JMS template using jmsConnectionFactory.
	 * 
	 * @param jmsConnectionFactory
	 * @return
	 */
	@Bean(name = "jmsTemplate")
	public JmsTemplate jmsTemplate(@Qualifier("jmsConnectionFactory") CachingConnectionFactory jmsConnectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory);
		return jmsTemplate;
	}

}
