package com.demo.hornetq.consumer.receiver;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class InternalQueueReceiver {

	private static final Logger logger = LogManager.getLogger(InternalQueueReceiver.class);
	
	@JmsListener(destination = "${jms.queue.jndi}", containerFactory = "jmsListenerContainerFactory")
	public void receiveInternalQueueMessage(Message msg) {
		
		logger.debug("receiveInternalQueueMessage Message to be consumed");
		//ObjectMessage om = (ObjectMessage) msg;
		try {
			
			//ObjectMapper objectMapper = new ObjectMapper();
			if ((msg instanceof TextMessage)) {
				String text =  ((TextMessage) msg).getText();
				logger.info("receiveInternalQueueMessage :: message succesfully consumed :  " + text);
			}else{
				logger.info("receiveInternalQueueMessage :: message not consumed ");
			}
		}catch (JMSException e) {
			logger.error("JMS error processing message " + msg);
		} catch (Exception e) {
			logger.error("JMS error processing message " + msg);
		}
	}
	
}
