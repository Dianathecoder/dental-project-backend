package com.dynalar.dynalar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.dynalar.dynalar.dto.chat.ChatDtos.ContactDTO;
import com.dynalar.dynalar.dto.chat.ChatDtos.ConversationDTO;
import com.dynalar.dynalar.dto.chat.ChatDtos.MessageDTO;
import com.dynalar.dynalar.model.chatmodel.DirectConversation;
import com.dynalar.dynalar.model.chatmodel.DirectMessage;
import com.dynalar.dynalar.model.patient.Patient;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.DirectConversationRepository;
import com.dynalar.dynalar.respository.DirectMessageRepository;
import com.dynalar.dynalar.respository.PatientRepository;
import com.dynalar.dynalar.respository.UserRepository;

@Service
public class ChatService {

    private static final int MAX_LEN = 2000;

    private final UserRepository userRepo;
    private final DirectConversationRepository convRepo;
    private final DirectMessageRepository msgRepo;
    private final PatientRepository patientRepo;
    private final ChatPermissionService perm;

    public ChatService(UserRepository userRepo,
                       DirectConversationRepository convRepo,
                       DirectMessageRepository msgRepo,
                       PatientRepository patientRepo,
                       ChatPermissionService perm) {
        this.userRepo = userRepo;
        this.convRepo = convRepo;
        this.msgRepo = msgRepo;
        this.patientRepo = patientRepo;
        this.perm = perm;
    }

    // ------------------------------------------------------------------ contactos

    @Transactional(readOnly = true)
    public List<ContactDTO> contacts(User me, String q, ChatScope scope) {
        requireScope(me, scope);
        String needle = q == null ? "" : q.trim().toLowerCase();

        Map<Long, DirectConversation> byOther = new HashMap<>();
        if (scope == ChatScope.SUPPORT && perm.isSupportManager(me)) {
            Comparator<DirectConversation> activity = Comparator
                    .comparing(DirectConversation::getLastMessageAt,
                            Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(DirectConversation::getCreatedAt,
                            Comparator.nullsFirst(Comparator.naturalOrder()));
            for (DirectConversation c : convRepo.findAll()) {
                User patientUser = supportPatient(c);
                User auxiliary = supportAuxiliary(c);
                if (patientUser == null || auxiliary == null) continue;
                byOther.merge(patientUser.getId(), c,
                        (previous, candidate) -> activity.compare(candidate, previous) > 0 ? candidate : previous);
            }
        } else {
            for (DirectConversation c : convRepo.findAllByUser(me.getId())) {
                Long otherId = c.getUserLow().getId().equals(me.getId())
                        ? c.getUserHigh().getId() : c.getUserLow().getId();
                byOther.put(otherId, c);
            }
        }

        Comparator<ContactDTO> byDate = Comparator.comparing(
                ContactDTO::lastMessageAt,
                Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder()));
        Comparator<ContactDTO> byName = Comparator.comparing(
                ContactDTO::name,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

        return userRepo.findAll().stream()
                .filter(u -> perm.canChatInScope(me, u, scope))
                .filter(u -> scope != ChatScope.SUPPORT || !perm.isSupportManager(me) || byOther.containsKey(u.getId()))
                .filter(u -> needle.isEmpty() || fullName(u).toLowerCase().contains(needle))
                .map(u -> toContact(me, u, byOther.get(u.getId())))
                .sorted(byDate.thenComparing(byName))
                .toList();
    }

    // ------------------------------------------------------------------ leer

    /** afterId == null -> últimos 100 mensajes. afterId != null -> solo los nuevos (polling). */
    @Transactional
    public ConversationDTO open(User me, Long otherId, Long afterId, ChatScope scope) {
        User other = loadAllowed(me, otherId, scope);
        DirectConversation conv = conversationFor(me, other, scope);

        List<DirectMessage> msgs;
        if (afterId == null) {
            msgs = new ArrayList<>(msgRepo.findTop100ByConversationIdOrderByIdDesc(conv.getId()));
            Collections.reverse(msgs);
        } else {
            msgs = msgRepo.findByConversationIdAndIdGreaterThanOrderByIdAsc(conv.getId(), afterId);
        }

        // Estoy mirando la pantalla -> lo que me han enviado queda leído
        msgRepo.markRead(conv.getId(), me.getId());

        ContactDTO contact = toContact(me, other, conv);
        return new ConversationDTO(
                new ContactDTO(contact.id(), contact.name(), contact.surname(), contact.avatarUrl(),
                        contact.role(), contact.roles(), contact.lastMessage(), contact.lastMessageAt(), 0,
                        contact.patientId()),
                msgs.stream().map(m -> toDto(m, me)).toList());
    }

    // ------------------------------------------------------------------ enviar

    @Transactional
    public MessageDTO send(User me, Long otherId, String text, ChatScope scope) {
        String clean = text == null ? "" : text.trim();
        if (clean.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensaje vacío");
        if (clean.length() > MAX_LEN)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensaje demasiado largo");

        User other = loadAllowed(me, otherId, scope);
        DirectConversation conv = conversationFor(me, other, scope);

        DirectMessage m = new DirectMessage();
        m.setConversation(conv);
        m.setSender(me);
        m.setMessage(clean);
        m = msgRepo.save(m);

        conv.setLastMessageAt(LocalDateTime.now());
        conv.setLastMessagePreview(clean.length() > 120 ? clean.substring(0, 120) : clean);
        convRepo.save(conv);

        return toDto(m, me);
    }

    @Transactional(readOnly = true)
    public long unreadCount(User me) {
        return msgRepo.countAllUnread(me.getId());
    }

    // ------------------------------------------------------------------ helpers

    public User requireAllowed(User me, Long otherId, ChatScope scope) {
        requireScope(me, scope);
        User other = userRepo.findById(otherId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!perm.canChatInScope(me, other, scope))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para chatear en esta bandeja");
        return other;
    }

    private User loadAllowed(User me, Long otherId, ChatScope scope) {
        return requireAllowed(me, otherId, scope);
    }

    private void requireScope(User me, ChatScope scope) {
        if (!perm.canUseScope(me, scope))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a esta bandeja");
    }

    private DirectConversation getOrCreate(User a, User b) {
        User low = a.getId() < b.getId() ? a : b;
        User high = (low == a) ? b : a;
        return convRepo.findByUserLowIdAndUserHighId(low.getId(), high.getId()).orElseGet(() -> {
            DirectConversation c = new DirectConversation();
            c.setUserLow(low);
            c.setUserHigh(high);
            return convRepo.save(c);
        });
    }

    private ContactDTO toContact(User me, User u, DirectConversation conv) {
        long unread = conv == null ? 0 : msgRepo.countUnread(conv.getId(), me.getId());
        return new ContactDTO(
                u.getId(), u.getName(), u.getSurname(),
                u.getAvatarUrl(),               // ajusta si tu User lo llama distinto
                perm.primaryRole(u), perm.rolesOf(u),
                conv == null ? null : conv.getLastMessagePreview(),
                conv == null ? null : conv.getLastMessageAt(),
                unread,
                perm.hasRole(u, "PATIENT")
                        ? patientRepo.findByUser_Id(u.getId()).map(Patient::getId).orElse(null)
                        : null);
    }

    private DirectConversation conversationFor(User me, User other, ChatScope scope) {
        if (scope == ChatScope.SUPPORT && perm.isSupportManager(me)) {
            return convRepo.findAllByUser(other.getId()).stream()
                    .filter(c -> supportAuxiliary(c) != null)
                    .max(Comparator
                            .comparing(DirectConversation::getLastMessageAt,
                                    Comparator.nullsFirst(Comparator.naturalOrder()))
                            .thenComparing(DirectConversation::getCreatedAt,
                                    Comparator.nullsFirst(Comparator.naturalOrder())))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "El cliente aún no tiene una conversación de soporte"));
        }
        return getOrCreate(me, other);
    }

    private User supportPatient(DirectConversation conversation) {
        if (perm.hasRole(conversation.getUserLow(), "PATIENT")) return conversation.getUserLow();
        if (perm.hasRole(conversation.getUserHigh(), "PATIENT")) return conversation.getUserHigh();
        return null;
    }

    private User supportAuxiliary(DirectConversation conversation) {
        if (perm.hasRole(conversation.getUserLow(), "AUXILIAR")) return conversation.getUserLow();
        if (perm.hasRole(conversation.getUserHigh(), "AUXILIAR")) return conversation.getUserHigh();
        return null;
    }

    private MessageDTO toDto(DirectMessage m, User me) {
        Long senderId = m.getSender().getId();
        return new MessageDTO(m.getId(), senderId, m.getMessage(), m.getCreatedAt(),
                senderId.equals(me.getId()), m.isRead());
    }

    private String fullName(User u) {
        return (u.getName() == null ? "" : u.getName()) + " " + (u.getSurname() == null ? "" : u.getSurname());
    }
}
