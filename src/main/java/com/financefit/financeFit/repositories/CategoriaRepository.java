package com.financefit.financeFit.repositories;

import com.financefit.financeFit.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
