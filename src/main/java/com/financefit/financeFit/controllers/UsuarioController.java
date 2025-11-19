package com.financefit.financeFit.controllers;


import com.financefit.financeFit.dtos.UsuarioDTO;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Validated
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO dto) {
        try {
            Usuario usuario = toEntity(dto);
            Usuario criado = usuarioService.criarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(criado));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscar(@PathVariable int id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(toDto(usuario));
        } catch (RuntimeException e) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
    }

    @GetMapping
    public ResponseEntity<java.util.List<UsuarioDTO>> listarTodos() {
        try {
            java.util.List<Usuario> usuarios = usuarioService.listarTodos();
            java.util.List<UsuarioDTO> dtos = usuarios.stream()
                    .map(this::toDto)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable int id, @Valid @RequestBody UsuarioDTO dto) {
        try {
            Usuario dadosAtualizados = toEntity(dto);
            Usuario atualizado = usuarioService.atualizarUsuario(id, dadosAtualizados);
            return ResponseEntity.ok(toDto(atualizado));
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar usuário com ID " + id + ": " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao deletar usuário com ID " + id + ": " + e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        try {
            return usuarioService.buscarPorEmail(email)
                    .map(usuario -> ResponseEntity.ok(toDto(usuario)))
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<UsuarioDTO> alterarSenha(@PathVariable int id, @RequestBody java.util.Map<String, String> body) {
        try {
            String novaSenha = body.get("senha");
            if (novaSenha == null || novaSenha.isEmpty()) {
                throw new IllegalArgumentException("Senha não pode ser vazia");
            }
            Usuario atualizado = usuarioService.alterarSenha(id, novaSenha);
            return ResponseEntity.ok(toDto(atualizado));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao alterar senha do usuário com ID " + id + ": " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/meta")
    public ResponseEntity<UsuarioDTO> atualizarMeta(@PathVariable int id, @RequestBody java.util.Map<String, Double> body) {
        try {
            Double novaMeta = body.get("metaMensal");
            if (novaMeta == null || novaMeta < 0) {
                throw new IllegalArgumentException("Meta mensal deve ser maior ou igual a zero");
            }
            Usuario atualizado = usuarioService.atualizarMeta(id, novaMeta);
            return ResponseEntity.ok(toDto(atualizado));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao atualizar meta do usuário com ID " + id + ": " + e.getMessage());
        }
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<java.util.Map<String, Object>> resumoFinanceiro(@PathVariable int id) {
        try {
            java.util.Map<String, Object> resumo = usuarioService.resumoFinanceiro(id);
            return ResponseEntity.ok(resumo);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar resumo financeiro do usuário com ID " + id + ": " + e.getMessage());
        }
    }

    @GetMapping("/{id}/resumo/{mes}/{ano}")
    public ResponseEntity<java.util.Map<String, Object>> resumoFinanceiroPeriodo(
            @PathVariable Integer id,
            @PathVariable Integer mes,
            @PathVariable Integer ano) {
        try {
            if (mes < 1 || mes > 12) {
                throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
            }
            if (ano < 2000 || ano > 2100) {
                throw new IllegalArgumentException("Ano inválido");
            }
            java.util.Map<String, Object> resumo = usuarioService.resumoFinanceiro(id, mes, ano);
            return ResponseEntity.ok(resumo);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar resumo financeiro: " + e.getMessage());
        }
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        if (dto.getId() != null) {
            u.setUserId(dto.getId());
        }
        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        u.setSenha(dto.getSenha());
        if (dto.getDataCriacao() != null) {
            u.setDataCriacao(dto.getDataCriacao());
        }
        if (dto.getMetaMensal() != null) {
            u.setMetaMensal(dto.getMetaMensal());
        }
        return u;
    }

    private UsuarioDTO toDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getUserId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setDataCriacao(usuario.getDataCriacao());
        dto.setMetaMensal(usuario.getMetaMensal());
        // não retornar senha em respostas
        return dto;
    }
}
