package com.financefit.financeFit.controllers;

import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria) {
        try {
            Categoria criada = categoriaService.salvar(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar categoria: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodas() {
        try {
            List<Categoria> categorias = categoriaService.listarTodas();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar categorias: " + e.getMessage());
        }
    }
}
