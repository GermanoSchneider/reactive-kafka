package com.example.reactivekafka.infra;

import static java.util.Collections.singleton;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

@Configuration
class KafkaControllerConfig {

  @Value("${topic.name}")
  private String topic;

  @Bean
  SenderOptions<String, Object> senderOptions(KafkaProperties properties) {

    return SenderOptions.create(properties.buildProducerProperties());
  }

  @Bean
  ReceiverOptions<String, Object> receiverOptions(KafkaProperties properties) {

    ReceiverOptions<String, Object> receiverOptions = ReceiverOptions.create(properties.buildConsumerProperties());

    return receiverOptions.subscription(singleton(topic));
  }

  @Bean
  ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaProducerTemplate(KafkaProperties kafkaProperties) {

    return new ReactiveKafkaProducerTemplate<>(senderOptions(kafkaProperties));
  }

  @Bean
  ReactiveKafkaConsumerTemplate<String, Object> reactiveKafkaConsumerTemplate(KafkaProperties kafkaProperties) {

    return new ReactiveKafkaConsumerTemplate<>(receiverOptions(kafkaProperties));
  }
}
