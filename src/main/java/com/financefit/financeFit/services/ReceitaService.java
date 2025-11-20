package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Receita;
import com.financefit.financeFit.repositories.ReceitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReceitaService {

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    public Receita salvar(Receita receita, int idUsuario, Long idCategoria) {
        receita.setUsuario(usuarioService.buscarPorId(idUsuario));
        receita.setCategoria(categoriaService.listarTodas()
                .stream()
                .filter(c -> Objects.equals(c.getCategoriaId(), idCategoria))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada")));
        return receitaRepository.save(receita);
    }

    public List<Receita> listar(int idUsuario) {
        return receitaRepository.findByUsuarioUserId(idUsuario);
    }

    public Receita buscarPorId(int id) {
        return receitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));
    }

    public Receita atualizar(int id, Receita receitaAtualizada, int idUsuario, Long idCategoria) {
        Receita receitaExistente = buscarPorId(id);
        receitaExistente.setValor(receitaAtualizada.getValor());
        receitaExistente.setData(receitaAtualizada.getData());
        receitaExistente.setDescricao(receitaAtualizada.getDescricao());
        receitaExistente.setUsuario(usuarioService.buscarPorId(idUsuario));
        receitaExistente.setCategoria(categoriaService.listarTodas()
                .stream()
                .filter(c -> Objects.equals(c.getCategoriaId(), idCategoria))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada")));

        return receitaRepository.save(receitaExistente);
    }

    public void deletar(int id) {
        receitaRepository.deleteById(id);
    }
}
