package com.pet.buscaativa.alertas;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AlertaDestinatariosProviderPendente implements AlertaDestinatariosProvider {

    @Override
    public List<DestinatarioAlerta> montarDestinatarios() {
        log.warn("RF26: a regra de destinatários ainda não foi implementada. Nenhum alerta será enviado.");
        return List.of();
    }
}