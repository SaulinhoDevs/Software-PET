package com.pet.buscaativa.entities.dto;

import java.time.DayOfWeek;

import com.pet.buscaativa.entities.enums.TurnoEnum;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadeDTO(

        Long id,

        Long usuarioId,

        @NotNull 
        DayOfWeek diaSemana,

        @NotNull 
        TurnoEnum turno,

        @NotNull 
        Integer capacidade
        
) {

}