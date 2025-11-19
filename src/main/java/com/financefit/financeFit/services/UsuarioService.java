package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.repositories.DespesaRepository;
import com.financefit.financeFit.repositories.ReceitaRepository;
import com.financefit.financeFit.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private ReceitaRepository receitaRepository; // Injetar ReceitaRepository

    public Usuario buscarPorId(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario criarUsuario(Usuario usuario) {
        if (usuario.getDataCriacao() == null) {
            usuario.setDataCriacao(LocalDate.now());
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void deletarUsuario(int id) {
        Usuario usuario = buscarPorId(id); // valida se existe
        usuarioRepository.deleteById(id);
    }

    public Usuario atualizarUsuario(int idUsuario, Usuario dadosAtualizados) {
        Usuario usuario = buscarPorId(idUsuario);

        if (dadosAtualizados.getNome() != null && !dadosAtualizados.getNome().isEmpty()) {
            usuario.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getEmail() != null && !dadosAtualizados.getEmail().isEmpty()) {
            usuario.setEmail(dadosAtualizados.getEmail());
        }
        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isEmpty()) {
            usuario.setSenha(dadosAtualizados.getSenha());
        }
        if (dadosAtualizados.getMetaMensal() >= 0) {
            usuario.setMetaMensal(dadosAtualizados.getMetaMensal());
        }

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario alterarSenha(int idUsuario, String novaSenha) {
        Usuario usuario = buscarPorId(idUsuario);
        usuario.setSenha(novaSenha);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarMeta(int idUsuario, double novaMeta) {
        Usuario usuario = buscarPorId(idUsuario);
        usuario.setMetaMensal(novaMeta);
        return usuarioRepository.save(usuario);
    }

    public Map<String, Object> resumoFinanceiro(int idUsuario) {
        LocalDate hoje = LocalDate.now();
        return resumoFinanceiro(idUsuario, hoje.getMonthValue(), hoje.getYear());
    }

    public Map<String, Object> resumoFinanceiro(int idUsuario, int mes, int ano) {
        BigDecimal totalGasto = despesaRepository.calcularTotalGastoNoMes(
                idUsuario,
                mes,
                ano
        );

        BigDecimal totalReceita = receitaRepository.calcularTotalReceitaNoMes( // Calcular total de receitas
                idUsuario,
                mes,
                ano
        );

        Usuario usuario = buscarPorId(idUsuario);

        if (totalGasto == null) totalGasto = BigDecimal.ZERO;
        if (totalReceita == null) totalReceita = BigDecimal.ZERO; // Tratar caso de receita nula

        BigDecimal metaMensal = BigDecimal.valueOf(usuario.getMetaMensal());
        BigDecimal percentual;

        if (metaMensal.compareTo(BigDecimal.ZERO) == 0) {
            percentual = BigDecimal.ZERO;
        } else {
            percentual = totalGasto
                    .divide(metaMensal, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        BigDecimal saldo = totalReceita.subtract(totalGasto); // Calcular saldo

        String status = percentual.compareTo(BigDecimal.valueOf(80)) >= 0
                ? "ALERTA: Próximo do limite!"
                : "OK";

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalGasto", totalGasto);
        resumo.put("totalReceita", totalReceita); // Adicionar totalReceita ao resumo
        resumo.put("metaMensal", metaMensal);
        resumo.put("saldo", saldo); // Adicionar saldo ao resumo
        resumo.put("percentualUsado", percentual);
        resumo.put("statusMeta", status);
        resumo.put("mes", mes);
        resumo.put("ano", ano);

        return resumo;
    }
}
