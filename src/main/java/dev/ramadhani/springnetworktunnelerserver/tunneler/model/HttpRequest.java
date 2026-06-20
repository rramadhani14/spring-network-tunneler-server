package dev.ramadhani.springnetworktunnelerserver.tunneler.model;

import java.util.Map;

public record HttpRequest(
        String requestId,
        Map<String, String> headers,
        Object body

) {
}
