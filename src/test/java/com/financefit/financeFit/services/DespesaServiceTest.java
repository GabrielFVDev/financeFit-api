package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.entities.Despesa;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.repositories.DespesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespesaServiceTest {

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private DespesaService despesaService;

    private Despesa despesa;
    private Usuario usuario;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUserId(1L);
        usuario.setNome("Usuario Teste");

        categoria = new Categoria(1L, "Alimentação");

        despesa = new Despesa();
        despesa.setId(1L);
        despesa.setValor(new BigDecimal("100.00"));
        despesa.setData(LocalDate.now());
        despesa.setDescricao("Compra");
        despesa.setUsuario(usuario);
        despesa.setCategoria(categoria);
    }

    @Test
    void deveSalvarDespesaComSucesso() {
        // Given
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
        when(categoriaService.listarTodas()).thenReturn(java.util.List.of(categoria));
        when(despesaRepository.save(any(Despesa.class))).thenReturn(despesa);

        // When
        Despesa resultado = despesaService.salvar(despesa, 1L, 1L);

        // Then
        assertNotNull(resultado);
        assertEquals(new BigDecimal("100.00"), resultado.getValor());
        verify(despesaRepository).save(any(Despesa.class));
    }

    @Test
    void deveBuscarDespesaPorId() {
        // Given
        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        // When
        Despesa resultado = despesaService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoDespesaNaoEncontrada() {
        // Given
        when(despesaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> despesaService.buscarPorId(999L));
    }
}

