package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Despesa;
import com.financefit.financeFit.repositories.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DespesaService {

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    public Despesa salvar(Despesa despesa, int idUsuario, int idCategoria) {
        despesa.setUsuario(usuarioService.buscarPorId(idUsuario));
        despesa.setCategoria(categoriaService.listarTodas()
                .stream()
                .filter(c -> c.getCategoriaId() == idCategoria)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada")));
        return despesaRepository.save(despesa);
    }

    public List<Despesa> listar(int idUsuario) {
        return despesaRepository.findAll()
                .stream()
                .filter(d -> d.getUsuario().getUserId() == idUsuario)
                .toList();
    }
}
