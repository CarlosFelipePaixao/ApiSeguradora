package com.blip.BlipSeguros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "clientes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_clientes_cpf", columnNames = "cpf"),
                @UniqueConstraint(name = "uk_clientes_email", columnNames = "email")
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    // ATENÇÃO: mantemos apenas 11 dígitos; sem validar DV
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank
    @Column(nullable = false)
    private String firstname;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @OneToMany(
            mappedBy = "cliente",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Carro> carros = new ArrayList<>();

    /** Normaliza CPF antes de salvar/atualizar: guarda só os dígitos */
    @PrePersist
    @PreUpdate
    private void normalizeCpf() {
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("\\D", "");
        }
    }

    /** Valida que o CPF possui exatamente 11 dígitos (sem validar DV) */
    @AssertTrue(message = "cpf deve conter exatamente 11 dígitos")
    private boolean isCpfCom11Digitos() {
        if (this.cpf == null) return false;
        String digits = this.cpf.replaceAll("\\D", "");
        return digits.length() == 11;
    }

    // Helpers (opcional)
    public void addCarro(Carro carro) {
        this.carros.add(carro);
        carro.setCliente(this);
    }
    public void removeCarro(Carro carro) {
        this.carros.remove(carro);
        carro.setCliente(null);
    }
}
