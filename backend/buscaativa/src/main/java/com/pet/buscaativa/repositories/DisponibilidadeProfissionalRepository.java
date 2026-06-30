package com.pet.buscaativa.repositories;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.buscaativa.entities.DisponibilidadeProfissional;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.enums.TurnoEnum;

@Repository
public interface DisponibilidadeProfissionalRepository extends JpaRepository<DisponibilidadeProfissional, Long>{
    
    //Busca a disponibilidade do profissional pelo dia da semana e o turno
    Optional<DisponibilidadeProfissional> findByUsuarioAndDiaSemanaAndTurno(Usuario usuario, DayOfWeek diaSemana, TurnoEnum turno);

    //Busca a disponibilidade do profissional pelo dia da semana 
    List<DisponibilidadeProfissional> findByUsuarioAndDiaSemana(Usuario usuario, DayOfWeek diaSemana);
}
