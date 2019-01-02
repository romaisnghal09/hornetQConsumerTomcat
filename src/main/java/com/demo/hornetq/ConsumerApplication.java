package com.demo.hornetq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class ConsumerApplication extends SpringBootServletInitializer{
	
	private static final Logger LOGGER = LogManager.getLogger(ConsumerApplication.class);

	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		
		LOGGER.debug("ConsumerApplication SpringApplicationBuilder start");
		return application.sources(ConsumerApplication.class);
	}
	
	public static void main(String[] args) {
		LOGGER.info("ConsumerApplication context configuration start.......");
		SpringApplication.run(ConsumerApplication.class, args);
		LOGGER.info("ConsumerApplication  context configuration end.......");
	}
	
	

}

