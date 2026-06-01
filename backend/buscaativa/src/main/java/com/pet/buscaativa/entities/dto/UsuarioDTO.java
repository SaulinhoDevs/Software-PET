package com.pet.buscaativa.entities.dto;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.enums.TipoUsuario;

public class UsuarioDTO {

    private Long id;
    private String email;
    private TipoUsuario tipoUsuario;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Usuario entity) {
        id = entity.getId();
        email = entity.getEmail();
        tipoUsuario = entity.getTipoUsuario();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }
}
