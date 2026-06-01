package com.pet.buscaativa.services;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.UsuarioDTO;
import com.pet.buscaativa.repositories.UsuarioRepository;
import com.pet.buscaativa.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> findAll() {
        List<Usuario> list = usuarioRepository.findAll();
        return list.stream()
                .map(UsuarioDTO::new)
                .toList();
    }

    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new UsuarioDTO(usuario);
    }
}
