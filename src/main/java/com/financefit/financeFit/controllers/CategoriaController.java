package com.financefit.financeFit.controllers;

import com.financefit.financeFit.dtos.CategoriaDTO;
import com.financefit.financeFit.dtos.CreateCategoriaDTO;
import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaDTO> criar(@Valid @RequestBody CreateCategoriaDTO createCategoriaDTO) {
        try {
            Categoria categoria = new Categoria();
            BeanUtils.copyProperties(createCategoriaDTO, categoria);
            Categoria criada = categoriaService.salvar(categoria);
            CategoriaDTO categoriaDTO = new CategoriaDTO();
            BeanUtils.copyProperties(criada, categoriaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaDTO);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar categoria: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        try {
            List<Categoria> categorias = categoriaService.listarTodas();
            List<CategoriaDTO> categoriaDTOs = categorias.stream().map(categoria -> {
                CategoriaDTO dto = new CategoriaDTO();
                BeanUtils.copyProperties(categoria, dto);
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(categoriaDTOs);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar categorias: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CreateCategoriaDTO createCategoriaDTO) { // Alterado para Long
        try {
            Categoria categoria = new Categoria();
            BeanUtils.copyProperties(createCategoriaDTO, categoria);
            Categoria atualizada = categoriaService.atualizar(id, categoria);
            CategoriaDTO categoriaDTO = new CategoriaDTO();
            BeanUtils.copyProperties(atualizada, categoriaDTO);
            return ResponseEntity.ok(categoriaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar categoria: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) { // Alterado para Long
        try {
            categoriaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar categoria: " + e.getMessage());
        }
    }
}
