package com.dynalar.dynalar.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.dynalar.dynalar.dto.chat.ChatDtos.MessageDTO;
import com.dynalar.dynalar.dto.chat.ChatDtos.SendMessageRequest;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.UserRepository;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (value, type, context) -> new JsonPrimitive(value.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (value, type, context) -> LocalDateTime.parse(value.getAsString()))
            .create();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ChatService chatService,
                                UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession rawSession) throws Exception {
        try {
            String[] parts = rawSession.getUri().getPath().split("/");
            if (parts.length != 5 || !"ws".equals(parts[1]) || !"chat".equals(parts[2])) {
                rawSession.close(CloseStatus.BAD_DATA);
                return;
            }

            ChatScope scope = ChatScope.valueOf(parts[3].toUpperCase());
            Long peerId = Long.valueOf(parts[4]);
            String email = (String) rawSession.getAttributes().get("email");
            User me = userRepository.findByEmail(email).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            chatService.requireAllowed(me, peerId, scope);

            rawSession.getAttributes().put("senderId", me.getId());
            rawSession.getAttributes().put("peerId", peerId);
            rawSession.getAttributes().put("scope", scope.name());
            WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(rawSession, 10_000, 512 * 1024);
            sessions.put(rawSession.getId(), safeSession);
        } catch (Exception e) {
            log.warn("Could not establish chat WebSocket session", e);
            rawSession.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Long senderId = (Long) session.getAttributes().get("senderId");
            Long peerId = (Long) session.getAttributes().get("peerId");
            ChatScope scope = ChatScope.valueOf((String) session.getAttributes().get("scope"));
            String email = (String) session.getAttributes().get("email");
            User sender = userRepository.findByEmail(email).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            if (!sender.getId().equals(senderId)) {
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            SendMessageRequest request = gson.fromJson(message.getPayload(), SendMessageRequest.class);
            MessageDTO saved = chatService.send(sender, peerId, request.text(), scope);
            broadcast(senderId, peerId, scope, saved);
        } catch (Exception e) {
            log.warn("Rejected chat WebSocket message", e);
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    private void broadcast(Long senderId, Long peerId, ChatScope scope, MessageDTO message) {
        for (WebSocketSession target : sessions.values()) {
            Long targetId = (Long) target.getAttributes().get("senderId");
            Long targetPeerId = (Long) target.getAttributes().get("peerId");
            String targetScope = (String) target.getAttributes().get("scope");
            if (targetId == null || targetPeerId == null || !scope.name().equals(targetScope)) continue;

            boolean belongsToConversation = scope == ChatScope.SUPPORT
                    ? targetId.equals(peerId) || targetPeerId.equals(peerId)
                    : (targetId.equals(senderId) && targetPeerId.equals(peerId))
                        || (targetId.equals(peerId) && targetPeerId.equals(senderId));
            if (!belongsToConversation || !target.isOpen()) continue;

            boolean mineForTarget = targetId.equals(message.senderId());
            MessageDTO outgoing = new MessageDTO(message.id(), message.senderId(), message.text(),
                    message.createdAt(), mineForTarget, message.read());
            try {
                target.sendMessage(new TextMessage(gson.toJson(outgoing)));
            } catch (Exception e) {
                log.warn("Could not deliver chat message to WebSocket session {}", target.getId(), e);
                try {
                    target.close(CloseStatus.SERVER_ERROR);
                } catch (IOException closeError) {
                    log.debug("WebSocket session {} was already closed", target.getId(), closeError);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }
}
