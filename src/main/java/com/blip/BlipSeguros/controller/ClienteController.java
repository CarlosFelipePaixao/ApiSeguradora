package com.blip.BlipSeguros.controller;

import com.blip.BlipSeguros.model.Cliente;
import com.blip.BlipSeguros.repository.ClienteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    // Buscar por CPF (aceita com ou sem máscara)
    @GetMapping("/cpf/{cpf}")
    public Cliente getByCpf(@PathVariable String cpf) {
        String normalized = cpf.replaceAll("\\D", "");
        if (normalized.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }
        return repository.findByCpf(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente create(@Valid @RequestBody Cliente body) {
        body.setId(null);
        body.setCpf(body.getCpf().replaceAll("\\D", "")); // normaliza
        return repository.save(body);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public Map<String, String> handleConstraint(org.springframework.dao.DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        if (msg != null && msg.toLowerCase().contains("cpf")) {
            return Map.of("error", "CPF já cadastrado");
        }
        return Map.of("error", "Violação de integridade", "detail", msg);
    }


    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id, @Valid @RequestBody Cliente body) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        existente.setFirstname(body.getFirstname());
        if (body.getCpf() != null) {
            existente.setCpf(body.getCpf().replaceAll("\\D", ""));
        }
        existente.setCarro_marca(body.getCarro_marca());
        existente.setCarro_ano(body.getCarro_ano());


        try {
            return repository.save(existente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        repository.deleteById(id);
    }

    // Tratamento de validações @Valid
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return errors;
    }
}
