package br.com.flordemandacaru.model;

import java.util.regex.Pattern;

final class CodigoCatalogo {
    private CodigoCatalogo() {
    }

    static String validar(String codigo, String prefixo) {
        if (codigo == null || codigo.trim().length() > 20
                || !Pattern.matches(prefixo + "-\\d{3,}", codigo.trim()))
            throw new IllegalArgumentException("código deve seguir o padrão " + prefixo + "-001");
        return codigo.trim();
    }
}
