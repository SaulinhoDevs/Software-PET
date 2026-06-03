package com.pet.buscaativa.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UnidadeAtuacao {
    
    USF(1),
    CAPS_AD(2),
    CAPS_I(3),
    CAPS_II(4);

    private int codigo;

    UnidadeAtuacao(int codigo) {
        this.codigo = codigo;
    }

    @JsonValue
    public int getCodigo() {
        return codigo;
    }

    public static UnidadeAtuacao valueOf(int codigo) {
        for (UnidadeAtuacao value : UnidadeAtuacao.values()) {
            if (value.getCodigo() == codigo) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código da Unidade Inválido!");
    }
}
