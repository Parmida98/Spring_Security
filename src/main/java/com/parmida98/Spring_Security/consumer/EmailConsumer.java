package com.parmida98.Spring_Security.consumer;

import com.parmida98.Spring_Security.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

// @service:
//Spring skapar en instans av klassen automatiskt
//Den blir en bean i Application Context
//Den kan användas för logik som t.ex. hantering av inkommande meddelanden
//I praktiken säger du: detta är en komponent som ska utföra affärslogik.
@Service
public class EmailConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Spring skapar automatiskt en "listener container" som:
    //Håller en aktiv koppling till RabbitMQ
    //Väntar på nya meddelanden
    //Anropar metoden när ett meddelande anländer
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME) // @RabbitListener säger till Spring att denna metod ska lyssna på en RabbitMQ-kö.
    public void handleMessage(String message) { // Metoden som körs varje gång ett meddelande hamnar i kön.
        System.out.println("TESTING");
        log.info(message);
    }

}
