package com.financefit.financeFit.conciliacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Locale;

public final class IdempotencyHelper {

    private IdempotencyHelper() {}

    public static String buildKey(Long contaId, BigDecimal valor, LocalDate data,
                                  String descricao, String fonte, String externalId) {
        String cents = valor.setScale(2, RoundingMode.HALF_UP).movePointRight(2).toPlainString();
        String raw = contaId + "|" + cents + "|" + data.toString()
                + "|" + normalize(descricao) + "|" + normalize(fonte) + "|" + normalize(externalId);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }

    static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String lower = input.trim().toLowerCase(Locale.ROOT);
        String decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD);
        String noAccent = decomposed.replaceAll("\\p{M}", "");
        return noAccent.replaceAll("\\s+", " ").trim();
    }
}
