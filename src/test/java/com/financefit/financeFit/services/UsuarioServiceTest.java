package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.repositories.DespesaRepository;
import com.financefit.financeFit.repositories.ReceitaRepository;
import com.financefit.financeFit.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private ReceitaRepository receitaRepository;


    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUserId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@example.com");
        usuario.setMetaMensal(2000.0);
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        // Given
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Novo Usuário");
        novoUsuario.setEmail("novo@example.com");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(novoUsuario);

        // When
        Usuario resultado = usuarioService.criarUsuario(novoUsuario);

        // Then
        assertNotNull(resultado);
        assertEquals("Novo Usuário", resultado.getNome());
        assertNotNull(resultado.getDataCriacao());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveBuscarUsuarioPorId() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = usuarioService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getUserId());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> usuarioService.buscarPorId(999L));
    }

    @Test
    void deveListarTodosOsUsuarios() {
        // Given
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        // When
        List<Usuario> resultado = usuarioService.listarTodos();

        // Then
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        // Given
        Usuario dadosAtualizados = new Usuario();
        dadosAtualizados.setNome("Nome Atualizado");
        dadosAtualizados.setEmail("email@atualizado.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario resultado = usuarioService.atualizarUsuario(1L, dadosAtualizados);

        // Then
        assertNotNull(resultado);
        assertEquals("Nome Atualizado", resultado.getNome());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> usuarioService.deletarUsuario(1L));
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        // Given
        when(usuarioRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(usuario));

        // When
        Optional<Usuario> resultado = usuarioService.buscarPorEmail("teste@example.com");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(usuario.getEmail(), resultado.get().getEmail());
    }

    @Test
    void deveRetornarResumoFinanceiro() {
        // Given
        int mes = 11;
        int ano = 2025;
        BigDecimal totalGasto = new BigDecimal("1500.00");
        BigDecimal totalReceita = new BigDecimal("5000.00");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(despesaRepository.calcularTotalGastoNoMes(1L, mes, ano)).thenReturn(totalGasto);
        when(receitaRepository.calcularTotalReceitaNoMes(1L, mes, ano)).thenReturn(totalReceita);

        // When
        Map<String, Object> resumo = usuarioService.resumoFinanceiro(1L, mes, ano);

        // Then
        assertNotNull(resumo);
        assertEquals(totalGasto, resumo.get("totalGasto"));
        assertEquals(totalReceita, resumo.get("totalReceita"));
        assertEquals(new BigDecimal("3500.00"), resumo.get("saldo")); // 5000 - 1500
        assertEquals("OK", resumo.get("statusMeta"));
    }
}

