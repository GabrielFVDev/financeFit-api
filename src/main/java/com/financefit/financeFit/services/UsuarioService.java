package com.financefit.financeFit.services;

import com.financefit.financeFit.dtos.response.ResumoFinanceiroResponse;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.BusinessException;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.mappers.UsuarioMapper;
import com.financefit.financeFit.repositories.DespesaRepository;
import com.financefit.financeFit.repositories.ReceitaRepository;
import com.financefit.financeFit.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DespesaRepository despesaRepository;
    private final ReceitaRepository receitaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          DespesaRepository despesaRepository,
                          ReceitaRepository receitaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.despesaRepository = despesaRepository;
        this.receitaRepository = receitaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        if (usuario.getEmail() != null && usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado: " + usuario.getEmail());
        }
        if (usuario.getDataCriacao() == null) {
            usuario.setDataCriacao(LocalDate.now());
        }
        if (usuario.getSenha() != null && !usuario.getSenha().startsWith("$2a$")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public void deletarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        despesaRepository.findByUsuarioUserId(usuario.getUserId()).forEach(d -> despesaRepository.deleteById(d.getId()));
        receitaRepository.findByUsuarioUserId(usuario.getUserId()).forEach(r -> receitaRepository.deleteById(r.getId()));
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public Usuario atualizarUsuario(Long idUsuario, Usuario dadosAtualizados) {
        Usuario usuario = buscarPorId(idUsuario);

        if (dadosAtualizados.getNome() != null && !dadosAtualizados.getNome().isEmpty()) {
            usuario.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getEmail() != null && !dadosAtualizados.getEmail().isEmpty()) {
            if (!usuario.getEmail().equalsIgnoreCase(dadosAtualizados.getEmail()) &&
                    usuarioRepository.findByEmail(dadosAtualizados.getEmail()).isPresent()) {
                throw new BusinessException("Email já cadastrado: " + dadosAtualizados.getEmail());
            }
            usuario.setEmail(dadosAtualizados.getEmail());
        }
        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dadosAtualizados.getSenha()));
        }
        if (dadosAtualizados.getMetaMensal() != null && dadosAtualizados.getMetaMensal() >= 0) {
            usuario.setMetaMensal(dadosAtualizados.getMetaMensal());
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizarUsuarioPorEmail(String email, String nome, String novaSenha, Double metaMensal) {
        Usuario usuario = buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
        }
        if (novaSenha != null && !novaSenha.isBlank()) {
            usuario.setSenha(passwordEncoder.encode(novaSenha));
        }
        if (metaMensal != null && metaMensal >= 0) {
            usuario.setMetaMensal(metaMensal);
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletarUsuarioPorEmail(String email) {
        Usuario usuario = buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        despesaRepository.findByUsuarioUserId(usuario.getUserId()).forEach(d -> despesaRepository.deleteById(d.getId()));
        receitaRepository.findByUsuarioUserId(usuario.getUserId()).forEach(r -> receitaRepository.deleteById(r.getId()));
        usuarioRepository.deleteById(usuario.getUserId());
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario alterarSenha(Long idUsuario, String novaSenha) {
        Usuario usuario = buscarPorId(idUsuario);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizarMeta(Long idUsuario, double novaMeta) {
        Usuario usuario = buscarPorId(idUsuario);
        usuario.setMetaMensal(novaMeta);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> resumoFinanceiro(Long idUsuario) {
        ResumoFinanceiroResponse r = resumoFinanceiroDetalhado(idUsuario);
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalGasto", r.totalGasto());
        resumo.put("totalReceita", r.totalReceita());
        resumo.put("metaMensal", r.metaMensal());
        resumo.put("saldo", r.saldo());
        resumo.put("percentualUsado", r.percentualUsado());
        resumo.put("statusMeta", r.statusMeta());
        resumo.put("mes", r.mes());
        resumo.put("ano", r.ano());
        return resumo;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> resumoFinanceiro(Long idUsuario, int mes, int ano) {
        ResumoFinanceiroResponse r = resumoFinanceiroDetalhado(idUsuario, mes, ano);
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalGasto", r.totalGasto());
        resumo.put("totalReceita", r.totalReceita());
        resumo.put("metaMensal", r.metaMensal());
        resumo.put("saldo", r.saldo());
        resumo.put("percentualUsado", r.percentualUsado());
        resumo.put("statusMeta", r.statusMeta());
        resumo.put("mes", r.mes());
        resumo.put("ano", r.ano());
        return resumo;
    }

    @Transactional(readOnly = true)
    public ResumoFinanceiroResponse resumoFinanceiroDetalhado(Long idUsuario) {
        LocalDate hoje = LocalDate.now();
        return resumoFinanceiroDetalhado(idUsuario, hoje.getMonthValue(), hoje.getYear());
    }

    @Transactional(readOnly = true)
    public ResumoFinanceiroResponse resumoFinanceiroDetalhado(Long idUsuario, int mes, int ano) {
        buscarPorId(idUsuario);

        BigDecimal totalGasto = despesaRepository.calcularTotalGastoNoMes(idUsuario, mes, ano);
        BigDecimal totalReceita = receitaRepository.calcularTotalReceitaNoMes(idUsuario, mes, ano);

        Usuario usuario = buscarPorId(idUsuario);

        if (totalGasto == null) totalGasto = BigDecimal.ZERO;
        if (totalReceita == null) totalReceita = BigDecimal.ZERO;

        BigDecimal metaMensal = usuario.getMetaMensal() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(usuario.getMetaMensal());
        BigDecimal percentual;

        if (metaMensal.compareTo(BigDecimal.ZERO) == 0) {
            percentual = BigDecimal.ZERO;
        } else {
            percentual = totalGasto
                    .divide(metaMensal, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        BigDecimal saldo = totalReceita.subtract(totalGasto);

        String status = percentual.compareTo(BigDecimal.valueOf(80)) >= 0
                ? "ALERTA: Próximo do limite!"
                : "OK";

        return UsuarioMapper.toResumoResponse(
                totalGasto, totalReceita, metaMensal, saldo, percentual, status, mes, ano);
    }
}
