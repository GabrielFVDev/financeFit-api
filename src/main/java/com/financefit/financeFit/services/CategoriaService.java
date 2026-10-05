package com.financefit.financeFit.services;

import com.financefit.financeFit.entities.Categoria;
import com.financefit.financeFit.exception.ResourceNotFoundException;
import com.financefit.financeFit.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }

    @Transactional
    public Categoria salvar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, Categoria categoriaAtualizada) {
        Categoria categoriaExistente = buscarPorId(id);
        categoriaExistente.setNome(categoriaAtualizada.getNome());
        return categoriaRepository.save(categoriaExistente);
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        categoriaRepository.deleteById(id);
    }
}
