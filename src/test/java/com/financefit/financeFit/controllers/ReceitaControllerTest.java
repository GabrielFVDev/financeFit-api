package com.financefit.financeFit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financefit.financeFit.dtos.request.ReceitaRequest;
import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.entities.Receita;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.GlobalExceptionHandler;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.services.ReceitaService;
import com.financefit.financeFit.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

@WebMvcTest(controllers = ReceitaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@WithMockUser(username = "test@example.com")
class ReceitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReceitaService receitaService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private com.financefit.financeFit.security.JwtUtil jwtUtil;

    @MockBean
    private com.financefit.financeFit.security.CustomUserDetailsService userDetailsService;

    private Usuario usuario;
    private Categoria categoria;
    private Receita receita;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUserId(1L);
        usuario.setNome("Test");
        usuario.setEmail("test@example.com");

        categoria = new Categoria(1L, "Salário");

        receita = new Receita();
        receita.setId(1L);
        receita.setValor(new BigDecimal("2000.00"));
        receita.setData(LocalDate.now());
        receita.setDescricao("Salário");
        receita.setUsuario(usuario);
        receita.setCategoria(categoria);

        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(Optional.of(usuario));
    }

    @Test
    @DisplayName("Deve criar receita do usuário autenticado e retornar 201")
    void criar_Autenticado_RetornaCreated() throws Exception {
        ReceitaRequest req = new ReceitaRequest(new BigDecimal("2000.00"), LocalDate.now(), "Salário", 1L);
        when(receitaService.salvar(any(Receita.class), eq(1L), eq(1L))).thenReturn(receita);

        mockMvc.perform(post("/receitas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idUsuario").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar receita inválida")
    void criar_Invalida_RetornaBadRequest() throws Exception {
        ReceitaRequest req = new ReceitaRequest(new BigDecimal("-5.00"), null, "", null);

        mockMvc.perform(post("/receitas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar minhas receitas e retornar 200")
    void listarMinhas_RetornaOk() throws Exception {
        when(receitaService.listar(1L)).thenReturn(List.of(receita));

        mockMvc.perform(get("/receitas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar receita própria e retornar 200")
    void buscar_Propria_RetornaOk() throws Exception {
        when(receitaService.buscarPorId(1L)).thenReturn(receita);

        mockMvc.perform(get("/receitas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 403 ao buscar receita de outro usuário")
    void buscar_DeOutroUsuario_RetornaForbidden() throws Exception {
        Usuario outro = new Usuario();
        outro.setUserId(2L);
        Receita alheia = new Receita();
        alheia.setId(9L);
        alheia.setUsuario(outro);
        alheia.setCategoria(categoria);
        when(receitaService.buscarPorId(9L)).thenReturn(alheia);

        mockMvc.perform(get("/receitas/{id}", 9L))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar receita inexistente")
    void buscar_Inexistente_RetornaNotFound() throws Exception {
        when(receitaService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Receita não encontrada com ID: 99"));

        mockMvc.perform(get("/receitas/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar receita própria e retornar 200")
    void atualizar_Propria_RetornaOk() throws Exception {
        ReceitaRequest req = new ReceitaRequest(new BigDecimal("2500.00"), LocalDate.now(), "Atualizada", 1L);
        when(receitaService.buscarPorId(1L)).thenReturn(receita);
        when(receitaService.atualizar(eq(1L), any(Receita.class), eq(1L), eq(1L))).thenReturn(receita);

        mockMvc.perform(put("/receitas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve deletar receita própria e retornar 204")
    void deletar_Propria_RetornaNoContent() throws Exception {
        when(receitaService.buscarPorId(1L)).thenReturn(receita);
        doNothing().when(receitaService).deletar(1L);

        mockMvc.perform(delete("/receitas/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
