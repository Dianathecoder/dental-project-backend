package com.dynalar.dynalar.service;


import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
import org.springframework.stereotype.Service;
 
import com.dynalar.dynalar.model.user.User;
 

@Service
public class ChatPermissionService {
 
    private static final Set<String> ALL =
            Set.of("SUPERADMIN", "OWNER", "ADMIN", "DENTIST", "AUXILIAR", "PATIENT");
 
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "SUPERADMIN", ALL,
            "OWNER", ALL,
            "ADMIN", ALL,
            "AUXILIAR", ALL,
            "DENTIST", Set.of("SUPERADMIN", "OWNER", "ADMIN", "AUXILIAR"),
            "PATIENT", Set.of("SUPERADMIN", "OWNER", "ADMIN", "AUXILIAR")
    );
 
    private static final List<String> PRIORITY =
            List.of("SUPERADMIN", "OWNER", "ADMIN", "DENTIST", "AUXILIAR", "PATIENT");

    private static final Set<String> STAFF =
            Set.of("SUPERADMIN", "OWNER", "ADMIN", "DENTIST", "AUXILIAR");
    private static final Set<String> SUPPORT_MANAGERS =
            Set.of("SUPERADMIN", "OWNER", "ADMIN");

    public boolean canChatInScope(User a, User b, ChatScope scope) {
        if (a == null || b == null || a.getId() == null || b.getId() == null
                || a.getId().equals(b.getId()) || scope == null) return false;

        Set<String> rolesA = normalizedRoles(a);
        Set<String> rolesB = normalizedRoles(b);
        if (scope == ChatScope.STAFF) {
            return rolesA.stream().anyMatch(STAFF::contains)
                    && rolesB.stream().anyMatch(STAFF::contains);
        }

        return (rolesA.contains("AUXILIAR") && rolesB.contains("PATIENT"))
                || (rolesB.contains("AUXILIAR") && rolesA.contains("PATIENT"))
                || (rolesA.stream().anyMatch(SUPPORT_MANAGERS::contains) && rolesB.contains("PATIENT"))
                || (rolesB.stream().anyMatch(SUPPORT_MANAGERS::contains) && rolesA.contains("PATIENT"));
    }

    public boolean canUseScope(User user, ChatScope scope) {
        if (user == null || scope == null) return false;
        Set<String> roles = normalizedRoles(user);
        return scope == ChatScope.STAFF
                ? roles.stream().anyMatch(STAFF::contains)
                : roles.contains("AUXILIAR") || roles.contains("PATIENT")
                    || roles.stream().anyMatch(SUPPORT_MANAGERS::contains);
    }

    public boolean isSupportManager(User user) {
        return user != null && normalizedRoles(user).stream().anyMatch(SUPPORT_MANAGERS::contains);
    }

    public boolean hasRole(User user, String role) {
        return user != null && normalizedRoles(user).contains(role);
    }
 
    public boolean canChat(User a, User b) {
        if (a == null || b == null || a.getId() == null || a.getId().equals(b.getId())) return false;
 
        for (String ra : normalizedRoles(a)) {
            for (String rb : normalizedRoles(b)) {
                if (allows(ra, rb) && allows(rb, ra)) return true;
            }
        }
        return false;
    }
 
    public List<String> rolesOf(User u) {
        return List.copyOf(normalizedRoles(u));
    }
 
    public String primaryRole(User u) {
        Set<String> roles = normalizedRoles(u);
        for (String r : PRIORITY) if (roles.contains(r)) return r;
        return null;
    }
 
    private boolean allows(String from, String to) {
        Set<String> allowed = ALLOWED.get(from);
        return allowed != null && allowed.contains(to);
    }
 
    private Set<String> normalizedRoles(User u) {
        Set<String> out = new LinkedHashSet<>();
        if (u.getRoles() == null) return out;
        u.getRoles().forEach(r -> {
            String name = r.name().toUpperCase();
            if (name.startsWith("ROLE_")) name = name.substring(5);
            if (name.equals("DOCTOR")) name = "DENTIST";
            if (ALL.contains(name)) out.add(name);
        });
        return out;
    }
}
 
