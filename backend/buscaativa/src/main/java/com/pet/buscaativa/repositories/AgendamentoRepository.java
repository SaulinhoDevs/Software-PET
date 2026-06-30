package com.pet.buscaativa.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.buscaativa.entities.Agendamento;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.enums.TurnoEnum;
import com.pet.buscaativa.entities.enums.SituacaoAtendimento;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long>{
    
    List<Agendamento>findByDataAgendamento(LocalDate dataAgendamento);

    //Responsabel por contar quantos agendamentos de um usuario fem em uma determinada data para um determinado turno analisando
    //Se o paciente foi presente, faltou ou foi remarcado
    int countByUsuarioAndDataAgendamentoAndTurnoAgendamentoAndSituacaoAtendimentoNot(
        Usuario usuario, LocalDate dataAgendamento, TurnoEnum turnoAgendamento, SituacaoAtendimento situacaoAtendimento);
}
