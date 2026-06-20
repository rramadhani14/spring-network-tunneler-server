package dev.ramadhani.springnetworktunnelerserver.tunneler.config;


import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/tunneler/ws");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{5000L, 5000L});
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new ChannelInterceptor() {
                    @Override
                    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                        if(accessor == null) return message;
                        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
                            return ChannelInterceptor.super.preSend(message, channel);
                        }
                        else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                            String uuid = UUID.randomUUID().toString();
                            String destination = "/topic/" + uuid;
                            accessor.setDestination(destination);
                            accessor.setSubscriptionId(uuid);
                        } else if(StompCommand.SEND.equals(accessor.getCommand())) {
                            return null;
                        }
                        return ChannelInterceptor.super.preSend(message, channel);
                    }
                }
        );
    }


}