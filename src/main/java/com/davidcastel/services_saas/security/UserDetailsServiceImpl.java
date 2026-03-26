package com.davidcastel.services_saas.security;

import com.davidcastel.services_saas.user.User;
import com.davidcastel.services_saas.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Esta clase implementa la interfaz UserDetailsService de Spring Security.
 * Se encarga de cargar los datos del usuario desde la base de datos cuando se necesita autenticar.
 * Es usada por AuthenticationManager para validar las credenciales del usuario.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Convención Spring: roles suelen ir como "ROLE_ADMIN", "ROLE_USER"
        String role = "ROLE_" + user.getRole().name();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .disabled(!user.isEnabled())
                .build();
    }

}