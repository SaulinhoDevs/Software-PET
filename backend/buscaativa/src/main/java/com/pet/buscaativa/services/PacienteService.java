package com.pet.buscaativa.services;

import com.pet.buscaativa.entities.dto.PacienteDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PacienteService {

    PacienteDTO save(PacienteDTO pacienteDTO);


    List<PacienteDTO> findAll();
    PacienteDTO findById(Long id);
    PacienteDTO findByCNS(String CNS);
    PacienteDTO findByCPF(String CPF);
    List<PacienteDTO> findByNome(String nome);
    PacienteDTO findByNomeMae(String nomeMae);
    void inativarPaciente(Long id);
    void validarPacienteDuplicado(String CNS, Long id);

}