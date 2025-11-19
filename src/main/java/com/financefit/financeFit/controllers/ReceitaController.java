package com.financefit.financeFit.controllers;

import com.financefit.financeFit.dtos.CreateReceitaDTO;
import com.financefit.financeFit.dtos.ReceitaDTO;
import com.financefit.financeFit.entities.Receita;
import com.financefit.financeFit.services.ReceitaService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {

    @Autowired
    private ReceitaService receitaService;

    @PostMapping
    public ResponseEntity<ReceitaDTO> criar(
            @Valid @RequestBody CreateReceitaDTO createReceitaDTO) {
        try {
            if (createReceitaDTO.getIdUsuario() <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }

            Receita receita = new Receita();
            BeanUtils.copyProperties(createReceitaDTO, receita);

            Receita criada = receitaService.salvar(receita, createReceitaDTO.getIdUsuario(), Optional.ofNullable(createReceitaDTO.getIdCategoria()));
            
            ReceitaDTO receitaDTO = new ReceitaDTO();
            BeanUtils.copyProperties(criada, receitaDTO);
            receitaDTO.setIdUsuario(criada.getUsuario().getUserId());
            if (criada.getCategoria() != null) {
                receitaDTO.setIdCategoria(criada.getCategoria().getCategoriaId());
            }
            receitaDTO.setTipo(criada.getTipo()); // Adicionar o tipo

            return ResponseEntity.status(HttpStatus.CREATED).body(receitaDTO);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar receita: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReceitaDTO>> listarPorUsuario(@PathVariable int idUsuario) {
        try {
            if (idUsuario <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }
            List<Receita> receitas = receitaService.listar(idUsuario);
            List<ReceitaDTO> receitasDTO = receitas.stream().map(receita -> {
                ReceitaDTO dto = new ReceitaDTO();
                BeanUtils.copyProperties(receita, dto);
                dto.setIdUsuario(receita.getUsuario().getUserId());
                if (receita.getCategoria() != null) {
                    dto.setIdCategoria(receita.getCategoria().getCategoriaId());
                }
                dto.setTipo(receita.getTipo()); // Adicionar o tipo
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(receitasDTO);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar receitas do usuário: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaDTO> buscarPorId(@PathVariable int id) {
        try {
            Receita receita = receitaService.buscarPorId(id);
            ReceitaDTO receitaDTO = new ReceitaDTO();
            BeanUtils.copyProperties(receita, receitaDTO);
            receitaDTO.setIdUsuario(receita.getUsuario().getUserId());
            if (receita.getCategoria() != null) {
                receitaDTO.setIdCategoria(receita.getCategoria().getCategoriaId());
            }
            receitaDTO.setTipo(receita.getTipo()); // Adicionar o tipo
            return ResponseEntity.ok(receitaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar receita: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CreateReceitaDTO createReceitaDTO) {
        try {
            if (createReceitaDTO.getIdUsuario() == null || createReceitaDTO.getIdUsuario() <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }

            Receita receitaAtualizada = new Receita();
            BeanUtils.copyProperties(createReceitaDTO, receitaAtualizada);

            Receita atualizada = receitaService.atualizar(id, receitaAtualizada, createReceitaDTO.getIdUsuario(), Optional.ofNullable(createReceitaDTO.getIdCategoria()));
            
            ReceitaDTO receitaDTO = new ReceitaDTO();
            BeanUtils.copyProperties(atualizada, receitaDTO);
            receitaDTO.setIdUsuario(atualizada.getUsuario().getUserId());
            if (atualizada.getCategoria() != null) {
                receitaDTO.setIdCategoria(atualizada.getCategoria().getCategoriaId());
            }
            receitaDTO.setTipo(atualizada.getTipo()); // Adicionar o tipo

            return ResponseEntity.ok(receitaDTO);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar receita: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            receitaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar receita: " + e.getMessage());
        }
    }
}
