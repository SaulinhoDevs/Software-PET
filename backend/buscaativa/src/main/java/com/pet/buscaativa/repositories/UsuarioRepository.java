package com.pet.buscaativa.repositories;

import com.pet.buscaativa.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
