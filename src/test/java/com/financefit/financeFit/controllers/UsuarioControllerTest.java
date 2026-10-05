package com.financefit.financeFit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financefit.financeFit.dtos.request.AlterarSenhaRequest;
import com.financefit.financeFit.dtos.request.AtualizarMetaRequest;
import com.financefit.financeFit.dtos.request.UsuarioRequest;
import com.financefit.financeFit.dtos.request.UsuarioUpdateRequest;
import com.financefit.financeFit.dtos.response.ResumoFinanceiroResponse;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.GlobalExceptionHandler;
import com.financefit.financeFit.exception.ResourceNotFoundException;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private com.financefit.financeFit.security.JwtUtil jwtUtil;

    @MockBean
    private com.financefit.financeFit.security.CustomUserDetailsService userDetailsService;

    private Usuario usuario;
    private UsuarioRequest usuarioRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUserId(1L);
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setDataCriacao(LocalDate.now());
        usuario.setMetaMensal(1000.0);

        usuarioRequest = new UsuarioRequest("Test User", "test@example.com", "senha123", 1000.0);
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo ID e retornar status OK")
    void buscar_QuandoUsuarioExiste_RetornaStatusOk() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()))
                .andExpect(jsonPath("$.nome").value(usuario.getNome()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND ao buscar usuário por ID inexistente")
    void buscar_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(usuarioService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Usuário não encontrado com ID: 99"));

        mockMvc.perform(get("/usuarios/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com ID: 99"));
    }

    @Test
    @DisplayName("Deve criar um usuário e retornar status CREATED")
    void criar_ComDadosValidos_RetornaStatusCreated() throws Exception {
        when(usuarioService.criarUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()))
                .andExpect(jsonPath("$.nome").value(usuario.getNome()));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao tentar criar usuário com dados inválidos")
    void criar_ComDadosInvalidos_RetornaStatusBadRequest() throws Exception {
        UsuarioRequest invalido = new UsuarioRequest("", "invalid-email", "123", null);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar uma lista de usuários e status OK")
    void listarTodos_QuandoExistemUsuarios_RetornaStatusOkComLista() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(usuario.getUserId()))
                .andExpect(jsonPath("$[0].nome").value(usuario.getNome()));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia de usuários e status OK quando não há usuários")
    void listarTodos_QuandoNaoExistemUsuarios_RetornaStatusOkComListaVazia() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar um usuário e retornar status OK")
    void atualizar_QuandoUsuarioExiste_RetornaStatusOk() throws Exception {
        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setUserId(1L);
        updatedUsuario.setNome("Updated User");
        updatedUsuario.setEmail("updated@example.com");

        when(usuarioService.atualizarUsuario(eq(1L), any(Usuario.class))).thenReturn(updatedUsuario);

        UsuarioUpdateRequest updatedDTO = new UsuarioUpdateRequest("Updated User", "updated@example.com", null, 1000.0);

        mockMvc.perform(put("/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Updated User"));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND ao tentar atualizar usuário inexistente")
    void atualizar_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(usuarioService.atualizarUsuario(eq(99L), any(Usuario.class)))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado com ID: 99"));

        UsuarioUpdateRequest updatedDTO = new UsuarioUpdateRequest("Non Existent", "nonexistent@example.com", null, null);

        mockMvc.perform(put("/usuarios/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com ID: 99"));
    }

    @Test
    @DisplayName("Deve deletar um usuário e retornar status NO CONTENT")
    void deletar_QuandoUsuarioExiste_RetornaStatusNoContent() throws Exception {
        doNothing().when(usuarioService).deletarUsuario(1L);

        mockMvc.perform(delete("/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND ao tentar deletar usuário inexistente")
    void deletar_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Usuário não encontrado com ID: 99")).when(usuarioService).deletarUsuario(99L);

        mockMvc.perform(delete("/usuarios/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com ID: 99"));
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo email e retornar status OK")
    void buscarPorEmail_QuandoUsuarioExiste_RetornaStatusOk() throws Exception {
        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/usuarios/email/{email}", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND ao buscar usuário por email inexistente")
    void buscarPorEmail_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(usuarioService.buscarPorEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/email/{email}", "nonexistent@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve alterar a senha do usuário e retornar status OK")
    void alterarSenha_ComDadosValidos_RetornaStatusOk() throws Exception {
        when(usuarioService.alterarSenha(1L, "novaSenha123")).thenReturn(usuario);

        AlterarSenhaRequest body = new AlterarSenhaRequest("novaSenha123");

        mockMvc.perform(patch("/usuarios/{id}/senha", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao tentar alterar senha com campo vazio")
    void alterarSenha_ComSenhaVazia_RetornaStatusBadRequest() throws Exception {
        Map<String, String> body = Map.of("novaSenha", "");

        mockMvc.perform(patch("/usuarios/{id}/senha", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar a meta do usuário e retornar status OK")
    void atualizarMeta_ComDadosValidos_RetornaStatusOk() throws Exception {
        usuario.setMetaMensal(2500.0);
        when(usuarioService.atualizarMeta(1L, 2500.0)).thenReturn(usuario);

        AtualizarMetaRequest body = new AtualizarMetaRequest(2500.0);

        mockMvc.perform(patch("/usuarios/{id}/meta", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()))
                .andExpect(jsonPath("$.metaMensal").value(2500.0));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao tentar atualizar meta com valor negativo")
    void atualizarMeta_ComValorNegativo_RetornaStatusBadRequest() throws Exception {
        AtualizarMetaRequest body = new AtualizarMetaRequest(-100.0);

        mockMvc.perform(patch("/usuarios/{id}/meta", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar o resumo financeiro do usuário e status OK")
    void resumoFinanceiro_ComIdValido_RetornaStatusOk() throws Exception {
        ResumoFinanceiroResponse resumo = new ResumoFinanceiroResponse(
                new BigDecimal("500.00"), new BigDecimal("1000.00"), new BigDecimal("1000.00"),
                new BigDecimal("500.00"), new BigDecimal("50.00"), "OK", 10, 2023);

        when(usuarioService.resumoFinanceiroDetalhado(1L)).thenReturn(resumo);

        mockMvc.perform(get("/usuarios/{id}/resumo", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGasto").value(500.00))
                .andExpect(jsonPath("$.totalReceita").value(1000.00))
                .andExpect(jsonPath("$.saldo").value(500.00));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND ao buscar resumo financeiro de usuário inexistente")
    void resumoFinanceiro_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(usuarioService.resumoFinanceiroDetalhado(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado com ID: 99"));

        mockMvc.perform(get("/usuarios/{id}/resumo", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com ID: 99"));
    }

    @Test
    @DisplayName("Deve retornar o resumo financeiro do usuário por período e status OK")
    void resumoFinanceiroPeriodo_ComDadosValidos_RetornaStatusOk() throws Exception {
        ResumoFinanceiroResponse resumo = new ResumoFinanceiroResponse(
                new BigDecimal("300.00"), new BigDecimal("700.00"), new BigDecimal("1000.00"),
                new BigDecimal("400.00"), new BigDecimal("30.00"), "OK", 10, 2023);

        when(usuarioService.resumoFinanceiroDetalhado(1L, 10, 2023)).thenReturn(resumo);

        mockMvc.perform(get("/usuarios/{id}/resumo/{mes}/{ano}", 1L, 10, 2023)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGasto").value(300.00))
                .andExpect(jsonPath("$.totalReceita").value(700.00))
                .andExpect(jsonPath("$.saldo").value(400.00))
                .andExpect(jsonPath("$.mes").value(10))
                .andExpect(jsonPath("$.ano").value(2023));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao buscar resumo financeiro com mês inválido")
    void resumoFinanceiroPeriodo_ComMesInvalido_RetornaStatusBadRequest() throws Exception {
        mockMvc.perform(get("/usuarios/{id}/resumo/{mes}/{ano}", 1L, 13, 2023)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao buscar resumo financeiro com ano inválido")
    void resumoFinanceiroPeriodo_ComAnoInvalido_RetornaStatusBadRequest() throws Exception {
        mockMvc.perform(get("/usuarios/{id}/resumo/{mes}/{ano}", 1L, 10, 1999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar detalhes do usuário autenticado e status OK")
    @WithMockUser(username = "test@example.com")
    void me_QuandoAutenticado_RetornaStatusOkComUsuario() throws Exception {
        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário autenticado e retornar status OK")
    @WithMockUser(username = "test@example.com")
    void atualizarMe_QuandoAutenticadoComDadosValidos_RetornaStatusOk() throws Exception {
        UsuarioUpdateRequest updateDTO = new UsuarioUpdateRequest("Novo Nome", null, null, 3000.0);

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setUserId(1L);
        updatedUsuario.setNome("Novo Nome");
        updatedUsuario.setEmail("test@example.com");
        updatedUsuario.setMetaMensal(3000.0);

        when(usuarioService.atualizarUsuarioPorEmail(eq("test@example.com"), eq("Novo Nome"), any(), eq(3000.0)))
                .thenReturn(updatedUsuario);

        mockMvc.perform(patch("/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"))
                .andExpect(jsonPath("$.metaMensal").value(3000.0));
    }

    @Test
    @DisplayName("Deve deletar a conta do usuário autenticado e retornar status NO CONTENT")
    @WithMockUser(username = "test@example.com")
    void deletarMe_QuandoAutenticado_RetornaStatusNoContent() throws Exception {
        doNothing().when(usuarioService).deletarUsuarioPorEmail("test@example.com");

        mockMvc.perform(delete("/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
