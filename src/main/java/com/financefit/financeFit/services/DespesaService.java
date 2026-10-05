package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.entities.Despesa;
import com.financefit.financeFit.entities.Usuario;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.repositories.DespesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DespesaService {

    private final DespesaRepository despesaRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    public DespesaService(DespesaRepository despesaRepository,
                          UsuarioService usuarioService,
                          CategoriaService categoriaService) {
        this.despesaRepository = despesaRepository;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    @Transactional
    public Despesa salvar(Despesa despesa, Long idUsuario, Long idCategoria) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        Categoria categoria = categoriaService.buscarPorId(idCategoria);

        despesa.setUsuario(usuario);
        despesa.setCategoria(categoria);
        return despesaRepository.save(despesa);
    }

    @Transactional(readOnly = true)
    public List<Despesa> listar(Long idUsuario) {
        usuarioService.buscarPorId(idUsuario);
        return despesaRepository.findByUsuarioUserId(idUsuario);
    }

    @Transactional(readOnly = true)
    public Despesa buscarPorId(Long id) {
        return despesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada com ID: " + id));
    }

    @Transactional
    public Despesa atualizar(Long id, Despesa despesaAtualizada, Long idUsuario, Long idCategoria) {
        Despesa despesaExistente = buscarPorId(id);
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        Categoria categoria = categoriaService.buscarPorId(idCategoria);

        despesaExistente.setValor(despesaAtualizada.getValor());
        despesaExistente.setData(despesaAtualizada.getData());
        despesaExistente.setDescricao(despesaAtualizada.getDescricao());
        despesaExistente.setUsuario(usuario);
        despesaExistente.setCategoria(categoria);

        return despesaRepository.save(despesaExistente);
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        despesaRepository.deleteById(id);
    }
}
