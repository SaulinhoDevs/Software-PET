package com.pet.buscaativa.entities.dto;

import java.util.UUID;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.entities.enums.UnidadeAtuacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioDTO(
    UUID idPublico,
    
    @NotBlank
    @NotNull(message = "Digite o nome do profissional.")
    String nome,

    @NotBlank
    @NotNull(message = "O campo de e-mail não pode ficar vazio.")
    String email,


    @NotBlank
    @NotNull(message = "O tipo de usuário deve ser selecionado.")
    TipoUsuario tipoUsuario,

    @NotBlank
    @NotNull(message = "A unidade de atuação deve ser informada.")
    UnidadeAtuacao unidadeAtuacao,

    @NotBlank
    @NotNull(message = "O campo de senha não pode ficar vazio.")
    @Size(min = 6)
    String senha) {

    public UsuarioDTO(Usuario entity) {
        this(
            entity.getIdPublico(), 
            entity.getNome(),
            entity.getEmail(), 
            entity.getTipoUsuario(), 
            entity.getUnidadeAtuacao(),
            null
        );
    }
}
