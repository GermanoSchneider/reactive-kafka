package com.example.reactivekafka.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KafkaDomainService {

  Mono<Void> send(Object message);

  Flux<Object> receive();
}
