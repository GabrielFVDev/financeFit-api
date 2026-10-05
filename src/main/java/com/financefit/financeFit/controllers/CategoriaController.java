package com.financefit.financeFit.controllers;

import com.financefit.financeFit.dtos.request.CategoriaRequest;
import com.financefit.financeFit.dtos.response.CategoriaResponse;
import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.mappers.CategoriaMapper;
import com.financefit.financeFit.services.CategoriaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@Validated
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest dto) {
        Categoria salva = categoriaService.salvar(CategoriaMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaMapper.toResponse(salva));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarTodas() {
        List<CategoriaResponse> lista = categoriaService.listarTodas()
                .stream()
                .map(CategoriaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        return ResponseEntity.ok(CategoriaMapper.toResponse(categoriaService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable @Positive(message = "ID deve ser positivo") Long id,
            @Valid @RequestBody CategoriaRequest dto) {
        Categoria atualizada = categoriaService.atualizar(id, CategoriaMapper.toEntity(dto));
        return ResponseEntity.ok(CategoriaMapper.toResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable @Positive(message = "ID deve ser positivo") Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
