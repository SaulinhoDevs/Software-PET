package com.pet.buscaativa.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pet.buscaativa.entities.Agendamento;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.enums.SituacaoAtendimento;
import com.pet.buscaativa.entities.enums.TurnoEnum;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long>{
    
    List<Agendamento>findByDataAgendamento(LocalDate dataAgendamento);

    //Responsabel por contar quantos agendamentos de um usuario fem em uma determinada data para um determinado turno analisando

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.usuario = :usuario AND a.dataAgendamento = :data AND a.turnoAgendamento = :turno AND a.situacaoAtendimento <> :situacao")
    int contarVagasOcupadas(@Param("usuario") Usuario usuario, 
                            @Param("data") LocalDate dataAgendamento, 
                            @Param("turno") TurnoEnum turnoAgendamento, 
                            @Param("situacao") SituacaoAtendimento situacao);
}
