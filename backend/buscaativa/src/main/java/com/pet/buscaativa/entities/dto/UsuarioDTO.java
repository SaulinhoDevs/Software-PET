package com.pet.buscaativa.entities.dto;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.entities.enums.UnidadeAtuacao;

public record UsuarioDTO(
    Long id,
    String email,
    TipoUsuario tipoUsuario,
    UnidadeAtuacao unidadeAtuacao,
    String senha) {

    public UsuarioDTO(Usuario entity) {
        this(
            entity.getId(), 
            entity.getEmail(), 
            entity.getTipoUsuario(), 
            entity.getUnidadeAtuacao(),
            null
        );
    }
}
