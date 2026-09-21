package com.jhon.barbearia.controller;

import com.jhon.barbearia.service.AgendamentoService;
import com.jhon.barbearia.service.BarbeiroService;
import com.jhon.barbearia.service.ClienteService;
import com.jhon.barbearia.service.DashboardService;
import com.jhon.barbearia.service.ServicoService;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final DashboardService dashboardService;
    private final AgendamentoService agendamentoService;
    private final BarbeiroService barbeiroService;
    private final ServicoService servicoService;
    private final ClienteService clienteService;

    private static final String UPLOAD_DIR = "src/main/resources/static/image/barbeiros/";

    public AdminController(
            DashboardService dashboardService,
            AgendamentoService agendamentoService,
            BarbeiroService barbeiroService,
            ServicoService servicoService,
            ClienteService clienteService
    ) {
        this.dashboardService = dashboardService;
        this.agendamentoService = agendamentoService;
        this.barbeiroService = barbeiroService;
        this.servicoService = servicoService;
        this.clienteService = clienteService;
    }

    // Spring Security já protege /admin/** com hasAnyRole("ADMIN", "PROPRIETARIO")

    @GetMapping("/admin")
    public String dashboard(@RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("resumo", dashboardService.montarResumo());
        model.addAttribute("agendamentosPage", agendamentoService.buscarTodosPaginado(pagina, 10));
        model.addAttribute("barbeiros", barbeiroService.buscarTodos());
        model.addAttribute("servicos", servicoService.buscarTodos());
        model.addAttribute("clientes", clienteService.buscarTodos());
        return "admin/dashboard";
    }

    @PostMapping("/admin/barbeiros/salvar")
    public String salvarBarbeiro(@RequestParam(required = false) Long id,
                                 @RequestParam String nome,
                                 @RequestParam(required = false) String especialidade,
                                 @RequestParam(required = false) MultipartFile fotoArquivo,
                                 @RequestParam(required = false) String fotoAtual,
                                 RedirectAttributes redirectAttributes) {
        String caminhoFoto = fotoAtual;

        // Se um arquivo foi enviado, salvar no disco
        if (fotoArquivo != null && !fotoArquivo.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                String nomeArquivo = UUID.randomUUID() + "_" + fotoArquivo.getOriginalFilename();
                Path destino = uploadPath.resolve(nomeArquivo);
                Files.copy(fotoArquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

                caminhoFoto = "image/barbeiros/" + nomeArquivo;
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("erro", "Falha ao salvar foto do barbeiro.");
                return "redirect:/admin#barbeiros";
            }
        }

        barbeiroService.salvar(id, nome, especialidade, caminhoFoto);
        redirectAttributes.addFlashAttribute("sucesso", "Barbeiro salvo.");
        return "redirect:/admin#barbeiros";
    }

    @PostMapping("/admin/barbeiros/excluir")
    public String excluirBarbeiro(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        barbeiroService.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Barbeiro excluido.");
        return "redirect:/admin#barbeiros";
    }

    @PostMapping("/admin/servicos/salvar")
    public String salvarServico(@RequestParam(required = false) Long id,
                                @RequestParam String nome,
                                @RequestParam BigDecimal preco,
                                @RequestParam(required = false) Integer duracaoMinutos,
                                RedirectAttributes redirectAttributes) {
        servicoService.salvar(id, nome, preco, duracaoMinutos);
        redirectAttributes.addFlashAttribute("sucesso", "Servico salvo.");
        return "redirect:/admin#servicos";
    }

    @PostMapping("/admin/servicos/excluir")
    public String excluirServico(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        servicoService.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Servico excluido.");
        return "redirect:/admin#servicos";
    }

    @PostMapping("/admin/clientes/permissao")
    public String atualizarPermissao(@RequestParam Long id,
                                     @RequestParam String papel,
                                     @RequestParam(defaultValue = "false") boolean ativo,
                                     RedirectAttributes redirectAttributes) {
        clienteService.atualizarPermissao(id, papel, ativo);
        redirectAttributes.addFlashAttribute("sucesso", "Permissao atualizada.");
        return "redirect:/admin#clientes";
    }
}
