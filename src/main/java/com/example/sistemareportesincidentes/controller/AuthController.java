package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.auth.JwtResponse;
import com.example.sistemareportesincidentes.dto.auth.LoginRequest;
import com.example.sistemareportesincidentes.dto.auth.MessageResponse;
import com.example.sistemareportesincidentes.dto.auth.SignupRequest;
import com.example.sistemareportesincidentes.entity.Rol;
import com.example.sistemareportesincidentes.entity.Usuario;
import com.example.sistemareportesincidentes.repository.RolRepository;
import com.example.sistemareportesincidentes.repository.UsuarioRepository;
import com.example.sistemareportesincidentes.security.UserDetailsImpl;
import com.example.sistemareportesincidentes.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. AUTENTICAR usuario con username/password
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. ESTABLECER autenticación en contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. GENERAR JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. OBTENER detalles del usuario autenticado
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 5. RETORNAR respuesta con token
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        // 1. VALIDAR que username no exista
        if (usuarioRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El nombre de usuario ya está en uso"));
        }

        // 1. VALIDAR que email no exista
        if (usuarioRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso"));
        }

        // 3. CREAR nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(signupRequest.getNombre());
        usuario.setUsername(signupRequest.getUsername());
        usuario.setEmail(signupRequest.getEmail());
        usuario.setPassword(encoder.encode(signupRequest.getPassword()));

        // 4. ASIGNAR roles
        Set<String> strRoles = signupRequest.getRoles();
        Set<Rol> roles = new HashSet<>();

        if (strRoles == null) {
            // Rol por defecto
            Rol userRole = rolRepository.findByNombre(Rol.nombreRol.ROLE_MESA_AYUDA)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
            roles.add(userRole);
        } else {
            strRoles.forEach(rol -> {
                switch (rol) {
                    case "admin":
                        Rol adminRol = rolRepository.findByNombre(Rol.nombreRol.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
                        roles.add(adminRol);
                        break;
                    case "rrhh":
                        Rol rrhhRol = rolRepository.findByNombre(Rol.nombreRol.ROLE_RRHH)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
                        roles.add(rrhhRol);
                        break;
                    case "comercial":
                        Rol comercialRol = rolRepository.findByNombre(Rol.nombreRol.ROLE_COMERCIAL)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
                        roles.add(comercialRol);
                        break;
                        default:
                            Rol mesaAyuda = rolRepository.findByNombre(Rol.nombreRol.ROLE_MESA_AYUDA)
                                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
                            roles.add(mesaAyuda);
                }
            });
        }
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente"));
    }
}

