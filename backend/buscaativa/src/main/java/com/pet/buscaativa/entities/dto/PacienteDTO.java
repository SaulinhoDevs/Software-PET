package com.pet.buscaativa.entities.dto;

import java.time.LocalDate;

import com.pet.buscaativa.entities.Endereco;
import com.pet.buscaativa.entities.Paciente;
import com.pet.buscaativa.entities.UsfReferencia;
import com.pet.buscaativa.entities.enums.RacaCorEnum;
import com.pet.buscaativa.entities.enums.SexoEnum;
import com.pet.buscaativa.entities.enums.StatusPaciente;
import com.pet.buscaativa.entities.enums.TipoAcompanhamento;

public record PacienteDTO(
    Long id,
    String nome,
    String nomeMae,
    LocalDate dataNascimento,
    LocalDate dataUltimaPresenca,
    SexoEnum sexo,
    RacaCorEnum racacor,
    String CNS,
    String CPF,
    String telefone,
    Endereco endereco,
    boolean situacaoRua,
    TipoAcompanhamento tipoAcompanhamento,
    int countFaltas,
    StatusPaciente statusPaciente,
    UsfReferencia usfReferencia

) {
    public PacienteDTO(Paciente entity) {
        this(
            entity.getId(), 
            entity.getNome(),
            entity.getNomeMae(), 
            entity.getDataNascimento(),
            entity.getDataUltimaPresenca(),
            entity.getSexo(),
            entity.getRacacor(),
            entity.getCNS(),
            entity.getCPF(),
            entity.getTelefone(),
            entity.getEndereco(),
            entity.isSituacaoRua(),
            entity.getTipoAcompanhamento(),
            entity.getCountFaltas(),
            entity.getStatusPaciente(),
            entity.getUsfReferencia()
        );
    }
    
}
