package com.pet.buscaativa.mapping;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.UsuarioDTO;

public interface UsuarioMapper {
    
    public UsuarioDTO toUsuarioDTO(Usuario usuarioEntity);

    public Usuario toUsuarioEntity(UsuarioDTO UsuarioDTO);
}
