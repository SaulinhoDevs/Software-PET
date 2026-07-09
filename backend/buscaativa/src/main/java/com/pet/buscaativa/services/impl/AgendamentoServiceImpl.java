package com.pet.buscaativa.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pet.buscaativa.entities.Agendamento;
import com.pet.buscaativa.entities.Disponibilidade;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.AgendamentoDTO;
import com.pet.buscaativa.entities.enums.SituacaoAtendimento;
import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.entities.enums.TurnoEnum;
import com.pet.buscaativa.mapping.AgendamentoMapper;
import com.pet.buscaativa.repositories.AgendamentoRepository;
import com.pet.buscaativa.repositories.BloqueioAgendaRepository;
import com.pet.buscaativa.repositories.DisponibilidadeRepository;
import com.pet.buscaativa.repositories.UsuarioRepository;
import com.pet.buscaativa.services.AgendamentoService;
import com.pet.buscaativa.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService{


    private final AgendamentoRepository agendamentoRepository;
    private final BloqueioAgendaRepository bloqueioRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final AgendamentoMapper agendamentoMapper;


    @Override
    public AgendamentoDTO save(AgendamentoDTO agendamentoDTO) {
        Agendamento agendamento = agendamentoMapper.toAgendamentoEntity(agendamentoDTO);

        if (agendamentoDTO.id() != null) {
            var original = agendamentoRepository.findById(agendamentoDTO.id())
                    .orElseThrow(() -> new RuntimeException("Agendamento original não encontrado"));
                                                        
            agendamento.setAgendamentoOriginal(original);
            agendamento.setSituacaoAtendimento(SituacaoAtendimento.REMARCADO);
        } else {
            agendamento.setSituacaoAtendimento(SituacaoAtendimento.AGENDADO);
        }

        agendamento = agendamentoRepository.save(agendamento);
        return agendamentoMapper.toAgendamentoDTO(agendamento);
    }

    @Override
    public List<LocalDate> sugerirDataRemarcacao(Long agendamento){
        var original = agendamentoRepository.findById(agendamento)
                        .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        return buscarProximasVagasDisponiveis(original.getUsuario(), original.getTurnoAgendamento(), LocalDate.now(), 3);
    }


    @Override
    public int calcularVagasDisponiveis(Long usuarioId, LocalDate data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calcularVagasDisponiveis'");
    }

    @Override
    public List<AgendamentoDTO> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public List<LocalDate> buscarProximasVagasDisponiveis(Usuario usuario, TurnoEnum turno, LocalDate dataInicio, int quantidadeDesejada) {
        List<LocalDate> datasDisponiveis = new ArrayList<>();
        LocalDate dataVerificacao = dataInicio;

        int limiteDiasBusca = 90;
        int diasBuscados = 0;

        while(datasDisponiveis.size() < quantidadeDesejada && diasBuscados < limiteDiasBusca){
            diasBuscados++;

            dataVerificacao = dataVerificacao.plusDays(1); 

            boolean isBloqueado = bloqueioRepository.isDataBloqueadaParaUsuario(usuario, dataVerificacao);
            if(isBloqueado){
                continue;
            }

            DayOfWeek diaSemana = dataVerificacao.getDayOfWeek();

            Optional<Disponibilidade> disponibilidadeOpt = disponibilidadeRepository.findByUsuarioAndDiaDaSemanaAndTurno(usuario, diaSemana, turno);

            if(disponibilidadeOpt.isEmpty()){
                continue;
            }

            Disponibilidade disponibilidade = disponibilidadeOpt.get();

            int vagasOcupadas = agendamentoRepository.contarVagasOcupadas(usuario, dataVerificacao, turno, SituacaoAtendimento.FALTOU);

            if(vagasOcupadas < disponibilidade.getCapacidade()){
                datasDisponiveis.add(dataVerificacao);
            }
        }

        return datasDisponiveis;

    }

    @Override
    public List<AgendamentoDTO> buscarAgendaDoDia(LocalDate data, String emailLogado, Long usuarioId) {
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailLogado)
                        .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        List<Agendamento> agendamentos;

        if (usuarioLogado.getTipoUsuario() == TipoUsuario.PROFISSIONAL) {
            agendamentos = agendamentoRepository.findByDataAgendamentoAndUsuario(data, usuarioLogado);
        }else{
            if(usuarioId != null){
                Usuario profissionalAlvo = usuarioRepository.findById(usuarioId)
                                        .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
                
                agendamentos = agendamentoRepository.findByDataAgendamentoAndUsuario(data, profissionalAlvo);
            }else{
                agendamentos = agendamentoRepository.findByDataAgendamento(data);
            }
        }

        return agendamentos.stream()
            .map(agendamentoMapper::toAgendamentoDTO)
            .toList();

    }

    @Override
    public AgendamentoDTO atualizarStatus(Long id, SituacaoAtendimento novoStatus) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado para o id: " + id));

        agendamento.setSituacaoAtendimento(novoStatus);
        agendamento = agendamentoRepository.save(agendamento);

        return agendamentoMapper.toAgendamentoDTO(agendamento);
    }
}