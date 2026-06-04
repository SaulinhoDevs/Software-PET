package com.pet.buscaativa.mapping;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.UsuarioDTO;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    
    public UsuarioDTO toUsuarioDTO(Usuario usuarioEntity);

    public Usuario toUsuarioEntity(UsuarioDTO UsuarioDTO);
}
