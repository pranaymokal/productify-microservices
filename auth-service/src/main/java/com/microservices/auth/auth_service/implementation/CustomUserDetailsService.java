package com.microservices.auth.auth_service.implementation;

import com.microservices.auth.auth_service.response.UserResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final RestClient restClient;

    public CustomUserDetailsService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://USER-SERVICE").build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserResponse userResponse = restClient.get()
                    .uri("/user-service/v1/users/{username}", username)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new UsernameNotFoundException("User not found: " + username);
                    })
                    .body(UserResponse.class);

            if (userResponse == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            List<SimpleGrantedAuthority> authorities = userResponse.getRoles().stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    userResponse.getUsername(),
                    userResponse.getPassword(),
                    authorities
            );
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UsernameNotFoundException("Failed to fetch user details for: " + username, e);
        }
    }
}
