package com.example.sistemareportesincidentes.security;

import com.example.sistemareportesincidentes.entity.Usuario;
import com.example.sistemareportesincidentes.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // UserDetailsService (Carga usuario desde BD)
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. BUSCAR usuario en base de datos
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        // 2. CONVERTIR Usuario → UserDetails usando el adaptador UserDetailsImpl
        return UserDetailsImpl.build(usuario); //es un metodo estatico
    }
}
