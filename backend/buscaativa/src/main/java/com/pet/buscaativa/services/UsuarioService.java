package com.pet.buscaativa.services;

import com.pet.buscaativa.entities.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsuarioService {

    UsuarioDTO save(UsuarioDTO usuarioDTO);


    public List<UsuarioDTO> findAll();

    public UsuarioDTO findById(Long id);

    public UsuarioDTO findByEmail(String email);

    public void validarEmailDuplicado(String email, Long id);
}
