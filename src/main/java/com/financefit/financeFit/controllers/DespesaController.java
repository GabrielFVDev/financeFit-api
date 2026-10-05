package com.financefit.financeFit.controllers;

import com.financefit.financeFit.dtos.request.DespesaRequest;
import com.financefit.financeFit.dtos.response.DespesaResponse;
import com.financefit.financeFit.entities.Despesa;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.mappers.DespesaMapper;
import com.financefit.financeFit.services.DespesaService;
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
@RequestMapping("/despesas")
@Validated
public class DespesaController {

    private final DespesaService despesaService;
    private final UsuarioService usuarioService;

    public DespesaController(DespesaService despesaService, UsuarioService usuarioService) {
        this.despesaService = despesaService;
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

    private void garantirDono(Despesa despesa, Long usuarioId) {
        if (despesa.getUsuario() == null || !usuarioId.equals(despesa.getUsuario().getUserId())) {
            throw new AccessDeniedException("Acesso negado para este recurso");
        }
    }


    @PostMapping
    public ResponseEntity<DespesaResponse> criar(@Valid @RequestBody DespesaRequest dto) {
        Long usuarioId = usuarioAutenticadoId();
        Despesa criada = despesaService.salvar(DespesaMapper.toEntity(dto), usuarioId, dto.idCategoria());
        return ResponseEntity.status(HttpStatus.CREATED).body(DespesaMapper.toResponse(criada));
    }

    @GetMapping
    public ResponseEntity<List<DespesaResponse>> listarMinhas() {
        Long usuarioId = usuarioAutenticadoId();
        List<DespesaResponse> dtos = despesaService.listar(usuarioId)
                .stream()
                .map(DespesaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<DespesaResponse>> listarPorUsuario(
            @PathVariable @Positive(message = "ID do usuário deve ser positivo") Long idUsuario) {
        Long usuarioId = usuarioAutenticadoId();
        if (!usuarioId.equals(idUsuario)) {
            throw new AccessDeniedException("Acesso negado para este recurso");
        }
        List<DespesaResponse> dtos = despesaService.listar(idUsuario)
                .stream()
                .map(DespesaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespesaResponse> buscarPorId(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        Long usuarioId = usuarioAutenticadoId();
        Despesa despesa = despesaService.buscarPorId(id);
        garantirDono(despesa, usuarioId);
        return ResponseEntity.ok(DespesaMapper.toResponse(despesa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaResponse> atualizar(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id,
            @Valid @RequestBody DespesaRequest dto) {
        Long usuarioId = usuarioAutenticadoId();
        garantirDono(despesaService.buscarPorId(id), usuarioId);
        Despesa atualizada = despesaService.atualizar(id, DespesaMapper.toEntity(dto), usuarioId, dto.idCategoria());
        return ResponseEntity.ok(DespesaMapper.toResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        Long usuarioId = usuarioAutenticadoId();
        garantirDono(despesaService.buscarPorId(id), usuarioId);
        despesaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
