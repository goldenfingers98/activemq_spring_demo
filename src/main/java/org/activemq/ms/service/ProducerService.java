package org.activemq.ms.service;

import static org.activemq.ms.config.MsConfig.topic;

import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProducerService {

    @Autowired
    private JmsTemplate template;

    @Autowired
    private ObjectMapper mapper;

    private String getJsonFromObject(Object obj) throws JsonProcessingException {
        return mapper.writer()
                .withDefaultPrettyPrinter()
                .writeValueAsString(obj);
    }

    private void _send(Object obj, String dest) throws JsonProcessingException {
        String strJson = getJsonFromObject(obj);
        template.send(dest, messageCreator -> {
            TextMessage msg = messageCreator.createTextMessage();
            msg.setText(strJson);
            return msg;
        });
    }

    public void sendToQueue(Object obj, String queue) throws JsonProcessingException {
        _send(obj, queue);
    }

    public void sendToTopic(Object obj) throws JsonProcessingException {
        _send(obj, topic);
    }
}
