package com.pet.buscaativa.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pet.buscaativa.entities.Usuario;

import jakarta.persistence.LockModeType;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByIdPublico(UUID idPublico);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.idPublico = :idPublico")
    Optional<Usuario> findByIdPublicoForUpdate(@Param("idPublico") UUID idPublico);
}
