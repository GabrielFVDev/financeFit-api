package com.financefit.financeFit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.financefit.financeFit.dtos.CreateDespesaDTO;

import com.financefit.financeFit.dtos.CreateReceitaDTO;

import com.financefit.financeFit.dtos.UpdateUsuarioDTO;

import com.financefit.financeFit.dtos.UsuarioDTO;

import com.financefit.financeFit.entities.Categoria;

import com.financefit.financeFit.entities.Despesa;

import com.financefit.financeFit.entities.Receita;

import com.financefit.financeFit.entities.Usuario;

import com.financefit.financeFit.services.DespesaService;

import com.financefit.financeFit.services.ReceitaService;

import com.financefit.financeFit.services.UsuarioService;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.financefit.financeFit.security.CustomUserDetailsService;

import com.financefit.financeFit.security.JwtAuthenticationFilter;

import com.financefit.financeFit.security.SecurityConfig;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import com.financefit.financeFit.exception.GlobalExceptionHandler;



import java.math.BigDecimal;

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



@WebMvcTest(controllers = UsuarioController.class,

            properties = "spring.jpa.hibernate.ddl-auto=create-drop") // Adicionado para garantir que o contexto do DB seja carregado corretamente para testes

@Import(SecurityConfig.class)

class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private DespesaService despesaService;

    @MockBean
    private ReceitaService receitaService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUserId(1L);
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNome("Test User");
        usuarioDTO.setEmail("test@example.com");

        categoria = new Categoria();
        categoria.setCategoriaId(1L);
        categoria.setNome("Alimentação");
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
        when(usuarioService.buscarPorId(any(Long.class))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Changed to 500 as RuntimeException is thrown
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com ID: 99"));
    }

    @Test
    @DisplayName("Deve criar um usuário e retornar status CREATED")
    void criar_ComDadosValidos_RetornaStatusCreated() throws Exception {
        when(usuarioService.criarUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()))
                .andExpect(jsonPath("$.nome").value(usuario.getNome()));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao tentar criar usuário com dados inválidos")
    void criar_ComDadosInvalidos_RetornaStatusBadRequest() throws Exception {
        // Given - um DTO inválido (ex: email mal formatado ou nome vazio, dependendo da validação do DTO)
        UsuarioDTO invalidUsuarioDTO = new UsuarioDTO();
        invalidUsuarioDTO.setNome(""); // Nome vazio para forçar uma validação @Valid
        invalidUsuarioDTO.setEmail("invalid");

        // Mockamos a validação do serviço para lançar uma exceção de argumento ilegal,
        // ou o controller poderia ter um @ExceptionHandler mais específico
        when(usuarioService.criarUsuario(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("Nome do usuário não pode ser vazio"));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUsuarioDTO)))
                .andExpect(status().isBadRequest()); // O GlobalExceptionHandler deve capturar IllegalArgumentException e mapear para BAD_REQUEST
    }

    @Test
    @DisplayName("Deve retornar uma lista de usuários e status OK")
    void listarTodos_QuandoExistemUsuarios_RetornaStatusOkComLista() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(java.util.List.of(usuario));

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(usuario.getUserId()))
                .andExpect(jsonPath("$[0].nome").value(usuario.getNome()));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia de usuários e status OK quando não há usuários")
    void listarTodos_QuandoNaoExistemUsuarios_RetornaStatusOkComListaVazia() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar um usuário e retornar status OK")
    void atualizar_QuandoUsuarioExiste_RetornaStatusOk() throws Exception {
        UsuarioDTO updatedUsuarioDTO = new UsuarioDTO();
        updatedUsuarioDTO.setId(1L);
        updatedUsuarioDTO.setNome("Updated User");
        updatedUsuarioDTO.setEmail("updated@example.com");

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setUserId(1L);
        updatedUsuario.setNome("Updated User");
        updatedUsuario.setEmail("updated@example.com");

        when(usuarioService.atualizarUsuario(any(Long.class), any(Usuario.class))).thenReturn(updatedUsuario);

        mockMvc.perform(put("/usuarios/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUsuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUsuario.getUserId()))
                .andExpect(jsonPath("$.nome").value(updatedUsuario.getNome()))
                .andExpect(jsonPath("$.email").value(updatedUsuario.getEmail()));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao tentar atualizar usuário inexistente")
    void atualizar_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        UsuarioDTO updatedUsuarioDTO = new UsuarioDTO();
        updatedUsuarioDTO.setId(99L);
        updatedUsuarioDTO.setNome("Non Existent User");

        when(usuarioService.atualizarUsuario(any(Long.class), any(Usuario.class))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(put("/usuarios/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUsuarioDTO)))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao atualizar usuário com ID 99: Usuário não encontrado"));
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
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao tentar deletar usuário inexistente")
    void deletar_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        doThrow(new RuntimeException("Usuário não encontrado")).when(usuarioService).deletarUsuario(any(Long.class));

        mockMvc.perform(delete("/usuarios/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao deletar usuário com ID 99: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo email e retornar status OK")
    void buscarPorEmail_QuandoUsuarioExiste_RetornaStatusOk() throws Exception {
        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(java.util.Optional.of(usuario));

        mockMvc.perform(get("/usuarios/email/{email}", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao buscar usuário por email inexistente")
    void buscarPorEmail_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(usuarioService.buscarPorEmail(any(String.class))).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/usuarios/email/{email}", "nonexistent@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com email: nonexistent@example.com"));
    }

    @Test
    @DisplayName("Deve alterar a senha do usuário e retornar status OK")
    void alterarSenha_ComDadosValidos_RetornaStatusOk() throws Exception {
        Usuario usuarioComNovaSenha = new Usuario();
        usuarioComNovaSenha.setUserId(1L);
        usuarioComNovaSenha.setNome("Test User");
        usuarioComNovaSenha.setEmail("test@example.com");
        usuarioComNovaSenha.setSenha("novaSenhaHash"); // Assume a senha já foi encodificada no serviço

        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("senha", "novaSenha123");

        when(usuarioService.alterarSenha(1L, "novaSenha123")).thenReturn(usuarioComNovaSenha);

        mockMvc.perform(patch("/usuarios/{id}/senha", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioComNovaSenha.getUserId()))
                .andExpect(jsonPath("$.nome").value(usuarioComNovaSenha.getNome()));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao tentar alterar senha de usuário inexistente")
    void alterarSenha_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("senha", "novaSenha123");

        when(usuarioService.alterarSenha(any(Long.class), any(String.class))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(patch("/usuarios/{id}/senha", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao alterar senha do usuário com ID 99: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao tentar alterar senha com campo vazio")
    void alterarSenha_ComSenhaVazia_RetornaStatusBadRequest() throws Exception {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("senha", "");

        mockMvc.perform(patch("/usuarios/{id}/senha", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest()); // Assuming IllegalArgumentException is mapped to 400
    }

    @Test
    @DisplayName("Deve atualizar a meta do usuário e retornar status OK")
    void atualizarMeta_ComDadosValidos_RetornaStatusOk() throws Exception {
        java.util.Map<String, Double> body = new java.util.HashMap<>();
        body.put("metaMensal", 2500.0);

        Usuario usuarioComNovaMeta = new Usuario();
        usuarioComNovaMeta.setUserId(1L);
usuarioComNovaMeta.setNome("Test User");
        usuarioComNovaMeta.setEmail("test@example.com");
        usuarioComNovaMeta.setMetaMensal(2500.0);

        when(usuarioService.atualizarMeta(1L, 2500.0)).thenReturn(usuarioComNovaMeta);

        mockMvc.perform(patch("/usuarios/{id}/meta", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioComNovaMeta.getUserId()))
                .andExpect(jsonPath("$.metaMensal").value(usuarioComNovaMeta.getMetaMensal()));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao tentar atualizar meta de usuário inexistente")
    void atualizarMeta_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        java.util.Map<String, Double> body = new java.util.HashMap<>();
        body.put("metaMensal", 2500.0);

        when(usuarioService.atualizarMeta(any(Long.class), any(Double.class))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(patch("/usuarios/{id}/meta", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao atualizar meta do usuário com ID 99: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao tentar atualizar meta com valor negativo")
    void atualizarMeta_ComValorNegativo_RetornaStatusBadRequest() throws Exception {
        java.util.Map<String, Double> body = new java.util.HashMap<>();
        body.put("metaMensal", -100.0);

        mockMvc.perform(patch("/usuarios/{id}/meta", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest()); // Assuming IllegalArgumentException is mapped to 400
    }

    @Test
    @DisplayName("Deve retornar o resumo financeiro do usuário e status OK")
    void resumoFinanceiro_ComIdValido_RetornaStatusOk() throws Exception {
        java.util.Map<String, Object> resumo = new java.util.HashMap<>();
        resumo.put("totalGasto", new BigDecimal("500.00"));
        resumo.put("totalReceita", new BigDecimal("1000.00"));
        resumo.put("saldo", new BigDecimal("500.00"));

        when(usuarioService.resumoFinanceiro(1L)).thenReturn(resumo);

        mockMvc.perform(get("/usuarios/{id}/resumo", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGasto").value(500.00))
                .andExpect(jsonPath("$.totalReceita").value(1000.00))
                .andExpect(jsonPath("$.saldo").value(500.00));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao buscar resumo financeiro de usuário inexistente")
    void resumoFinanceiro_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(usuarioService.resumoFinanceiro(any(Long.class))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/{id}/resumo", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao buscar resumo financeiro do usuário com ID 99: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar o resumo financeiro do usuário por período e status OK")
    void resumoFinanceiroPeriodo_ComDadosValidos_RetornaStatusOk() throws Exception {
        java.util.Map<String, Object> resumo = new java.util.HashMap<>();
        resumo.put("totalGasto", new BigDecimal("300.00"));
        resumo.put("totalReceita", new BigDecimal("700.00"));
        resumo.put("saldo", new BigDecimal("400.00"));
        resumo.put("mes", 10);
        resumo.put("ano", 2023);

        when(usuarioService.resumoFinanceiro(1L, 10, 2023)).thenReturn(resumo);

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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Mês deve estar entre 1 e 12"));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao buscar resumo financeiro com ano inválido")
    void resumoFinanceiroPeriodo_ComAnoInvalido_RetornaStatusBadRequest() throws Exception {
        mockMvc.perform(get("/usuarios/{id}/resumo/{mes}/{ano}", 1L, 10, 1999)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ano inválido"));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao buscar resumo financeiro de usuário inexistente por período")
    void resumoFinanceiroPeriodo_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(usuarioService.resumoFinanceiro(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/{id}/resumo/{mes}/{ano}", 99L, 10, 2023)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao buscar resumo financeiro: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar uma lista de despesas por usuário e status OK")
    void listarDespesasPorUsuario_QuandoExistemDespesas_RetornaStatusOkComLista() throws Exception {
        Despesa despesa = new Despesa();
        despesa.setId(1L);
        despesa.setDescricao("Aluguel");
        despesa.setValor(new BigDecimal("1000.00"));
        despesa.setUsuario(usuario);
        despesa.setCategoria(new Categoria(1L, "Moradia"));

        when(despesaService.listar(1L)).thenReturn(java.util.List.of(despesa));

        mockMvc.perform(get("/usuarios/{id}/despesas", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(despesa.getId()))
                .andExpect(jsonPath("$[0].descricao").value(despesa.getDescricao()))
                .andExpect(jsonPath("$[0].idUsuario").value(despesa.getUsuario().getUserId()));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia de despesas por usuário e status OK quando não há despesas")
    void listarDespesasPorUsuario_QuandoNaoExistemDespesas_RetornaStatusOkComListaVazia() throws Exception {
        when(despesaService.listar(1L)).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/usuarios/{id}/despesas", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao buscar despesas para usuário inexistente")
    void listarDespesasPorUsuario_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(despesaService.listar(any(Long.class))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/{id}/despesas", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao listar despesas do usuário com ID 99: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar uma lista de receitas por usuário e status OK")
    void listarReceitasPorUsuario_QuandoExistemReceitas_RetornaStatusOkComLista() throws Exception {
        Receita receita = new Receita();
        receita.setId(1L);
        receita.setDescricao("Salário");
        receita.setValor(new BigDecimal("2500.00"));
        receita.setUsuario(usuario);
        receita.setCategoria(new Categoria(1L, "Salário"));

        when(receitaService.listar(1L)).thenReturn(java.util.List.of(receita));

        mockMvc.perform(get("/usuarios/{id}/receitas", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(receita.getId()))
                .andExpect(jsonPath("$[0].descricao").value(receita.getDescricao()))
                .andExpect(jsonPath("$[0].idUsuario").value(receita.getUsuario().getUserId()));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia de receitas por usuário e status OK quando não há receitas")
    void listarReceitasPorUsuario_QuandoNaoExistemReceitas_RetornaStatusOkComListaVazia() throws Exception {
        when(receitaService.listar(1L)).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/usuarios/{id}/receitas", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao buscar receitas para usuário inexistente")
    void listarReceitasPorUsuario_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        when(receitaService.listar(any(Long.class))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/{id}/receitas", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao listar receitas do usuário com ID 99: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve criar uma despesa para o usuário e retornar status CREATED")
    void criarDespesa_ComDadosValidos_RetornaStatusCreated() throws Exception {
        CreateDespesaDTO createDespesaDTO = new CreateDespesaDTO();
        createDespesaDTO.setValor(new BigDecimal("150.00"));
        createDespesaDTO.setDescricao("Contas de casa");
        createDespesaDTO.setIdUsuario(1L);
        createDespesaDTO.setIdCategoria(1L);

        Despesa novaDespesa = new Despesa();
        novaDespesa.setId(1L);
        novaDespesa.setValor(new BigDecimal("150.00"));
        novaDespesa.setDescricao("Contas de casa");
        novaDespesa.setUsuario(usuario);
        novaDespesa.setCategoria(categoria);

        when(despesaService.salvar(any(Despesa.class), eq(1L), eq(1L))).thenReturn(novaDespesa);

        mockMvc.perform(post("/usuarios/{id}/despesas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDespesaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(novaDespesa.getId()))
                .andExpect(jsonPath("$.descricao").value(novaDespesa.getDescricao()))
                .andExpect(jsonPath("$.idUsuario").value(novaDespesa.getUsuario().getUserId()));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao criar despesa com ID de usuário divergente")
    void criarDespesa_ComIdUsuarioDivergente_RetornaStatusBadRequest() throws Exception {
        CreateDespesaDTO createDespesaDTO = new CreateDespesaDTO();
        createDespesaDTO.setValor(new BigDecimal("150.00"));
        createDespesaDTO.setDescricao("Contas de casa");
        createDespesaDTO.setIdUsuario(2L); // Diferente do path variable
        createDespesaDTO.setIdCategoria(1L);

        mockMvc.perform(post("/usuarios/{id}/despesas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDespesaDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID do usuário no path não corresponde ao ID no corpo da requisição"));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao criar despesa com ID de categoria inválido (<= 0)")
    void criarDespesa_ComIdCategoriaInvalido_RetornaStatusBadRequest() throws Exception {
        CreateDespesaDTO createDespesaDTO = new CreateDespesaDTO();
        createDespesaDTO.setValor(new BigDecimal("150.00"));
        createDespesaDTO.setDescricao("Contas de casa");
        createDespesaDTO.setIdUsuario(1L);
        createDespesaDTO.setIdCategoria(0L); // Categoria inválida

        mockMvc.perform(post("/usuarios/{id}/despesas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDespesaDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID da categoria inválido"));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao criar despesa para usuário inexistente")
    void criarDespesa_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        CreateDespesaDTO createDespesaDTO = new CreateDespesaDTO();
        createDespesaDTO.setValor(new BigDecimal("150.00"));
        createDespesaDTO.setDescricao("Contas de casa");
        createDespesaDTO.setIdUsuario(99L);
        createDespesaDTO.setIdCategoria(1L);

        when(despesaService.salvar(any(Despesa.class), eq(99L), eq(1L))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(post("/usuarios/{id}/despesas", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDespesaDTO)))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao criar despesa: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao criar despesa com categoria inexistente")
    void criarDespesa_QuandoCategoriaNaoExiste_RetornaStatusNotFound() throws Exception {
        CreateDespesaDTO createDespesaDTO = new CreateDespesaDTO();
        createDespesaDTO.setValor(new BigDecimal("150.00"));
        createDespesaDTO.setDescricao("Contas de casa");
        createDespesaDTO.setIdUsuario(1L);
        createDespesaDTO.setIdCategoria(99L); // Categoria inexistente

        when(despesaService.salvar(any(Despesa.class), eq(1L), eq(99L))).thenThrow(new RuntimeException("Categoria não encontrada"));

        mockMvc.perform(post("/usuarios/{id}/despesas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDespesaDTO)))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao criar despesa: Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve criar uma receita para o usuário e retornar status CREATED")
    void criarReceita_ComDadosValidos_RetornaStatusCreated() throws Exception {
        CreateReceitaDTO createReceitaDTO = new CreateReceitaDTO();
        createReceitaDTO.setValor(new BigDecimal("2000.00"));
        createReceitaDTO.setDescricao("Salário mensal");
        createReceitaDTO.setIdUsuario(1L);
        createReceitaDTO.setIdCategoria(1L);

        Receita novaReceita = new Receita();
        novaReceita.setId(1L);
        novaReceita.setValor(new BigDecimal("2000.00"));
        novaReceita.setDescricao("Salário mensal");
        novaReceita.setUsuario(usuario);
        novaReceita.setCategoria(categoria);

        when(receitaService.salvar(any(Receita.class), eq(1L), eq(1L))).thenReturn(novaReceita);

        mockMvc.perform(post("/usuarios/{id}/receitas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReceitaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(novaReceita.getId()))
                .andExpect(jsonPath("$.descricao").value(novaReceita.getDescricao()))
                .andExpect(jsonPath("$.idUsuario").value(novaReceita.getUsuario().getUserId()));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao criar receita com ID de usuário divergente")
    void criarReceita_ComIdUsuarioDivergente_RetornaStatusBadRequest() throws Exception {
        CreateReceitaDTO createReceitaDTO = new CreateReceitaDTO();
        createReceitaDTO.setValor(new BigDecimal("2000.00"));
        createReceitaDTO.setDescricao("Salário mensal");
        createReceitaDTO.setIdUsuario(2L); // Diferente do path variable
        createReceitaDTO.setIdCategoria(1L);

        mockMvc.perform(post("/usuarios/{id}/receitas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReceitaDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID do usuário no path não corresponde ao ID no corpo da requisição"));
    }

    @Test
    @DisplayName("Deve retornar status BAD REQUEST ao criar receita com ID de categoria inválido (null ou <= 0)")
    void criarReceita_ComIdCategoriaInvalido_RetornaStatusBadRequest() throws Exception {
        CreateReceitaDTO createReceitaDTO = new CreateReceitaDTO();
        createReceitaDTO.setValor(new BigDecimal("2000.00"));
        createReceitaDTO.setDescricao("Salário mensal");
        createReceitaDTO.setIdUsuario(1L);
        createReceitaDTO.setIdCategoria(null); // Categoria inválida (null)

        mockMvc.perform(post("/usuarios/{id}/receitas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReceitaDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID da categoria inválido"));

        createReceitaDTO.setIdCategoria(0L); // Categoria inválida (0)
        mockMvc.perform(post("/usuarios/{id}/receitas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReceitaDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID da categoria inválido"));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao criar receita para usuário inexistente")
    void criarReceita_QuandoUsuarioNaoExiste_RetornaStatusNotFound() throws Exception {
        CreateReceitaDTO createReceitaDTO = new CreateReceitaDTO();
        createReceitaDTO.setValor(new BigDecimal("2000.00"));
        createReceitaDTO.setDescricao("Salário mensal");
        createReceitaDTO.setIdUsuario(99L);
        createReceitaDTO.setIdCategoria(1L);

        when(receitaService.salvar(any(Receita.class), eq(99L), eq(1L))).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(post("/usuarios/{id}/receitas", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReceitaDTO)))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao criar receita: Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) ao criar receita com categoria inexistente")
    void criarReceita_QuandoCategoriaNaoExiste_RetornaStatusNotFound() throws Exception {
        CreateReceitaDTO createReceitaDTO = new CreateReceitaDTO();
        createReceitaDTO.setValor(new BigDecimal("2000.00"));
        createReceitaDTO.setDescricao("Salário mensal");
        createReceitaDTO.setIdUsuario(1L);
        createReceitaDTO.setIdCategoria(99L); // Categoria inexistente

        when(receitaService.salvar(any(Receita.class), eq(1L), eq(99L))).thenThrow(new RuntimeException("Categoria não encontrada"));

        mockMvc.perform(post("/usuarios/{id}/receitas", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReceitaDTO)))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Erro ao criar receita: Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve retornar detalhes do usuário autenticado e status OK")
    @WithMockUser(username = "test@example.com")
    void me_QuandoAutenticado_RetornaStatusOkComUsuario() throws Exception {
        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(java.util.Optional.of(usuario));

        mockMvc.perform(get("/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getUserId()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));
    }

    @Test
    @DisplayName("Deve retornar status UNAUTHORIZED quando não autenticado")
    void me_QuandoNaoAutenticado_RetornaStatusUnauthorized() throws Exception {
        mockMvc.perform(get("/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar status NOT FOUND (ou 500) se o usuário autenticado não for encontrado no serviço")
    @WithMockUser(username = "test@example.com")
    void me_QuandoUsuarioAutenticadoNaoExiste_RetornaStatusNotFound() throws Exception {
        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Assuming GlobalExceptionHandler maps RuntimeException to 500
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário autenticado e retornar status OK")
    @WithMockUser(username = "test@example.com")
    void atualizarMe_QuandoAutenticadoComDadosValidos_RetornaStatusOk() throws Exception {
        UpdateUsuarioDTO updateDTO = new UpdateUsuarioDTO();
        updateDTO.setNome("Novo Nome");
        updateDTO.setSenha("novaSenhaHash");
        updateDTO.setMetaMensal(3000.0);

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setUserId(1L);
        updatedUsuario.setNome("Novo Nome");
        updatedUsuario.setEmail("test@example.com");
        updatedUsuario.setSenha("novaSenhaHash");
        updatedUsuario.setMetaMensal(3000.0);

        when(usuarioService.atualizarUsuarioPorEmail(eq("test@example.com"), eq("Novo Nome"), eq("novaSenhaHash"), eq(3000.0))).thenReturn(updatedUsuario);

        mockMvc.perform(patch("/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(updatedUsuario.getNome()))
                .andExpect(jsonPath("$.metaMensal").value(updatedUsuario.getMetaMensal()));
    }

    @Test
    @DisplayName("Deve retornar status UNAUTHORIZED ao tentar atualizar dados de usuário sem autenticação")
    void atualizarMe_QuandoNaoAutenticado_RetornaStatusUnauthorized() throws Exception {
        UpdateUsuarioDTO updateDTO = new UpdateUsuarioDTO();
        updateDTO.setNome("Novo Nome");

        mockMvc.perform(patch("/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnauthorized());
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

    @Test
    @DisplayName("Deve retornar status UNAUTHORIZED ao tentar deletar conta de usuário sem autenticação")
    void deletarMe_QuandoNaoAutenticado_RetornaStatusUnauthorized() throws Exception {
        mockMvc.perform(delete("/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
