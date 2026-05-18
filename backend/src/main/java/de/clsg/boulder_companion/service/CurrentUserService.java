package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oauth2User)) {
            throw new IllegalStateException("Authenticated principal is not an OAuth2 user");
        }

        Object githubIdAttribute = oauth2User.getAttribute("id");

        if (githubIdAttribute == null) {
            throw new IllegalStateException("OAuth2 user id is missing");
        }

        String githubId = String.valueOf(githubIdAttribute);

        return userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalStateException("Current user not found"))
                .id();
    }
}
