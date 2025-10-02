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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Autenticación", description = "Endponint para autenticación y registro de usuarios")
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

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario y devuelve un token JWT para acceder a los endpoints protegidos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "token": "eyJhbGciOiJIUzUxMiJ9...",
                        "type": "Bearer",
                        "id": 1,
                        "username": "admin",
                        "email": "admin@sistema.com",
                        "roles": ["ROLE_ADMIN"]
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                        "status": "Unauthorized",
                        "statusCode": 401,
                        "message": "Credenciales inválidas",
                        "timestamp": "04-06-2025 12:30:45",
                        "path": "/api/auth/login"
                    }
                    """
                            )
                    )
            )
    })

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales de usuario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "username": "admin",
                            "password": "admin123"
                        }
                        """
                            )
                    )
            )
            @Valid @RequestBody LoginRequest loginRequest) {

        // 1. AUTENTICAR usuario con username/password
        Authentication authentication = authenticationManager.
                authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.
                        getUsername(), loginRequest.getPassword()));

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

    @Operation(
            summary = "Registrar usuario",
            description = "Registra un nuevo usuario en el sistema con los roles especificados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "message": "Usuario registrado exitosamente"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en los datos proporcionados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                        "message": "Error: El nombre de usuario ya está en uso"
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignupRequest.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "nombre": "Juan Pérez",
                            "username": "juan.perez",
                            "email": "juan.perez@empresa.com",
                            "password": "password123",
                            "roles": ["rrhh"]
                        }
                        """
                            )
                    )
            )
            @Valid @RequestBody SignupRequest signupRequest) {
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

