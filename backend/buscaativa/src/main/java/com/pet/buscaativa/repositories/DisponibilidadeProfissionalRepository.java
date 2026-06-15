package com.pet.buscaativa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.buscaativa.entities.DisponibilidadeProfissional;

@Repository
public interface DisponibilidadeProfissionalRepository extends JpaRepository<DisponibilidadeProfissional, Long>{
    
}
