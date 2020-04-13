package com.parabbits.tajniakiserver;

import com.parabbits.tajniakiserver.game.GameController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
public class ConnectionListener {

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event){
        GameController.sessions.add(event.getMessage().getHeaders().get("simpSessionId").toString());
        System.out.println("Połączono");
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event){
        System.out.println("Rozłączono");
    }
}