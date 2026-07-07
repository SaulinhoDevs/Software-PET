package com.pet.buscaativa.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pet.buscaativa.entities.Agendamento;
import com.pet.buscaativa.entities.DisponibilidadeProfissional;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.AgendamentoDTO;
import com.pet.buscaativa.entities.enums.SituacaoAtendimento;
import com.pet.buscaativa.entities.enums.TurnoEnum;
import com.pet.buscaativa.mapping.AgendamentoMapper;
import com.pet.buscaativa.repositories.AgendamentoRepository;
import com.pet.buscaativa.repositories.BloqueioAgendaRepository;
import com.pet.buscaativa.repositories.DisponibilidadeProfissionalRepository;
import com.pet.buscaativa.services.AgendamentoService;
import com.pet.buscaativa.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService{


    private final AgendamentoRepository agendamentoRepository;
    private final BloqueioAgendaRepository bloqueioRepository;
    private final DisponibilidadeProfissionalRepository disponibilidadeRepository;
    private final AgendamentoMapper agendamentoMapper;


    @Override
    public AgendamentoDTO save(AgendamentoDTO agendamentoDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
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

            Optional<DisponibilidadeProfissional> disponibilidadeOpt = disponibilidadeRepository.findByUsuarioAndDiaDaSemanaAndTurno(usuario, diaSemana, turno);

            if(disponibilidadeOpt.isEmpty()){
                continue;
            }

            DisponibilidadeProfissional disponibilidade = disponibilidadeOpt.get();

            int vagasOcupadas = agendamentoRepository.contarVagasOcupadas(usuario, dataVerificacao, turno, SituacaoAtendimento.FALTOU);

            if(vagasOcupadas < disponibilidade.getCapacidade()){
                datasDisponiveis.add(dataVerificacao);
            }
        }

        return datasDisponiveis;

    }

    @Override
    public List<AgendamentoDTO> buscarAgendaDoDia(LocalDate data) {
        return agendamentoRepository.findByDataAgendamento(data).stream()
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