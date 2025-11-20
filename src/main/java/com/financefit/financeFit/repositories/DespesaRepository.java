package com.financefit.financeFit.repositories;

import com.financefit.financeFit.entities.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Integer> {
    @Query("SELECT SUM(d.valor) FROM Despesa d WHERE d.usuario.userId = :idUsuario AND MONTH(d.data) = :mes AND YEAR(d.data) = :ano")
    BigDecimal calcularTotalGastoNoMes(
            @Param("idUsuario") int idUsuario,
            @Param("mes") int mes,
            @Param("ano") int ano
    );

    @Query("SELECT d FROM Despesa d JOIN FETCH d.usuario JOIN FETCH d.categoria WHERE d.usuario.userId = :idUsuario")
    List<Despesa> findByUsuarioUserId(@Param("idUsuario") int idUsuario);
}
