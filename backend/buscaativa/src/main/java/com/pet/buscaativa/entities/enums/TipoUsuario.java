package com.pet.buscaativa.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoUsuario {

    ADMINISTRADOR(1),
    MEDICO(2),
    ENFERMEIRO(3),
    DENTISTA(4),
    TECNICO_ENFERMAGEM(5),
    RECEPCAO(6);

    private int codigo;

    TipoUsuario(int codigo) {
        this.codigo = codigo;
    }

    @JsonValue
    public int getCodigo() {
        return codigo;
    }

    public static TipoUsuario valueOf(int codigo) {
        for (TipoUsuario value : TipoUsuario.values()) {
            if (value.getCodigo() == codigo) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de Usuário Inválido!");
    }

}
