package com.blip.BlipSeguros.controller;

import com.blip.BlipSeguros.repository.CarroRepository;
import com.blip.BlipSeguros.model.Carro;
import com.blip.BlipSeguros.model.Cliente;
import com.blip.BlipSeguros.repository.ClienteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin
public class ClienteController {

    private final ClienteRepository repository;
    private final CarroRepository carroRepository;

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
        if (body.getCpf() != null) {
            body.setCpf(body.getCpf().replaceAll("\\D", "")); // normaliza
        }
        return repository.save(body);
    }

    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id, @Valid @RequestBody Cliente body) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        // Atualiza apenas campos do Cliente (sem dados de carro)
        existente.setFirstname(body.getFirstname());
        if (body.getEmail() != null) {
            existente.setEmail(body.getEmail());
        }
        if (body.getCpf() != null) {
            existente.setCpf(body.getCpf().replaceAll("\\D", ""));
        }

        try {
            return repository.save(existente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF ou e-mail já cadastrado");
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

    @GetMapping("/cpf/{cpf}/carros")
    public List<Carro> getCarrosByCpf(@PathVariable String cpf) {
        String normalized = cpf.replaceAll("\\D", "");
        if (normalized.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }

        Cliente cliente = repository.findByCpf(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        return carroRepository.findByClienteId(cliente.getId());

    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> handleConstraint(DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        Map<String, String> resp = new HashMap<>();
        resp.put("error", "Violação de integridade");
        if (msg != null) {
            String lower = msg.toLowerCase();
            if (lower.contains("uk_clientes_cpf") || lower.contains("cpf")) {
                resp.put("detail", "CPF já cadastrado");
                return resp;
            }
            if (lower.contains("uk_clientes_email") || lower.contains("email")) {
                resp.put("detail", "E-mail já cadastrado");
                return resp;
            }
            resp.put("detail", msg);
        }
        return resp;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return errors;
    }
    @PostMapping("/cpf/{cpf}/carros")
    public ResponseEntity<Carro> criarCarroPorCpf(
            @PathVariable String cpf,
            @Valid @RequestBody Carro body
    ) {
        String normalized = cpf.replaceAll("\\D", "");
        if (normalized.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }

        Cliente cliente = repository.findByCpf(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Carro carro = new Carro();
        carro.setMarca(body.getMarca());
        carro.setModelo(body.getModelo());
        carro.setAno(body.getAno());
        carro.setCliente(cliente);

        Carro salvo = carroRepository.save(carro);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/clientes/{clienteId}/carros/{carroId}")
                .buildAndExpand(cliente.getId(), salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

}
