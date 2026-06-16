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

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService{

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;


    @Override
    public PacienteDTO save(PacienteDTO pacienteDTO, boolean ignorarSimilaridade) {
        validarPacienteDuplicado(pacienteDTO, ignorarSimilaridade);

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
    public void validarPacienteDuplicado(PacienteDTO pacienteDTO, boolean ignorarSimilaridade) {
        boolean possuiDoc = false;

        if (pacienteDTO.cpf() != null && !pacienteDTO.cpf().isBlank()) {
            possuiDoc = true;
            pacienteRepository.findByCpf(pacienteDTO.cpf()).ifPresent(p -> {
                if (!p.getId().equals(pacienteDTO.id())) {
                    throw new RecursoDuplicadoException("Já existe um paciente com este CPF.");
                }
            });
        }

        if (pacienteDTO.cns() != null && !pacienteDTO.cns().isBlank()) {
            possuiDoc = true;
            pacienteRepository.findByCns(pacienteDTO.cns()).ifPresent(p -> {
                if (!p.getId().equals(pacienteDTO.id())) {
                    throw new RecursoDuplicadoException("Já existe um paciente com este CNS.");
                }
            });
        }

        //REGRA RF15: CPF/CNS ausentes -> Busca por similaridade
        if (!possuiDoc && !ignorarSimilaridade) {
            List<Paciente> similares = pacienteRepository
                .findByNomeIgnoreCaseAndNomeMaeIgnoreCaseAndDataNascimento(
                    pacienteDTO.nome(), pacienteDTO.nomeMae(), pacienteDTO.dataNascimento()
                );

                similares.removeIf(p -> p.getId().equals(pacienteDTO.id()));

            if (!similares.isEmpty()) {
                List<PacienteDTO> similaresDTO = similares.stream().map(pacienteMapper::toPacienteDTO).toList();
                
                throw new DatabaseException(
                    "Registros similares encontrados. Confirme para prosseguir." + similaresDTO
                );
            }
        }
    }

    @Override
    public PacienteDTO findByCns(String cns) {
        Paciente paciente = pacienteRepository.findByCns(cns)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o CNS informado."));
        return pacienteMapper.toPacienteDTO(paciente);

    }

    @Override
    public PacienteDTO findByCpf(String cpf) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
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