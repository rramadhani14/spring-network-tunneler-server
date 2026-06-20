package dev.ramadhani.springnetworktunnelerserver.tunneler.model;

import java.util.Map;

public record HttpResponse(
        String requestId,
        int status,
        Map<String, String> header,
        Object body
) {
}
