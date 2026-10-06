package com.financefit.financeFit.conciliacao;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IdempotencyHelperTest {

    @Test
    void mesmaEntradaGeraMesmaChave() {
        String a = IdempotencyHelper.buildKey(1L, new BigDecimal("42.90"),
                LocalDate.of(2026, 10, 4), "iFood", "csv", null);
        String b = IdempotencyHelper.buildKey(1L, new BigDecimal("42.90"),
                LocalDate.of(2026, 10, 4), "iFood", "csv", null);

        assertNotNull(a);
        assertEquals(64, a.length());
        assertTrue(a.matches("[0-9a-f]{64}"));
        assertEquals(a, b);
    }

    @Test
    void normalizacaoIgnoraCaixaEspacoEAcento() {
        String base = IdempotencyHelper.buildKey(1L, new BigDecimal("42.90"),
                LocalDate.of(2026, 10, 4), "iFood  lanche", "csv", null);
        String variacao = IdempotencyHelper.buildKey(1L, new BigDecimal("42.90"),
                LocalDate.of(2026, 10, 4), "  IFOOD   lanche ", "csv", null);
        String acento = IdempotencyHelper.buildKey(1L, new BigDecimal("10.00"),
                LocalDate.of(2026, 10, 5), "ação", "manual", null);
        String semAcento = IdempotencyHelper.buildKey(1L, new BigDecimal("10.00"),
                LocalDate.of(2026, 10, 5), "acao", "manual", null);

        assertEquals(base, variacao);
        assertEquals(acento, semAcento);
    }

    @Test
    void contaDistintaGeraChaveDistinta() {
        String nubank = IdempotencyHelper.buildKey(1L, new BigDecimal("42.90"),
                LocalDate.of(2026, 10, 4), "iFood", "csv", null);
        String inter = IdempotencyHelper.buildKey(2L, new BigDecimal("42.90"),
                LocalDate.of(2026, 10, 4), "iFood", "csv", null);

        assertNotEquals(nubank, inter);
    }
}
