package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Despesa;
import com.financefit.financeFit.repositories.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DespesaService {

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    public Despesa salvar(Despesa despesa, int idUsuario, Long idCategoria) { // Alterado para Long
        despesa.setUsuario(usuarioService.buscarPorId(idUsuario));
        despesa.setCategoria(categoriaService.listarTodas()
                .stream()
                .filter(c -> Objects.equals(c.getCategoriaId(), idCategoria)) // Usar Objects.equals para comparar Long
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada")));
        return despesaRepository.save(despesa);
    }

    public List<Despesa> listar(int idUsuario) {
        return despesaRepository.findByUsuarioUserId(idUsuario);
    }

    public Despesa buscarPorId(int id) {
        return despesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));
    }

    public Despesa atualizar(int id, Despesa despesaAtualizada, int idUsuario, Long idCategoria) { // Alterado para Long
        Despesa despesaExistente = buscarPorId(id);
        despesaExistente.setValor(despesaAtualizada.getValor());
        despesaExistente.setData(despesaAtualizada.getData());
        despesaExistente.setDescricao(despesaAtualizada.getDescricao());
        despesaExistente.setUsuario(usuarioService.buscarPorId(idUsuario)); // Ensure user is the same or updated
        despesaExistente.setCategoria(categoriaService.listarTodas()
                .stream()
                .filter(c -> Objects.equals(c.getCategoriaId(), idCategoria)) // Usar Objects.equals para comparar Long
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada")));
        return despesaRepository.save(despesaExistente);
    }

    public void deletar(int id) {
        despesaRepository.deleteById(id);
    }
}
