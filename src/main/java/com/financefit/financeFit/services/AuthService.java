package com.financefit.financeFit.services;

import com.financefit.financeFit.dtos.AuthResponseDTO;
import com.financefit.financeFit.dtos.LoginDTO;
import com.financefit.financeFit.dtos.RegisterDTO;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.repositories.UsuarioRepository;
import com.financefit.financeFit.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthResponseDTO register(RegisterDTO registerDTO) {
        // Verificar se o email já existe
        if (usuarioRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Criar novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(registerDTO.getNome());
        usuario.setEmail(registerDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(registerDTO.getSenha()));
        usuario.setDataCriacao(LocalDate.now());
        usuario.setMetaMensal(registerDTO.getMetaMensal());

        usuarioRepository.save(usuario);

        // Gerar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome());
    }

    public AuthResponseDTO login(LoginDTO loginDTO) {
        // Autenticar usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
        );

        // Buscar usuário
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gerar token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome());
    }
}

