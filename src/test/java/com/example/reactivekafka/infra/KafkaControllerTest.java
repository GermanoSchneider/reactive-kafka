package com.example.reactivekafka.infra;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static reactor.test.StepVerifier.create;

import com.example.reactivekafka.domain.Dummy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = {
    KafkaAutoConfiguration.class,
    KafkaControllerConfig.class,
    KafkaController.class
})
@EmbeddedKafka
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class KafkaControllerTest {

  @Autowired
  private KafkaController kafkaController;

  @Autowired
  private ReactiveKafkaProducerTemplate<String, Object> producer;

  @Autowired
  private ReactiveKafkaConsumerTemplate<String, Object> consumer;

  @Value("${topic.name}")
  private String topic;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private static Dummy DUMMY;

  @BeforeAll
  static void init() {

    DUMMY = Dummy.builder()
        .title("Dummy title")
        .description("Dummy description")
        .build();
  }

  @Test
  void shouldProduceMessage() {

    kafkaController.send(DUMMY).subscribe();

    var expectedValue = toJson(DUMMY);

    create(consumer.receiveAtMostOnce())
        .expectNextMatches(record -> toJson(record.value()).equals(expectedValue))
        .thenCancel()
        .verify();
  }

  @Test
  void shouldConsumeMessage() {

    producer.send(topic, DUMMY).subscribe();

    var expectedValue = toJson(DUMMY);

    create(kafkaController.receive())
        .expectNextMatches(record -> toJson(record).equals(expectedValue))
        .thenCancel()
        .verify();
  }

  private String toJson(Object object) {

    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
