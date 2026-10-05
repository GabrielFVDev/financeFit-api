package com.financefit.financeFit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financefit.financeFit.dtos.request.DespesaRequest;
import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.entities.Despesa;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.GlobalExceptionHandler;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.services.DespesaService;
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

@WebMvcTest(controllers = DespesaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@WithMockUser(username = "test@example.com")
class DespesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DespesaService despesaService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private com.financefit.financeFit.security.JwtUtil jwtUtil;

    @MockBean
    private com.financefit.financeFit.security.CustomUserDetailsService userDetailsService;

    private Usuario usuario;
    private Categoria categoria;
    private Despesa despesa;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUserId(1L);
        usuario.setNome("Test");
        usuario.setEmail("test@example.com");

        categoria = new Categoria(1L, "Moradia");

        despesa = new Despesa();
        despesa.setId(1L);
        despesa.setValor(new BigDecimal("150.00"));
        despesa.setData(LocalDate.now());
        despesa.setDescricao("Aluguel");
        despesa.setUsuario(usuario);
        despesa.setCategoria(categoria);

        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(Optional.of(usuario));
    }

    @Test
    @DisplayName("Deve criar despesa do usuário autenticado e retornar 201")
    void criar_Autenticado_RetornaCreated() throws Exception {
        DespesaRequest req = new DespesaRequest(new BigDecimal("150.00"), LocalDate.now(), "Aluguel", 1L);
        when(despesaService.salvar(any(Despesa.class), eq(1L), eq(1L))).thenReturn(despesa);

        mockMvc.perform(post("/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idUsuario").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar despesa inválida")
    void criar_Invalida_RetornaBadRequest() throws Exception {
        DespesaRequest req = new DespesaRequest(new BigDecimal("-10.00"), null, "", null);

        mockMvc.perform(post("/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar minhas despesas e retornar 200")
    void listarMinhas_RetornaOk() throws Exception {
        when(despesaService.listar(1L)).thenReturn(List.of(despesa));

        mockMvc.perform(get("/despesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar despesa própria e retornar 200")
    void buscar_Propria_RetornaOk() throws Exception {
        when(despesaService.buscarPorId(1L)).thenReturn(despesa);

        mockMvc.perform(get("/despesas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 403 ao buscar despesa de outro usuário")
    void buscar_DeOutroUsuario_RetornaForbidden() throws Exception {
        Usuario outro = new Usuario();
        outro.setUserId(2L);
        Despesa alheia = new Despesa();
        alheia.setId(9L);
        alheia.setUsuario(outro);
        alheia.setCategoria(categoria);
        when(despesaService.buscarPorId(9L)).thenReturn(alheia);

        mockMvc.perform(get("/despesas/{id}", 9L))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar despesa inexistente")
    void buscar_Inexistente_RetornaNotFound() throws Exception {
        when(despesaService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Despesa não encontrada com ID: 99"));

        mockMvc.perform(get("/despesas/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar despesa própria e retornar 200")
    void atualizar_Propria_RetornaOk() throws Exception {
        DespesaRequest req = new DespesaRequest(new BigDecimal("200.00"), LocalDate.now(), "Atualizada", 1L);
        when(despesaService.buscarPorId(1L)).thenReturn(despesa);
        when(despesaService.atualizar(eq(1L), any(Despesa.class), eq(1L), eq(1L))).thenReturn(despesa);

        mockMvc.perform(put("/despesas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve deletar despesa própria e retornar 204")
    void deletar_Propria_RetornaNoContent() throws Exception {
        when(despesaService.buscarPorId(1L)).thenReturn(despesa);
        doNothing().when(despesaService).deletar(1L);

        mockMvc.perform(delete("/despesas/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
