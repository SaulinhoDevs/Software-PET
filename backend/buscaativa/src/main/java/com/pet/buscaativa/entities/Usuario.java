package com.pet.buscaativa.entities;

import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.entities.enums.UnidadeAtuacao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "tb_usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String senha;

    private Integer tipoUsuario;

    private Integer unidadeAtuacao;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.valueOf(tipoUsuario);
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        if (tipoUsuario != null) {
            this.tipoUsuario = tipoUsuario.getCodigo();
        }
    }

    public UnidadeAtuacao getUnidadeAtuacao(){
        return UnidadeAtuacao.valueOf(unidadeAtuacao);
    }

    public void setUnidadeAtuacao(UnidadeAtuacao unidadeAtuacao) {
        if (unidadeAtuacao != null) {
            this.unidadeAtuacao = unidadeAtuacao.getCodigo();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
