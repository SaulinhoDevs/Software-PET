package com.pet.buscaativa.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pet.buscaativa.entities.Disponibilidade;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.DisponibilidadeDTO;
import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.repositories.DisponibilidadeRepository;
import com.pet.buscaativa.repositories.UsuarioRepository;
import com.pet.buscaativa.services.DisponibilidadeService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DisponibilidadeServiceImpl implements DisponibilidadeService{

    private final DisponibilidadeRepository disponibilidadeRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public DisponibilidadeDTO save(DisponibilidadeDTO disponibilidadeDTO, String emailLogado) {
        Usuario usuario = determinarUsuarioAlvo(disponibilidadeDTO.usuarioId(), emailLogado);

        Disponibilidade disponibilidade = new Disponibilidade();
        if (disponibilidadeDTO.id() != null) {
            disponibilidade = disponibilidadeRepository.findById(disponibilidadeDTO.id()).orElseThrow();
        }
        disponibilidade.setUsuario(usuario);
        disponibilidade.setDiaDaSemana(disponibilidadeDTO.diaSemana());
        disponibilidade.setTurno(disponibilidadeDTO.turno());
        disponibilidade.setCapacidade(disponibilidadeDTO.capacidade());

        disponibilidade = disponibilidadeRepository.save(disponibilidade);
        return new DisponibilidadeDTO(disponibilidade.getId(), usuario.getId(), disponibilidade.getDiaDaSemana(), disponibilidade.getTurno(), disponibilidade.getCapacidade());
    }

    @Override
    public List<DisponibilidadeDTO> listarDisponibilidades(String emailLogado, Long usuarioId) {
        Usuario usuarioAlvo = determinarUsuarioAlvo(usuarioId, emailLogado);
        
        return disponibilidadeRepository.findByUsuario(usuarioAlvo).stream()
                .map(d -> new DisponibilidadeDTO(d.getId(), d.getUsuario().getId(), d.getDiaDaSemana(), d.getTurno(), d.getCapacidade()))
                .toList();
    }

    @Override
    public void deletarDisponibilidade(Long id) {
        disponibilidadeRepository.deleteById(id);
    }


    //método utilizado para saber se o admin tá postando sua disponibilidade ou a disponibilidade de outro médico
    private Usuario determinarUsuarioAlvo(Long usuarioId, String emailLogado) {
        Usuario logado = usuarioRepository.findByEmail(emailLogado).orElseThrow();
        if (usuarioId != null && logado.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
            return usuarioRepository.findById(usuarioId).orElseThrow();
        }
        return logado; // Se não for admin ou não passar ID, cadastra para si mesmo
    }

}