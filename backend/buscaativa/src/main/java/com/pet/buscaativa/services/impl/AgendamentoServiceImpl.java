package com.pet.buscaativa.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pet.buscaativa.entities.dto.AgendamentoDTO;
import com.pet.buscaativa.services.AgendamentoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService{

    @Override
    public AgendamentoDTO save(AgendamentoDTO agendamentoDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public int calcularVagasDisponiveis(Long usuarioId, LocalDate data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calcularVagasDisponiveis'");
    }

    @Override
    public List<AgendamentoDTO> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
    
}
