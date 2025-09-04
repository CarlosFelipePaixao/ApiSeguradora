package com.blip.BlipSeguros.controller;

import com.blip.BlipSeguros.model.Carro;
import com.blip.BlipSeguros.model.Cliente;
import com.blip.BlipSeguros.repository.CarroRepository;
import com.blip.BlipSeguros.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/clientes/{clienteId}/carros")
public class CarroController {

    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarroRepository carroRepository;

    @PostMapping
    public ResponseEntity<Carro> criarCarro(
            @PathVariable Long clienteId,
            @Valid @RequestBody Carro body
    ) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Carro carro = new Carro();
        carro.setMarca(body.getMarca());
        carro.setModelo(body.getModelo());
        carro.setAno(body.getAno());
        carro.setCliente(cliente);

        Carro salvo = carroRepository.save(carro);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{carroId}")
    public ResponseEntity<Carro> atualizarCarro(
            @PathVariable Long clienteId,
            @PathVariable Long carroId,
            @Valid @RequestBody Carro body
    ) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Carro carro = carroRepository.findById(carroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado"));

        if (!carro.getCliente().getId().equals(clienteId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Carro não pertence ao cliente informado");
        }

        carro.setMarca(body.getMarca());
        carro.setModelo(body.getModelo());
        carro.setAno(body.getAno());

        Carro salvo = carroRepository.save(carro);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Carro>> listarCarros(@PathVariable Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        return ResponseEntity.ok(carroRepository.findByClienteId(clienteId));
    }

    @GetMapping("/{carroId}")
    public ResponseEntity<Carro> obterCarro(
            @PathVariable Long clienteId,
            @PathVariable Long carroId
    ) {
        Carro carro = carroRepository.findById(carroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado"));

        if (!carro.getCliente().getId().equals(clienteId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Carro não pertence ao cliente informado");
        }
        return ResponseEntity.ok(carro);
    }

    @DeleteMapping("/{carroId}")
    public ResponseEntity<Void> removerCarro(
            @PathVariable Long clienteId,
            @PathVariable Long carroId
    ) {
        Carro carro = carroRepository.findById(carroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado"));

        if (!carro.getCliente().getId().equals(clienteId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Carro não pertence ao cliente informado");
        }

        carroRepository.delete(carro);
        return ResponseEntity.noContent().build();
    }




}
