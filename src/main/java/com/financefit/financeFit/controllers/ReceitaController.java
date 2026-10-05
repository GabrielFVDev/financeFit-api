package com.financefit.financeFit.controllers;

import com.financefit.financeFit.dtos.request.ReceitaRequest;
import com.financefit.financeFit.dtos.response.ReceitaResponse;
import com.financefit.financeFit.entities.Receita;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.mappers.ReceitaMapper;
import com.financefit.financeFit.services.ReceitaService;
import com.financefit.financeFit.services.UsuarioService;
import jakarta.validation.Valid;
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
@RequestMapping("/receitas")
@Validated
public class ReceitaController {

    private final ReceitaService receitaService;
    private final UsuarioService usuarioService;

    public ReceitaController(ReceitaService receitaService, UsuarioService usuarioService) {
        this.receitaService = receitaService;
        this.usuarioService = usuarioService;
    }

    private Long usuarioAutenticadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
        return usuario.getUserId();
    }

    private void garantirDono(Receita receita, Long usuarioId) {
        if (receita.getUsuario() == null || !usuarioId.equals(receita.getUsuario().getUserId())) {
            throw new AccessDeniedException("Acesso negado para este recurso");
        }
    }


    @PostMapping
    public ResponseEntity<ReceitaResponse> criar(@Valid @RequestBody ReceitaRequest dto) {
        Long usuarioId = usuarioAutenticadoId();
        Receita criada = receitaService.salvar(ReceitaMapper.toEntity(dto), usuarioId, dto.idCategoria());
        return ResponseEntity.status(HttpStatus.CREATED).body(ReceitaMapper.toResponse(criada));
    }

    @GetMapping
    public ResponseEntity<List<ReceitaResponse>> listarMinhas() {
        Long usuarioId = usuarioAutenticadoId();
        List<ReceitaResponse> dtos = receitaService.listar(usuarioId)
                .stream()
                .map(ReceitaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReceitaResponse>> listarPorUsuario(
            @PathVariable @Positive(message = "ID do usuário deve ser positivo") Long idUsuario) {
        Long usuarioId = usuarioAutenticadoId();
        if (!usuarioId.equals(idUsuario)) {
            throw new AccessDeniedException("Acesso negado para este recurso");
        }
        List<ReceitaResponse> dtos = receitaService.listar(idUsuario)
                .stream()
                .map(ReceitaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaResponse> buscarPorId(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        Long usuarioId = usuarioAutenticadoId();
        Receita receita = receitaService.buscarPorId(id);
        garantirDono(receita, usuarioId);
        return ResponseEntity.ok(ReceitaMapper.toResponse(receita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaResponse> atualizar(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id,
            @Valid @RequestBody ReceitaRequest dto) {
        Long usuarioId = usuarioAutenticadoId();
        garantirDono(receitaService.buscarPorId(id), usuarioId);
        Receita atualizada = receitaService.atualizar(id, ReceitaMapper.toEntity(dto), usuarioId, dto.idCategoria());
        return ResponseEntity.ok(ReceitaMapper.toResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        Long usuarioId = usuarioAutenticadoId();
        garantirDono(receitaService.buscarPorId(id), usuarioId);
        receitaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
