package com.pet.buscaativa.entities;

import com.pet.buscaativa.entities.enums.TipoUsuario;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String senha;

    private Integer tipoUsuario;

    public Usuario() {
    }

    public Usuario(Long id, String email, String senha, TipoUsuario tipoUsuario) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        setTipoUsuario(tipoUsuario);
    }

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
