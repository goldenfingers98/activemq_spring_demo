package org.activemq.ms.config;

import javax.annotation.PostConstruct;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Setter;

@Configuration
@EnableJms
@ConfigurationProperties(prefix = "spring.activemq")
public class MsConfig {

    @Setter
    private String brokerUrl;
    @Setter
    private String user;
    @Setter
    private String password;

    public static String topic;
    public static String queue;

    public void setTopic(String topic) {
        MsConfig.topic = topic;
    }

    public void setQueue(String queue) {
        MsConfig.queue = queue;
    }

    private Logger logger = LoggerFactory.getLogger(MsConfig.class);

    @PostConstruct
    public void init() {
        logger.info("Active mq broker URL: {}", brokerUrl);
        logger.info("Active mq username: {}", user);
        logger.info("Active mq broker password: {}", "*****");
        logger.info("Active mq topic: {}", topic);
        logger.info("Active mq queue: {}", queue);
    }

    @Bean
    public ActiveMQConnectionFactory getConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setTrustAllPackages(true);
        factory.setBrokerURL(brokerUrl);
        factory.setUserName(user);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public MessageConverter getMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(getObjectMapper());
        return converter;
    }

    @Bean
    public DynamicDestinationResolver getDestinationResolver() {
        return new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain)
                    throws JMSException {
                if (destinationName.endsWith("Topic")) {
                    pubSubDomain = true;
                } else {
                    pubSubDomain = false;
                }
                return super.resolveDestinationName(session, destinationName, pubSubDomain);
            }
        };
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(getConnectionFactory());
        template.setMessageConverter(getMessageConverter());
        template.setPubSubDomain(true);
        template.setDestinationResolver(getDestinationResolver());
        template.setDeliveryPersistent(true);
        return template;
    }

    @Bean
    @Scope("prototype")
    public Logger getLogger(final InjectionPoint ip) {
        Class<?> clazz = ip.getMember().getDeclaringClass();
        return LoggerFactory.getLogger(clazz);
    }
}
