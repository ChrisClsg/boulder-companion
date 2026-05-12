package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.model.User;
import de.clsg.boulder_companion.repository.UserRepository;
import de.clsg.boulder_companion.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto authenticateUser(OAuth2User user) {
        String githubId = (String) user.getAttribute("login");
        String name = (String) user.getAttribute("name");
        String email = (String) user.getAttribute("email");

        User savedUser = userRepository.findByGithubId(githubId)
            .map(u -> u.withRole(User.Role.CLIMBER))
            .orElseGet(() -> User.builder()
                .githubId(githubId)
                .name(name)
                .email(email)
                .role(User.Role.CLIMBER)
                .build());

        return UserDto.fromUser(savedUser);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            return userRepository.findByGithubId((String) oAuth2User.getAttribute("login"))
                .orElseThrow();
        }
        return null;
    }

    public Map<String, Object> getOAuth2User() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            return oAuth2User.getAttributes();
        }
        return null;
    }
}
