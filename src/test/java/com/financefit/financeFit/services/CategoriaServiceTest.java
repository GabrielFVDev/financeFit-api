package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.repositories.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    public void deveSalvarCategoriaComSucesso() {
        Categoria categoria = new Categoria();
        categoria.setNome("Alimentação");

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria resultado = categoriaService.salvar(categoria);

        assertEquals("Alimentação", resultado.getNome());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    public void deveBuscarCategoriaPorId() {
        Categoria categoria = new Categoria(1L, "Transporte");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Transporte", resultado.getNome());
    }

    @Test
    public void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoriaService.buscarPorId(999L));
    }
}
