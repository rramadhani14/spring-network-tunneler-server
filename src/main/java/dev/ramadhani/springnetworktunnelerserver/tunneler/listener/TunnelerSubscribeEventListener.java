package dev.ramadhani.springnetworktunnelerserver.tunneler.listener;


import dev.ramadhani.springnetworktunnelerserver.tunneler.model.SubscribedMessage;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.HashMap;

@Component
@AllArgsConstructor
public class TunnelerSubscribeEventListener {
    private SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void processSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        if(accessor == null) return;
        String destination = accessor.getDestination();
        if(destination == null) return;
        String path = destination.replace("/topic/", "");
        Object portObj = accessor.getHeader("port");
        if(!(portObj instanceof Integer)) return;
        simpMessagingTemplate.convertAndSend(destination, new SubscribedMessage(path, (Integer) portObj, new HashMap<>()));
    }
}
