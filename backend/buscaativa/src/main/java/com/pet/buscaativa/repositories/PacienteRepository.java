package com.pet.buscaativa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.buscaativa.entities.Paciente;

import java.util.List;
import java.util.Optional;


@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long>{
 
    Optional<Paciente> findByCNS(String CNS);

    Optional<Paciente> findByCPF(String CPF);

    Optional<Paciente> findByNome(String nome);

    Optional<Paciente> findByNomeMae(String nomeMae);

    List<Paciente> findByNomeContainingIgnoreCase (String nome);    
}
