package com.github.freddy.services;

import com.github.freddy.dtos.auth.LoginDTO;
import com.github.freddy.dtos.auth.LoginResponseDTO;
import com.github.freddy.dtos.auth.RegisterDTO;
import com.github.freddy.dtos.auth.SignupResponseDTO;
import com.github.freddy.entity.Role;
import com.github.freddy.entity.RoleName;
import com.github.freddy.entity.User;
import com.github.freddy.exceptions.ConflictException;
import com.github.freddy.exceptions.ResourceNotFoundException;
import com.github.freddy.exceptions.UnauthorizedException;
import com.github.freddy.repositories.RoleRepository;
import com.github.freddy.repositories.UserRepository;
import com.github.freddy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider  jwtTokenProvider;


    // Retorna o usuário logado no momento
    public User authenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se a autenticação existe e se o usuário está autenticado
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Usuário não está autenticado");
        }

        // 2. Verifica se o principal é da classe esperada
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            throw new UnauthorizedException("Dados de autenticação inválidos");
        }
        return (User) principal;
    }

    @Transactional
    public SignupResponseDTO createUser(RegisterDTO userDTO) {

        if(userRepository.existsByEmail(userDTO.email())) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setName(userDTO.name());
        user.setEmail(userDTO.email());
        user.setPhone(userDTO.phone());

        //Criptografar a senha antes de salvar
        user.setPassword(passwordEncoder.encode(userDTO.password()));

        Role defaultRole = roleRepository.findByAuthority(RoleName.ROLE_CLIENT)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setRoles(Set.of(defaultRole));

        User savedUser = userRepository.save(user);

        return new SignupResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone()
        );
    }


    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginDTO loginDTO) {

        // Buscar o usuário
        UserDetails user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new AuthenticationException("E-mail ou senha inválidos") {});

        // Validar a senha
        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new AuthenticationException("E-mail ou senha inválidos") {};
        }

        //Gerar os Tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        //Mapear Roles
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Retornar o DTO
        User userEntity = (User) user;
        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                new LoginResponseDTO.UserSummaryDTO(
                        userEntity.getId(),
                        userEntity.getEmail(),
                        roles
                )
        );
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO refreshToken(String refreshToken) {
        // 1. Verificação básica de nulidade
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh Token não enviado");
        }

        try {
            // 2. Extrair o email primeiro (Sem validar a expiração ainda, apenas o parse)
            String email = jwtTokenProvider.getUserEmailFromToken(refreshToken);

            // 3. Buscar o usuário no banco de dados
            UserDetails user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário dono do token não encontrado"));

            // 4. AGORA SIM: Validar o token passando o usuário que acabamos de encontrar
            if (!jwtTokenProvider.validateToken(refreshToken, user)) {
                throw new UnauthorizedException("Refresh Token inválido ou expirado");
            }

            // 5. Se chegou aqui, o token é válido. Geramos o novo par (Access + Refresh)
            return createLoginResponse((User) user);

        } catch (Exception e) {
            // Captura erros de assinatura, expiração ou parse do JWT
            throw new UnauthorizedException("Falha ao processar Refresh Token: " + e.getMessage());
        }
    }

    private LoginResponseDTO createLoginResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                new LoginResponseDTO.UserSummaryDTO(
                        user.getId(),
                        user.getEmail(),
                        roles
                )
        );
    }



}