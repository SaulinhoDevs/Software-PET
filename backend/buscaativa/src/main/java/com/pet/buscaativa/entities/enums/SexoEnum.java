package com.pet.buscaativa.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SexoEnum {
 
    FEMININO(1),
    MASCULINO(2),
    INTERSEXO(3),
    OUTRO(4),
    NAO_INFORMADO(5);

    private int codigo;

    SexoEnum(int codigo) {
        this.codigo = codigo;
    }

    @JsonValue
    public int getCodigo() {
        return codigo;
    }

    public static SexoEnum valueOf(int codigo) {
        for (SexoEnum value : SexoEnum.values()) {
            if (value.getCodigo() == codigo) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de Sexo não identificado!");
    }
}
