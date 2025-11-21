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
            
            DespesaDTO despesaDTO = convertToDespesaDTO(criada);

            return ResponseEntity.status(HttpStatus.CREATED).body(despesaDTO);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar despesa: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<DespesaDTO>> listarPorUsuario(@PathVariable Long idUsuario) {
        try {
            if (idUsuario <= 0) {
                throw new IllegalArgumentException("ID do usuário inválido");
            }
            List<Despesa> despesas = despesaService.listar(idUsuario);
            List<DespesaDTO> despesasDTO = despesas.stream()
                    .map(this::convertToDespesaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(despesasDTO);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar despesas do usuário: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespesaDTO> buscarPorId(@PathVariable Long id) {
        try {
            Despesa despesa = despesaService.buscarPorId(id);
            DespesaDTO despesaDTO = convertToDespesaDTO(despesa);
            return ResponseEntity.ok(despesaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar despesa: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaDTO> atualizar(
            @PathVariable Long id,
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
            
            DespesaDTO despesaDTO = convertToDespesaDTO(atualizada);

            return ResponseEntity.ok(despesaDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar despesa: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            despesaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar despesa: " + e.getMessage());
        }
    }

    private DespesaDTO convertToDespesaDTO(Despesa despesa) {
        DespesaDTO dto = new DespesaDTO();
        BeanUtils.copyProperties(despesa, dto);
        dto.setIdUsuario(despesa.getUsuario().getUserId());
        dto.setTipo(despesa.getTipo());

        if (despesa.getCategoria() != null) {
            com.financefit.financeFit.dtos.CategoriaDTO categoriaDTO = new com.financefit.financeFit.dtos.CategoriaDTO();
            categoriaDTO.setCategoriaId(despesa.getCategoria().getCategoriaId());
            categoriaDTO.setNome(despesa.getCategoria().getNome());
            dto.setCategoria(categoriaDTO);
            dto.setIdCategoria(despesa.getCategoria().getCategoriaId());
        }

        return dto;
    }
}
