package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.entities.Receita;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.repositories.ReceitaRepository;
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
class ReceitaServiceTest {

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ReceitaService receitaService;

    private Receita receita;
    private Usuario usuario;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUserId(1L);
        usuario.setNome("Usuario Teste");

        categoria = new Categoria(1L, "Salário");

        receita = new Receita();
        receita.setId(1L);
        receita.setValor(new BigDecimal("3000.00"));
        receita.setData(LocalDate.now());
        receita.setDescricao("Salário mensal");
        receita.setUsuario(usuario);
        receita.setCategoria(categoria);
    }

    @Test
    void deveSalvarReceitaComSucesso() {
        // Given
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
        when(categoriaService.listarTodas()).thenReturn(java.util.List.of(categoria));
        when(receitaRepository.save(any(Receita.class))).thenReturn(receita);

        // When
        Receita resultado = receitaService.salvar(receita, 1L, 1L);

        // Then
        assertNotNull(resultado);
        assertEquals(new BigDecimal("3000.00"), resultado.getValor());
        verify(receitaRepository).save(any(Receita.class));
    }

    @Test
    void deveBuscarReceitaPorId() {
        // Given
        when(receitaRepository.findById(1L)).thenReturn(Optional.of(receita));

        // When
        Receita resultado = receitaService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoReceitaNaoEncontrada() {
        // Given
        when(receitaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> receitaService.buscarPorId(999L));
    }
}

