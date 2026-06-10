package com.pet.buscaativa.services.impl;

import com.pet.buscaativa.entities.Paciente;
import com.pet.buscaativa.entities.dto.PacienteDTO;
import com.pet.buscaativa.mapping.PacienteMapper;
import com.pet.buscaativa.repositories.PacienteRepository;
import com.pet.buscaativa.services.PacienteService;
import com.pet.buscaativa.services.exceptions.DatabaseException;
import com.pet.buscaativa.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService{

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;


    @Override
    public PacienteDTO save(PacienteDTO pacienteDTO) {

        Paciente pacienteSalvar;

        if(pacienteDTO.id() != null){
            pacienteSalvar = pacienteRepository.findById(pacienteDTO.id())
                    .orElseThrow(() -> new DatabaseException("Paciente não encontrado!"));

            pacienteSalvar.setNome(pacienteDTO.nome());
            pacienteSalvar.setNomeMae(pacienteDTO.nomeMae());
            pacienteSalvar.setDataNascimento(pacienteDTO.dataNascimento());
            pacienteSalvar.setDataUltimaPresenca(pacienteDTO.dataUltimaPresenca());
            pacienteSalvar.setSexo(pacienteDTO.sexo());
            pacienteSalvar.setRacacor(pacienteDTO.racacor());
            pacienteSalvar.setCNS(pacienteDTO.CNS());
            pacienteSalvar.setCPF(pacienteDTO.CPF());
            pacienteSalvar.setTelefone(pacienteDTO.telefone());
            pacienteSalvar.setEndereco(pacienteDTO.endereco());
            pacienteSalvar.setSituacaoRua(pacienteDTO.situacaoRua());
            pacienteSalvar.setTipoAcompanhamento(pacienteDTO.tipoAcompanhamento());
            pacienteSalvar.setCountFaltas(pacienteDTO.countFaltas());
            pacienteSalvar.setStatusPaciente(pacienteDTO.statusPaciente());
            pacienteSalvar.setUsfReferencia(pacienteDTO.usfReferencia());
        }else{
            pacienteSalvar = pacienteMapper.toPacienteEntity(pacienteDTO);
        }

        pacienteSalvar = pacienteRepository.save(pacienteSalvar);

        return pacienteMapper.toPacienteDTO(pacienteSalvar);

    }

    @Override
    public PacienteDTO findById(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new PacienteDTO(paciente);
    }

    @Override
    public List<PacienteDTO> findAll() {
        List<Paciente> list = pacienteRepository.findAll();
        return list.stream()
                .map(PacienteDTO::new)
                .toList();
    }
}