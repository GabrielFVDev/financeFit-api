package com.financefit.financeFit.repositories;

import com.financefit.financeFit.entities.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {
    @Query("SELECT SUM(r.valor) FROM Receita r WHERE r.usuario.userId = :idUsuario AND MONTH(r.data) = :mes AND YEAR(r.data) = :ano")
    BigDecimal calcularTotalReceitaNoMes(
            @Param("idUsuario") Long idUsuario,
            @Param("mes") int mes,
            @Param("ano") int ano
    );

    @Query("SELECT r FROM Receita r JOIN FETCH r.usuario LEFT JOIN FETCH r.categoria WHERE r.usuario.userId = :idUsuario")
    List<Receita> findByUsuarioUserId(@Param("idUsuario") Long idUsuario);
}
