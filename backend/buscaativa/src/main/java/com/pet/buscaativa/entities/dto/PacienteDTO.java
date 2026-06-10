package com.pet.buscaativa.entities.dto;

import java.time.LocalDate;

import com.pet.buscaativa.entities.Endereco;
import com.pet.buscaativa.entities.Paciente;
import com.pet.buscaativa.entities.UsfReferencia;
import com.pet.buscaativa.entities.enums.RacaCorEnum;
import com.pet.buscaativa.entities.enums.SexoEnum;
import com.pet.buscaativa.entities.enums.StatusPaciente;
import com.pet.buscaativa.entities.enums.TipoAcompanhamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PacienteDTO(
    Long id,
    @NotBlank
    @NotNull(message = "Informe o nome do Paciente.")
    String nome,
    @NotBlank
    @NotNull(message = "Informe o nome da Mãe do Paciente.")
    String nomeMae,
    @NotBlank
    @NotNull(message = "Informe a data de Nascimento do Paciente.")
    LocalDate dataNascimento,
    LocalDate dataUltimaPresenca,
    @NotBlank
    @NotNull(message = "Informe o Sexo do Paciente.")
    SexoEnum sexo,
    @NotBlank
    @NotNull(message = "Informe a Raça/Cor do Paciente.")
    RacaCorEnum racacor,
    @NotBlank
    @NotNull(message = "Informe o CNS do paciente.")
    String CNS,
    @NotBlank
    @NotNull(message = "Informe o CPF do Paciente.")
    String CPF,
    @NotBlank
    @NotNull(message = "Informe o número de Telefone do Paciente")
    String telefone,
    @NotBlank
    @NotNull(message = "Informe o Endereço do Paciente ou Equipamento Social de Apoio.")
    Endereco endereco,
    @NotBlank
    @NotNull(message = "Informe se o Paciente está em situação de Rua ou não.")
    boolean situacaoRua,
    @NotBlank
    @NotNull(message = "Informe o Tipo de Acompanhamento do Paciente.")
    TipoAcompanhamento tipoAcompanhamento,
    int countFaltas,
    StatusPaciente statusPaciente,
    @NotBlank
    @NotNull(message = "Informe a USF de Referência do Paciente.")
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
