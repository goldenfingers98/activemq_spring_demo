package org.activemq.ms.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @Autowired
    private Logger logger;


    private String _receive(final Message jsonMessage) throws JMSException{
        String messageData = null;
        if(jsonMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage)jsonMessage;
            messageData = textMessage.getText();
            logger.info("messageData:"+messageData);
        }
        return messageData;
    }
    

    @JmsListener(destination = "${spring.activemq.queue}")
    @SendTo("myNewQueue")
    public String receiveAndForwardMessageFromQueue(final Message jsonMessage) throws JMSException {
        return _receive(jsonMessage);
    }

    // @JmsListener(destination = "${spring.activemq.topic}")
    // @SendTo("myNewTopic")
    // public String receiveAndForwardMessageFromTopic(final Message jsonMessage) throws JMSException, JsonProcessingException {
    //     return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(_receive(jsonMessage));
    // }


    @JmsListener(destination = "${spring.activemq.topic}")
    public void receiveMessageFromTopic(final Message jsonMessage) throws JMSException {
        _receive(jsonMessage);
    }

}
