package com.pet.buscaativa.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.pet.buscaativa.entities.Paciente;
import com.pet.buscaativa.entities.dto.PacienteDTO;

@Mapper(componentModel = "spring")
public interface PacienteMapper {
    public PacienteDTO toPacienteDTO(Paciente pacienteEntity);

    public Paciente toPacienteEntity(PacienteDTO pacienteDTO);

    void updatePacienteFromDTO(PacienteDTO pacienteDTO, @MappingTarget Paciente pacienteEntity);
}
