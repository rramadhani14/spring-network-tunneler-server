package dev.ramadhani.springnetworktunnelerserver.tunneler.model;


public record SubscribedMessage(
        String connectionId,
        Integer port,
        Object config
) {
}
