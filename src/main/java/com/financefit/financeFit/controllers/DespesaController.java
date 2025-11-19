package com.financefit.financeFit.controllers;

import com.financefit.financeFit.dtos.CreateDespesaDTO;
import com.financefit.financeFit.dtos.DespesaDTO;
import com.financefit.financeFit.entities.Despesa;
import com.financefit.financeFit.services.DespesaService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/despesas")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;

    @PostMapping
    public ResponseEntity<DespesaDTO> criar(
            @Valid @RequestBody CreateDespesaDTO createDespesaDTO) {
        try {
            if (createDespesaDTO.getIdUsuario() <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }
            if (createDespesaDTO.getIdCategoria() <= 0) {
                throw new IllegalArgumentException("ID da categoria inválido");
            }

            Despesa despesa = new Despesa();
            BeanUtils.copyProperties(createDespesaDTO, despesa);

            Despesa criada = despesaService.salvar(despesa, createDespesaDTO.getIdUsuario(), createDespesaDTO.getIdCategoria());
            
            DespesaDTO despesaDTO = new DespesaDTO();
            BeanUtils.copyProperties(criada, despesaDTO);
            despesaDTO.setIdUsuario(criada.getUsuario().getUserId());
            despesaDTO.setIdCategoria(criada.getCategoria().getCategoriaId());
            despesaDTO.setTipo(criada.getTipo()); // Adicionar o tipo

            return ResponseEntity.status(HttpStatus.CREATED).body(despesaDTO);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar despesa: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<DespesaDTO>> listarPorUsuario(@PathVariable int idUsuario) {
        try {
            if (idUsuario <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }
            List<Despesa> despesas = despesaService.listar(idUsuario);
            List<DespesaDTO> despesasDTO = despesas.stream().map(despesa -> {
                DespesaDTO dto = new DespesaDTO();
                BeanUtils.copyProperties(despesa, dto);
                dto.setIdUsuario(despesa.getUsuario().getUserId());
                dto.setIdCategoria(despesa.getCategoria().getCategoriaId());
                dto.setTipo(despesa.getTipo()); // Adicionar o tipo
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(despesasDTO);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar despesas do usuário: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespesaDTO> buscarPorId(@PathVariable int id) {
        try {
            Despesa despesa = despesaService.buscarPorId(id);
            DespesaDTO despesaDTO = new DespesaDTO();
            BeanUtils.copyProperties(despesa, despesaDTO);
            despesaDTO.setIdUsuario(despesa.getUsuario().getUserId());
            despesaDTO.setIdCategoria(despesa.getCategoria().getCategoriaId());
            despesaDTO.setTipo(despesa.getTipo()); // Adicionar o tipo
            return ResponseEntity.ok(despesaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar despesa: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaDTO> atualizar(
            @PathVariable int id,
            @Valid @RequestBody CreateDespesaDTO createDespesaDTO) {
        try {
            if (createDespesaDTO.getIdUsuario() <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }
            if (createDespesaDTO.getIdCategoria() <= 0) {
                throw new IllegalArgumentException("ID da categoria inválido");
            }

            Despesa despesaAtualizada = new Despesa();
            BeanUtils.copyProperties(createDespesaDTO, despesaAtualizada);

            Despesa atualizada = despesaService.atualizar(id, despesaAtualizada, createDespesaDTO.getIdUsuario(), createDespesaDTO.getIdCategoria());
            
            DespesaDTO despesaDTO = new DespesaDTO();
            BeanUtils.copyProperties(atualizada, despesaDTO);
            despesaDTO.setIdUsuario(atualizada.getUsuario().getUserId());
            despesaDTO.setIdCategoria(atualizada.getCategoria().getCategoriaId());
            despesaDTO.setTipo(atualizada.getTipo()); // Adicionar o tipo

            return ResponseEntity.ok(despesaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar despesa: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            despesaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar despesa: " + e.getMessage());
        }
    }
}
