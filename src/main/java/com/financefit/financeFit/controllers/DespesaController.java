package com.financefit.financeFit.controllers;

import com.financefit.financeFit.entities.Despesa;
import com.financefit.financeFit.services.DespesaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/despesas")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;

    @PostMapping
    public ResponseEntity<Despesa> criar(
            @Valid @RequestBody Despesa despesa,
            @RequestParam int idUsuario,
            @RequestParam int idCategoria) {
        try {
            if (idUsuario <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }
            if (idCategoria <= 0) {
                throw new IllegalArgumentException("ID da categoria inválido");
            }
            Despesa criada = despesaService.salvar(despesa, idUsuario, idCategoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar despesa: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Despesa>> listarPorUsuario(@PathVariable int idUsuario) {
        try {
            if (idUsuario <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }
            List<Despesa> despesas = despesaService.listar(idUsuario);
            return ResponseEntity.ok(despesas);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar despesas do usuário: " + e.getMessage());
        }
    }
}
