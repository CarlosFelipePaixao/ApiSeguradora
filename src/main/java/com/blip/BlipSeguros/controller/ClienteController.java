package com.blip.BlipSeguros.controller;


import com.blip.BlipSeguros.model.Cliente;
import com.blip.BlipSeguros.repository.ClienteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin
public class ClienteController {

    private final ClienteRepository repository;

    @GetMapping
    public List<Cliente> list() {
        return repository.findAll();
    }

    //Buscar por CPF na blip
    @GetMapping("/cpf/{cpf}")
    public Cliente getByCpf(@PathVariable String cpf) {
        // normaliza: remove pontos/traço/espaços
        String normalized = cpf.replaceAll("\\D", "");
        if (normalized.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }

        return repository.findByCpf(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }


    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente create(@Valid @RequestBody Cliente body) {
        body.setId(null);
        return repository.save(body);
    }

}
