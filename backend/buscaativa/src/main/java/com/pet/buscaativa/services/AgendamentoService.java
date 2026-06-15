package com.pet.buscaativa.services;

import java.time.LocalDate;
import java.util.List;

import com.pet.buscaativa.entities.dto.AgendamentoDTO;

public interface AgendamentoService {


    AgendamentoDTO save(AgendamentoDTO agendamentoDTO);

    int calcularVagasDisponiveis(Long usuarioId, LocalDate data);

    List<AgendamentoDTO> findAll();
}