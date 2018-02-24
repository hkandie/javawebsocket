package org.javawebsocket.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.javawebsocket.model.Chat;
import org.javawebsocket.model.Chat;

@ApplicationScoped
@ServerEndpoint("/actions")
public class ChatWebSocketServer {

    @OnOpen
    public void open(Session session) {
        sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(ChatWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            if ("add".equals(jsonMessage.getString("action"))) {
                Chat chat = new Chat();
                chat.setMessage(jsonMessage.getString("message"));
                chat.setTimestamp(jsonMessage.getString("timestamp"));
                chat.setUsername(jsonMessage.getString("username"));
                chat.setStatus("Off");
                sessionHandler.addChat(chat);
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.removeChat(id);
            }

            if ("toggle".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.toggleChat(id);
            }
        }
    }
    @Inject
    private ChatSessionHandler sessionHandler;
 
}
