package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.entities.Receita;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.repositories.ReceitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    public ReceitaService(ReceitaRepository receitaRepository,
                          UsuarioService usuarioService,
                          CategoriaService categoriaService) {
        this.receitaRepository = receitaRepository;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    @Transactional
    public Receita salvar(Receita receita, Long idUsuario, Long idCategoria) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        Categoria categoria = categoriaService.buscarPorId(idCategoria);

        receita.setUsuario(usuario);
        receita.setCategoria(categoria);
        return receitaRepository.save(receita);
    }

    @Transactional(readOnly = true)
    public List<Receita> listar(Long idUsuario) {
        usuarioService.buscarPorId(idUsuario);
        return receitaRepository.findByUsuarioUserId(idUsuario);
    }

    @Transactional(readOnly = true)
    public Receita buscarPorId(Long id) {
        return receitaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receita não encontrada com ID: " + id));
    }

    @Transactional
    public Receita atualizar(Long id, Receita receitaAtualizada, Long idUsuario, Long idCategoria) {
        Receita receitaExistente = buscarPorId(id);
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        Categoria categoria = categoriaService.buscarPorId(idCategoria);

        receitaExistente.setValor(receitaAtualizada.getValor());
        receitaExistente.setData(receitaAtualizada.getData());
        receitaExistente.setDescricao(receitaAtualizada.getDescricao());
        receitaExistente.setUsuario(usuario);
        receitaExistente.setCategoria(categoria);

        return receitaRepository.save(receitaExistente);
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        receitaRepository.deleteById(id);
    }
}
