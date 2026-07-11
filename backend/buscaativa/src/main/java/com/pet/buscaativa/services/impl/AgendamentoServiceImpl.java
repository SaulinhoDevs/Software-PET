package com.pet.buscaativa.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pet.buscaativa.entities.Agendamento;
import com.pet.buscaativa.entities.Disponibilidade;
import com.pet.buscaativa.entities.Paciente;
import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.dto.AgendamentoDTO;
import com.pet.buscaativa.entities.enums.SituacaoAtendimento;
import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.entities.enums.TurnoEnum;
import com.pet.buscaativa.mapping.AgendamentoMapper;
import com.pet.buscaativa.repositories.AgendamentoRepository;
import com.pet.buscaativa.repositories.BloqueioAgendaRepository;
import com.pet.buscaativa.repositories.DisponibilidadeRepository;
import com.pet.buscaativa.repositories.PacienteRepository;
import com.pet.buscaativa.repositories.UsuarioRepository;
import com.pet.buscaativa.services.AgendamentoService;
import com.pet.buscaativa.services.exceptions.ResourceNotFoundException;
import com.pet.buscaativa.services.exceptions.ValidationException;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService{


    private final AgendamentoRepository agendamentoRepository;
    private final BloqueioAgendaRepository bloqueioAgendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final PacienteRepository pacienteRepository;

    private final AgendamentoMapper agendamentoMapper;


    @Override
    public AgendamentoDTO save(AgendamentoDTO agendamentoDTO) {
        Agendamento agendamento = agendamentoMapper.toAgendamentoEntity(agendamentoDTO);

        if (agendamentoDTO.id() != null) {
            var original = agendamentoRepository.findById(agendamentoDTO.id())
                    .orElseThrow(() -> new RuntimeException("Agendamento original não encontrado"));
            
            original.setSituacaoAtendimento(SituacaoAtendimento.REMARCADO_ORIGEM);
            agendamento.setAgendamentoOriginal(original);

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
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));

        // Se a data cai em uma data bloqueada, a disponibilidade é ZERO
        boolean isBloqueado = bloqueioAgendaRepository.isDataBloqueadaParaUsuario(usuario, data);
        if (isBloqueado) {
            return 0; 
        }

        DayOfWeek diaSemana = data.getDayOfWeek();
        int vagasDisponiveisTotal = 0;

        // Define quais status consideramos como ocupantes de vaga
        List<SituacaoAtendimento> ocupantesVaga = List.of(
            SituacaoAtendimento.AGENDADO, 
            SituacaoAtendimento.REMARCADO, 
            SituacaoAtendimento.PRESENTE
        );

        Optional<Disponibilidade> manhaOpt = disponibilidadeRepository.findByUsuarioAndDiaDaSemanaAndTurno(usuario, diaSemana, TurnoEnum.MANHA);
        Optional<Disponibilidade> tardeOpt = disponibilidadeRepository.findByUsuarioAndDiaDaSemanaAndTurno(usuario, diaSemana, TurnoEnum.TARDE);

        //Busca a disponibilidade da Manhã e desconta os ocupados
        if (manhaOpt.isPresent()) {
            Disponibilidade manha = manhaOpt.get();
            int ocupadas = agendamentoRepository.contarVagasOcupadasBySituacoes(usuario, data, TurnoEnum.MANHA, ocupantesVaga);
            vagasDisponiveisTotal += Math.max(0, manha.getCapacidade() - ocupadas);
        }

        //Busca a disponibilidade da Tarde e desconta os ocupados
        if (tardeOpt.isPresent()) {
            Disponibilidade tarde = tardeOpt.get();
            int ocupadas = agendamentoRepository.contarVagasOcupadasBySituacoes(usuario, data, TurnoEnum.TARDE, ocupantesVaga);
            vagasDisponiveisTotal += Math.max(0, tarde.getCapacidade() - ocupadas);
        }

        return vagasDisponiveisTotal;
    }

    @Override
    public List<AgendamentoDTO> findAll() {
        var list = agendamentoRepository.findAll();
        return list.stream().map(agendamentoMapper::toAgendamentoDTO).toList();
    }

    @Override
    public List<LocalDate> buscarProximasVagasDisponiveis(Usuario usuario, TurnoEnum turno, LocalDate dataInicio, int quantidadeDesejada) {
        List<LocalDate> datasDisponiveis = new ArrayList<>();
        LocalDate dataVerificacao = dataInicio;

        int limiteDiasBusca = 90;
        int diasBuscados = 0;

        //Situacoes que ocupam a vaga
        List<SituacaoAtendimento> ocupantesVaga = List.of(SituacaoAtendimento.AGENDADO, SituacaoAtendimento.REMARCADO, SituacaoAtendimento.PRESENTE);

        while(datasDisponiveis.size() < quantidadeDesejada && diasBuscados < limiteDiasBusca){
            diasBuscados++;

            dataVerificacao = dataVerificacao.plusDays(1); 

            boolean isBloqueado = bloqueioAgendaRepository.isDataBloqueadaParaUsuario(usuario, dataVerificacao);
            if(isBloqueado){
                continue;
            }

            DayOfWeek diaSemana = dataVerificacao.getDayOfWeek();

            Optional<Disponibilidade> disponibilidadeOpt = disponibilidadeRepository.findByUsuarioAndDiaDaSemanaAndTurno(usuario, diaSemana, turno);

            if(disponibilidadeOpt.isEmpty()){
                continue;
            }

            Disponibilidade disponibilidade = disponibilidadeOpt.get();

            int vagasOcupadas = agendamentoRepository.contarVagasOcupadasBySituacoes(usuario, dataVerificacao, turno, ocupantesVaga);

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
    @Transactional
    public AgendamentoDTO atualizarStatus(Long id, SituacaoAtendimento novoStatus, Integer expectedVersion) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado para o id"));

        //só permitir registrar presença/falta para agendamentos com data <= hoje
        LocalDate dataAgendamento = agendamento.getDataAgendamento();
        if (dataAgendamento != null && dataAgendamento.isAfter(LocalDate.now())) {
            throw new ValidationException("Não é permitido registrar presença ou falta para agendamentos com data futura.");
        }

        // Controle otimista: se cliente informou expectedVersion, verifica se bate com versão atual.
        if (expectedVersion != null && !expectedVersion.equals(agendamento.getVersion())) {
            throw new OptimisticLockException("O agendamento foi alterado por outro usuário. Atualize a agenda antes de tentar novamente.");
        }

        agendamento.setSituacaoAtendimento(novoStatus);

        Paciente paciente = agendamento.getPaciente();
        if (paciente != null) {
            if (novoStatus == SituacaoAtendimento.FALTOU) {
                paciente.setCountFaltas(paciente.getCountFaltas() + 1);
            } else if (novoStatus == SituacaoAtendimento.PRESENTE) {
                paciente.setCountFaltas(0);
                paciente.setDataUltimaPresenca(LocalDate.now());
            }
            // salva paciente após receber falta ou returar falta ao atualizar o status do agendamento
            pacienteRepository.save(paciente);
        }

        agendamento = agendamentoRepository.save(agendamento);

        return agendamentoMapper.toAgendamentoDTO(agendamento);
    }
}