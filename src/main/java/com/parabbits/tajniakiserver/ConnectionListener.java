package com.parabbits.tajniakiserver;


import com.parabbits.tajniakiserver.game.GameConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
public class ConnectionListener {

    @Autowired
    private GameConnector gameConnector;
    // TODO: przemyśleć, jak to powinno być. Być może powinna być jeszcze jakaś warstwa pośrednia, która dodaje do gry tylko połączenia, jeżeli trwa rozgrywka

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event){
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Połączono " + sessionId);
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event){
        // TODO: zrobić usuwanie
        // TODO: usunięcie
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        gameConnector.disconnectPlayer(sessionId);
        System.out.println("Rozłączono");
    }
}