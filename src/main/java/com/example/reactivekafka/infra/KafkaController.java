package com.example.reactivekafka.infra;

import com.example.reactivekafka.domain.KafkaDomainService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
class KafkaController implements KafkaDomainService {

  private final ReactiveKafkaConsumerTemplate<String, Object> consumer;

  private final String topic;

  private final ReactiveKafkaProducerTemplate<String, Object> producer;

  KafkaController(
      @Value("${topic.name}") String topic,
      ReactiveKafkaConsumerTemplate<String, Object> consumer,
      ReactiveKafkaProducerTemplate<String, Object> producer
  ) {
    this.topic = topic;
    this.consumer = consumer;
    this.producer = producer;
  }

  @Override
  public Mono<Void> send(Object message) {

    return producer
        .send(topic, message)
        .then();
  }

  @Override
  public Flux<Object> receive() {

    return consumer
        .receive()
        .map(ConsumerRecord::value);
  }
}
