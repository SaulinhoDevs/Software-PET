package com.pet.buscaativa.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusPaciente {

    ALTA_TERAPEUTICA(1),
    TRANSFERIDO(2),
    ABANDONO_TRATAMENTO(3),
    OBITO(4),
    OUTRO(5),
    INATIVO(6);

    private int codigo;

    StatusPaciente(int codigo) {
        this.codigo = codigo;
    }

    @JsonValue
    public int getCodigo() {
        return codigo;
    }

    public static StatusPaciente valueOf(int codigo) {
        for (StatusPaciente value : StatusPaciente.values()) {
            if (value.getCodigo() == codigo) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de Status inválido!");
    }
}
