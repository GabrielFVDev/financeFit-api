package com.financefit.financeFit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financefit.financeFit.dtos.request.CategoriaRequest;
import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.exception.GlobalExceptionHandler;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.services.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private com.financefit.financeFit.security.JwtUtil jwtUtil;

    @MockBean
    private com.financefit.financeFit.security.CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Deve criar categoria e retornar 201")
    void criar_ComDadosValidos_RetornaCreated() throws Exception {
        Categoria salva = new Categoria(1L, "Alimentação");
        when(categoriaService.salvar(any(Categoria.class))).thenReturn(salva);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoriaRequest("Alimentação"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoriaId").value(1L))
                .andExpect(jsonPath("$.nome").value("Alimentação"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar categoria sem nome")
    void criar_SemNome_RetornaBadRequest() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar categorias e retornar 200")
    void listar_RetornaOk() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of(new Categoria(1L, "A"), new Categoria(2L, "B")));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Deve buscar categoria por id e retornar 200")
    void buscar_Existente_RetornaOk() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(new Categoria(1L, "Alimentação"));

        mockMvc.perform(get("/categorias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Alimentação"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar categoria inexistente")
    void buscar_Inexistente_RetornaNotFound() throws Exception {
        when(categoriaService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Categoria não encontrada com ID: 99"));

        mockMvc.perform(get("/categorias/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar categoria e retornar 200")
    void atualizar_Existente_RetornaOk() throws Exception {
        when(categoriaService.atualizar(eq(1L), any(Categoria.class))).thenReturn(new Categoria(1L, "Nova"));

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoriaRequest("Nova"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nova"));
    }

    @Test
    @DisplayName("Deve deletar categoria e retornar 204")
    void deletar_Existente_RetornaNoContent() throws Exception {
        doNothing().when(categoriaService).deletar(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
