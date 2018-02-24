package org.javawebsocket.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import org.javawebsocket.model.Chat;

@ApplicationScoped
public class ChatSessionHandler {
    private int chatId = 0;
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Chat> chats = new HashSet<>();
    public void addSession(Session session) {
        sessions.add(session);
        chats.stream().map((chat) -> createAddMessage(chat)).forEachOrdered((addMessage) -> {
            sendToSession(session, addMessage);
        });

    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }
    public List<Chat> getChats() {
        return new ArrayList<>(chats);
    }

    public void addChat(Chat chat) {
         chat.setId(chatId);
        chats.add(chat);
        chatId++;
        JsonObject addMessage = createAddMessage(chat);
        sendToAllConnectedSessions(addMessage);
    }

    public void removeChat(int id) {
        Chat chat = getChatById(id);
        if (chat != null) {
            chats.remove(chat);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToAllConnectedSessions(removeMessage);
        }
    }

    public void toggleChat(int id) {
        JsonProvider provider = JsonProvider.provider();
        Chat chat = getChatById(id);
        if (chat != null) {
            if ("On".equals(chat.getStatus())) {
                chat.setStatus("Off");
            } else {
                chat.setStatus("On");
            }
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "toggle")
                    .add("id", chat.getId())
                    .add("status", chat.getStatus())
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
        }
    }
    private Chat getChatById(int id) {
        for (Chat chat : chats) {
            if (chat.getId() == id) {
                return chat;
            }
        }
        return null;
    }

    private JsonObject createAddMessage(Chat chat) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")
                .add("id", chat.getId())
                .add("message", chat.getMessage())
                .add("timestamp", chat.getTimestamp())
                .add("status", chat.getStatus())
                .add("username", chat.getUsername())
                .build();
        return addMessage;
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(ChatSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   
 
}