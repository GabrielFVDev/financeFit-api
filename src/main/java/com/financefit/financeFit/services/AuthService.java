package com.financefit.financeFit.services;

import com.financefit.financeFit.dtos.AuthResponseDTO;
import com.financefit.financeFit.dtos.LoginDTO;
import com.financefit.financeFit.dtos.RegisterDTO;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.BusinessException;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.repositories.UsuarioRepository;
import com.financefit.financeFit.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public AuthResponseDTO register(RegisterDTO registerDTO) {
        if (usuarioRepository.findByEmail(registerDTO.email()).isPresent()) {
            throw new BusinessException("Email já cadastrado: " + registerDTO.email());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(registerDTO.nome());
        usuario.setEmail(registerDTO.email());
        usuario.setSenha(passwordEncoder.encode(registerDTO.senha()));
        usuario.setDataCriacao(LocalDate.now());
        usuario.setMetaMensal(registerDTO.metaMensal() != null ? registerDTO.metaMensal() : 0.0);

        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome());
    }

    public AuthResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.senha())
        );

        Usuario usuario = usuarioRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + loginDTO.email()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome());
    }
}
