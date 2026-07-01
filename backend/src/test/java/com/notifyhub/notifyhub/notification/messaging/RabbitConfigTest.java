package com.notifyhub.notifyhub.notification.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

class RabbitConfigTest {

    final RabbitConfig config = new RabbitConfig();

    @Test
    void exchangesAreDurableWithExpectedNames() {
        DirectExchange exchange = config.notifyhubExchange();
        DirectExchange dlx = config.deadLetterExchange();

        assertThat(exchange.getName()).isEqualTo(RabbitConfig.EXCHANGE);
        assertThat(exchange.isDurable()).isTrue();
        assertThat(dlx.getName()).isEqualTo(RabbitConfig.DLX);
    }

    @Test
    void deliveryQueueDeclaresDeadLettering() {
        Queue queue = config.deliveryQueue();

        assertThat(queue.getName()).isEqualTo(RabbitConfig.QUEUE);
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", RabbitConfig.DLX)
                .containsEntry("x-dead-letter-routing-key", RabbitConfig.DLQ_ROUTING_KEY);
    }

    @Test
    void deadLetterQueueHasExpectedName() {
        assertThat(config.deadLetterQueue().getName()).isEqualTo(RabbitConfig.DLQ);
    }

    @Test
    void bindingsAreCreated() {
        Binding delivery = config.deliveryBinding();
        Binding dead = config.deadLetterBinding();

        assertThat(delivery.getRoutingKey()).isEqualTo(RabbitConfig.ROUTING_KEY);
        assertThat(dead.getRoutingKey()).isEqualTo(RabbitConfig.DLQ_ROUTING_KEY);
    }

    @Test
    void messageConverterIsJackson() {
        MessageConverter converter = config.jacksonMessageConverter(new ObjectMapper());
        assertThat(converter).isInstanceOf(Jackson2JsonMessageConverter.class);
    }

    @Test
    void rabbitTemplateUsesConfiguredConverter() {
        MessageConverter converter = config.jacksonMessageConverter(new ObjectMapper());
        RabbitTemplate template = config.rabbitTemplate(mock(ConnectionFactory.class), converter);

        assertThat(template.getMessageConverter()).isSameAs(converter);
    }
}
