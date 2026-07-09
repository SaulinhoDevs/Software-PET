package com.pet.buscaativa.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pet.buscaativa.entities.BloqueioAgenda;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.BloqueioAgendaDTO;
import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.repositories.BloqueioAgendaRepository;
import com.pet.buscaativa.repositories.DisponibilidadeRepository;
import com.pet.buscaativa.repositories.UsuarioRepository;
import com.pet.buscaativa.services.BloqueioAgendaService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BloqueioAgendaServiceImpl implements BloqueioAgendaService{

    private final BloqueioAgendaRepository bloqueioRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public BloqueioAgendaDTO save(BloqueioAgendaDTO bloqueioAgendaDTO, String emailLogado) {
        Usuario usuario = determinarUsuarioAlvo(bloqueioAgendaDTO.usuarioId(), emailLogado);

        BloqueioAgenda bloqueio = new BloqueioAgenda();
        if (bloqueioAgendaDTO.id() != null) {
            bloqueio = bloqueioRepository.findById(bloqueioAgendaDTO.id()).orElseThrow();
        }
        bloqueio.setUsuario(usuario);
        bloqueio.setDataInicio(bloqueioAgendaDTO.dataInicio());
        bloqueio.setDataFim(bloqueioAgendaDTO.dataFim());
        bloqueio.setMotivoBloqueio(bloqueioAgendaDTO.motivoBloqueio());

        bloqueio = bloqueioRepository.save(bloqueio);
        return new BloqueioAgendaDTO(bloqueio.getId(), usuario.getId(), bloqueio.getDataInicio(), bloqueio.getDataFim(), bloqueio.getMotivoBloqueio());
    }

    @Override
    public List<BloqueioAgendaDTO> listarBloqueios(String emailLogado, Long usuarioId) {
        Usuario usuarioAlvo = determinarUsuarioAlvo(usuarioId, emailLogado);
        
        return bloqueioRepository.findByUsuario(usuarioAlvo).stream()
                .map(b -> new BloqueioAgendaDTO(b.getId(), b.getUsuario().getId(), b.getDataInicio(), b.getDataFim(), b.getMotivoBloqueio()))
                .toList();
    }

    @Override
    public void deletarBloqueio(Long id) {
        bloqueioRepository.deleteById(id);
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
    