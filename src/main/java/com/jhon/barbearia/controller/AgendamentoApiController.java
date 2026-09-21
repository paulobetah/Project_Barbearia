package com.jhon.barbearia.controller;

import com.jhon.barbearia.service.BarbeiroEscalaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgendamentoApiController {

    private final BarbeiroEscalaService barbeiroEscalaService;

    public AgendamentoApiController(BarbeiroEscalaService barbeiroEscalaService) {
        this.barbeiroEscalaService = barbeiroEscalaService;
    }

    @GetMapping("/api/agendamentos/horarios-disponiveis")
    public List<String> horariosDisponiveis(
            @RequestParam Long barbeiroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        return barbeiroEscalaService.gerarHorariosDisponiveis(barbeiroId, data);
    }
}
