package com.pet.buscaativa.services.impl;

import com.pet.buscaativa.entities.Paciente;
import com.pet.buscaativa.entities.dto.PacienteDTO;
import com.pet.buscaativa.entities.enums.StatusPaciente;
import com.pet.buscaativa.mapping.PacienteMapper;
import com.pet.buscaativa.repositories.PacienteRepository;
import com.pet.buscaativa.services.PacienteService;
import com.pet.buscaativa.services.exceptions.DatabaseException;
import com.pet.buscaativa.services.exceptions.RecursoDuplicadoException;
import com.pet.buscaativa.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService{

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;


    @Override
    public PacienteDTO save(PacienteDTO pacienteDTO) {
        validarPacienteDuplicado(pacienteDTO.CNS(), pacienteDTO.id());

        Paciente pacienteSalvar;

        if(pacienteDTO.id() != null){
            pacienteSalvar = pacienteRepository.findById(pacienteDTO.id())
                    .orElseThrow(() -> new DatabaseException("Paciente não encontrado!"));

            pacienteMapper.updatePacienteFromDTO(pacienteDTO, pacienteSalvar);
        }else{
            pacienteSalvar = pacienteMapper.toPacienteEntity(pacienteDTO);

            pacienteSalvar.setStatusPaciente(StatusPaciente.ATIVO);
        }

        pacienteSalvar = pacienteRepository.save(pacienteSalvar);

        return pacienteMapper.toPacienteDTO(pacienteSalvar);

    }
    @Override
    public void inativarPaciente(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new DatabaseException("Paciente não encontrado!"));

        paciente.setStatusPaciente(StatusPaciente.INATIVO);

        pacienteRepository.save(paciente);
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
    @Override
    public void validarPacienteDuplicado(String CNS, Long id) {
        pacienteRepository.findByCNS(CNS).ifPresent(
            paciente -> {
                if(!paciente.getId().equals(id)){
                    throw new RecursoDuplicadoException("Paciente já cadastrado no sistema!");
                }
            }
        );
    }

    @Override
    public PacienteDTO findByCNS(String CNS) {
        Paciente paciente = pacienteRepository.findByCNS(CNS)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o CNS informado."));
        return pacienteMapper.toPacienteDTO(paciente);

    }

    @Override
    public PacienteDTO findByCPF(String CPF) {
        Paciente paciente = pacienteRepository.findByCPF(CPF)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o CPF informado."));
        return pacienteMapper.toPacienteDTO(paciente);

    }

    @Override
    public List<PacienteDTO> findByNome(String nome) {
        List<Paciente> pacientes = pacienteRepository.findByNomeContainingIgnoreCase(nome);
        
        return pacientes.stream()
                .map(pacienteMapper::toPacienteDTO)
                .toList();

    }

    @Override
    public PacienteDTO findByNomeMae(String nomeMae) {
        var paciente = pacienteRepository.findByNomeMae(nomeMae).orElse(null);

        if(paciente == null){
            return null;
        }

        return pacienteMapper.toPacienteDTO(paciente);
    }

    
}