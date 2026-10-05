package com.financefit.financeFit.controllers;

import com.financefit.financeFit.dtos.request.AlterarSenhaRequest;
import com.financefit.financeFit.dtos.request.AtualizarMetaRequest;
import com.financefit.financeFit.dtos.request.UsuarioRequest;
import com.financefit.financeFit.dtos.request.UsuarioUpdateRequest;
import com.financefit.financeFit.dtos.response.ResumoFinanceiroResponse;
import com.financefit.financeFit.dtos.response.UsuarioResponse;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.mappers.UsuarioMapper;
import com.financefit.financeFit.services.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private String emailAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null
                || "anonymousUser".equals(auth.getName())) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return auth.getName();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest dto) {
        Usuario criado = usuarioService.criarUsuario(UsuarioMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponse(criado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscar(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuarioService.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        List<UsuarioResponse> dtos = usuarioService.listarTodos().stream()
                .map(UsuarioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id,
            @Valid @RequestBody UsuarioUpdateRequest dto) {
        Usuario atualizado = usuarioService.atualizarUsuario(id, UsuarioMapper.toEntity(dto));
        return ResponseEntity.ok(UsuarioMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<UsuarioResponse> alterarSenha(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id,
            @Valid @RequestBody AlterarSenhaRequest body) {
        Usuario atualizado = usuarioService.alterarSenha(id, body.novaSenha());
        return ResponseEntity.ok(UsuarioMapper.toResponse(atualizado));
    }

    @PatchMapping("/{id}/meta")
    public ResponseEntity<UsuarioResponse> atualizarMeta(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id,
            @Valid @RequestBody AtualizarMetaRequest body) {
        Usuario atualizado = usuarioService.atualizarMeta(id, body.metaMensal());
        return ResponseEntity.ok(UsuarioMapper.toResponse(atualizado));
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<ResumoFinanceiroResponse> resumoFinanceiro(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        return ResponseEntity.ok(usuarioService.resumoFinanceiroDetalhado(id));
    }

    @GetMapping("/{id}/resumo/{mes}/{ano}")
    public ResponseEntity<ResumoFinanceiroResponse> resumoFinanceiroPeriodo(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id,
            @PathVariable @Min(value = 1, message = "Mês deve estar entre 1 e 12")
            @Max(value = 12, message = "Mês deve estar entre 1 e 12") Integer mes,
            @PathVariable @Min(value = 2000, message = "Ano inválido")
            @Max(value = 2100, message = "Ano inválido") Integer ano) {
        return ResponseEntity.ok(usuarioService.resumoFinanceiroDetalhado(id, mes, ano));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me() {
        String email = emailAutenticado();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarMePut(
            @Valid @RequestBody UsuarioUpdateRequest update) {
        return processarAtualizarMe(update);
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarMePatch(
            @RequestBody UsuarioUpdateRequest update) {
        return processarAtualizarMe(update);
    }

    private ResponseEntity<UsuarioResponse> processarAtualizarMe(UsuarioUpdateRequest update) {
        String email = emailAutenticado();
        Usuario atualizado = usuarioService.atualizarUsuarioPorEmail(
                email, update.nome(), update.senha(), update.metaMensal());
        return ResponseEntity.ok(UsuarioMapper.toResponse(atualizado));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarMe() {
        String email = emailAutenticado();
        usuarioService.deletarUsuarioPorEmail(email);
        return ResponseEntity.noContent().build();
    }
}
