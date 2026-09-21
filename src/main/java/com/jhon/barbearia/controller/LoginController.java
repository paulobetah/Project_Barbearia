package com.jhon.barbearia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String abrirTelaLogin(@RequestParam(required = false) String erro,
                                 @RequestParam(required = false) String bloqueado,
                                 Model model) {
        if (bloqueado != null) {
            model.addAttribute("erro", "Conta bloqueada por excesso de tentativas. Tente novamente em " + bloqueado + " minuto(s).");
        } else if (erro != null) {
            model.addAttribute("erro", "E-mail ou senha invalidos. Tente novamente.");
        }
        return "login";
    }
}
