package com.pet.buscaativa.services.impl;

import java.util.List;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.UsuarioDTO;
import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.mapping.UsuarioMapper;
import com.pet.buscaativa.repositories.UsuarioRepository;
import com.pet.buscaativa.services.UsuarioService;
import com.pet.buscaativa.services.exceptions.DatabaseException;
import com.pet.buscaativa.services.exceptions.RecursoDuplicadoException;
import com.pet.buscaativa.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioDTO save(UsuarioDTO usuarioDTO) {
        validarEmailDuplicado(usuarioDTO.email(), usuarioDTO.id());

        Usuario usuarioSalvar;

        if (usuarioDTO.id() != null) {
            usuarioSalvar = usuarioRepository.findById(usuarioDTO.id())
                    .orElseThrow(() -> new DatabaseException("Usuário não encontrado!"));

            usuarioSalvar.setEmail(usuarioDTO.email());
            usuarioSalvar.setTipoUsuario(usuarioDTO.tipoUsuario());
            usuarioSalvar.setUnidadeAtuacao(usuarioDTO.unidadeAtuacao());

        } else {
            usuarioSalvar = usuarioMapper.toUsuarioEntity(usuarioDTO);

            if (usuarioRepository.count() == 0) {
                usuarioSalvar.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            }
        }

        if (usuarioDTO.senha() != null && !usuarioDTO.senha().isBlank()) {
            usuarioSalvar.setSenha(passwordEncoder.encode(usuarioDTO.senha()));
        }

        usuarioSalvar = usuarioRepository.save(usuarioSalvar);

        return usuarioMapper.toUsuarioDTO(usuarioSalvar);
    }

    @Override
    public List<UsuarioDTO> findAll() {
        List<Usuario> list = usuarioRepository.findAll();
        return list.stream()
                .map(UsuarioDTO::new)
                .toList();
    }

    @Override
    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new UsuarioDTO(usuario);
    }

    public void validarEmailDuplicado(String email, Long id) {
        usuarioRepository.findByEmail(email).ifPresent(
                usuario -> {
                    if (!usuario.getId().equals(id)) {
                        throw new RecursoDuplicadoException("E-mail já cadastrado no sistema!");
                    }
                });
    }

    @Override
    public UsuarioDTO findByEmail(String email) {
        var usuarioEntity = usuarioRepository.findByEmail(email).orElse(null);
        if (usuarioEntity == null) {
            return null;
        }

        return usuarioMapper.toUsuarioDTO(usuarioEntity);
    }

}
