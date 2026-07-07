package com.pet.buscaativa.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.buscaativa.entities.dto.AgendamentoDTO;
import com.pet.buscaativa.entities.enums.SituacaoAtendimento;
import com.pet.buscaativa.services.AgendamentoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {
    
    private final AgendamentoService agendamentoService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFISSIONAL', 'RECEPCAO')")
    @GetMapping("/agenda/{data}")
    public ResponseEntity<List<AgendamentoDTO>> buscarAgendaDoDia(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data){
        return ResponseEntity.ok(agendamentoService.buscarAgendaDoDia(data));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFISSIONAL', 'RECEPCAO')")
    @GetMapping("/{id}/status")
    public ResponseEntity<AgendamentoDTO> atualizarStatus(@PathVariable Long id, @RequestParam SituacaoAtendimento novoStatus) {
        AgendamentoDTO agendamentoAtualizado = agendamentoService.atualizarStatus(id, novoStatus);
        
        return ResponseEntity.ok(agendamentoAtualizado);
    }
}
