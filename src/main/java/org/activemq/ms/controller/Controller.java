package org.activemq.ms.controller;

import org.activemq.ms.model.Foo;
import org.activemq.ms.service.ProducerService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/api")
public class Controller {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private Logger logger;

    @Autowired
    private ObjectMapper mapper;

    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastMsg(@RequestBody Foo foo){
        try {
            producerService.sendToTopic(foo);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/queue/{queue}/send")
    public ResponseEntity<Void> send(@RequestBody Foo foo, @PathVariable String queue){
        try {
            logger.info("Trying to send {} to {}", mapper.writeValueAsString(foo), queue);
            producerService.sendToQueue(foo, queue);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.accepted().build();
    }

}
