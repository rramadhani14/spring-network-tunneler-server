package dev.ramadhani.springnetworktunnelerserver.tunneler.controller;


import dev.ramadhani.springnetworktunnelerserver.tunneler.model.HttpRequest;
import dev.ramadhani.springnetworktunnelerserver.tunneler.model.HttpResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@AllArgsConstructor
public class HttpController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<ResponseEntity<Object>>> pendingRequests = new ConcurrentHashMap<>();

    @RequestMapping("/{subscriberId}/**")
    public Mono<ResponseEntity<Object>> forwardRequest(@PathVariable String subscriberId) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<ResponseEntity<Object>> completableFuture = new CompletableFuture<>();
        pendingRequests.put(requestId, completableFuture);
        simpMessagingTemplate.convertAndSend(subscriberId, new HttpRequest(requestId, new HashMap<>(), new HashMap<>()));
        return Mono.fromFuture(completableFuture);
    }

    @MessageMapping("topic/{subscriberId}")
    public void forwardResponse(@DestinationVariable String subscriptionId, HttpResponse response) {
        if(subscriptionId == null) return;
        if(response.requestId() == null) return;
        CompletableFuture<ResponseEntity<Object>> completableFuture = pendingRequests.get(response.requestId());
        if(completableFuture == null) return;
        completableFuture.complete(ResponseEntity.status(200).build());
    }
}
