package com.parmida98.Spring_Security.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Den här konfigurationen resulterar i:
//📦 En kö: email-queue
//🔁 En exchange: email-exchange
//🔗 En binding:
//routing key: email.routing
//kopplar exchange → queue

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "email-queue";
    public static final String EXCHANGE_NAME = "email-exchange";
    public static final String ROUTING_KEY = "email.routing";

    @Bean
    public Queue emailQueue() {    // Metoden skapar och returnerar en Queue-instans som Spring registrerar hos RabbitMQ.
        return new Queue(QUEUE_NAME, true); // Den överlever om RabbitMQ startas om. Meddelanden och köstruktur finns kvar
    }

    @Bean
    public DirectExchange emailExchange() {         // Detta är en typ av exchange där routing sker baserat på exakt matchande routing key.
        return new DirectExchange(EXCHANGE_NAME);   // Den kommer att ta emot meddelanden och skicka dem vidare till rätt kö baserat på routing key.
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder
                .bind(emailQueue)   // Which queue gets messages
                .to(emailExchange)  // From which exchange
                .with(ROUTING_KEY); // Under what condition
    }

}
