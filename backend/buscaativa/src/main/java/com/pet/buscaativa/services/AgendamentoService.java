package com.pet.buscaativa.services;

import java.time.LocalDate;
import java.util.List;

import com.pet.buscaativa.entities.dto.AgendamentoDTO;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.enums.SituacaoAtendimento;
import com.pet.buscaativa.entities.enums.TurnoEnum;

public interface AgendamentoService {


    AgendamentoDTO save(AgendamentoDTO agendamentoDTO);

    int calcularVagasDisponiveis(Long usuarioId, LocalDate data);

    List<AgendamentoDTO> findAll();

    List<LocalDate> buscarProximasVagasDisponiveis(Usuario usuario, TurnoEnum turno, LocalDate dataInicio, int quantidadeVagas);

// RF05: Busca a agenda de um dia específico
    List<AgendamentoDTO> buscarAgendaDoDia(LocalDate data);

    // RF06: Registra presença, falta ou remarcação
    AgendamentoDTO atualizarStatus(Long id, SituacaoAtendimento novoStatus);

}