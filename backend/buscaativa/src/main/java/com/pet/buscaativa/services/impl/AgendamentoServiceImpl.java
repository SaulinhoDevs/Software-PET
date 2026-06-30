package com.pet.buscaativa.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.AgendamentoDTO;
import com.pet.buscaativa.entities.enums.TurnoEnum;
import com.pet.buscaativa.services.AgendamentoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService{


    private final AgendamentoRepository agendamentoRepository;
    private final BloqueioAgendamentoRepository bloqueioRepository;
    private final DisponibilidadeProfissionalRepository disponbilidadeRepository;


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
    public List<LocalDate> buscarProximasVagasDisponiveis(Usuario usuario, TurnoEnum turno, LocalDate dataInicio, int quantidadeVagas) {
        List<LocalDate> datasDisponiveis = new ArrayList<>();
        LocalDate dataVerificacao = dataInicio;

        int limiteDiasBusca = 90;
        int diasBuscados = 0;

        while(datasDisponiveis.size() < quantidadeDesejada && diasBuscados < limiteDiasBusca){
            diasBuscados++;

            dataVerificacao = dataVerificacao.plusDay(1); 

            boolean isBloqueado = bloqueioRepository.isDataBloqueadaParaUsuario(usuario, dataVerificacao);
            if(isBloqueado){
                continue;
            }

            DayOfWeek diaSemana = dataVerificacao.getDayOfWeek();

            Optional<DisponibilidadeProfissional> disponibilidadeOpt = disponibilidadeRepository.findByUsuarioAndDiaSemanaAndTurno(profissional, diaSemana, turno);

            if(disponibilidadeOpt.isEmpty()){
                continue;
            }

            DisponibilidadeProfissional disponibilidade = disponibilidadeOpt.get();

            int vagasOcupadas = agendamentoRepository.countByUsuarioAndDataAgendamentoAndTurnoAgendamentoAndSituacaoAtendimentoNot(
                usuario, dataVerificacao, turno, SituacaoAtendimento.FALTOU);

            if(vagasOcupadas < disponibilidade.getCapacidade()){
                datasDisponiveis.add(dataVerificacao);
            }
        }

        return datasDisponiveis;

    }
}