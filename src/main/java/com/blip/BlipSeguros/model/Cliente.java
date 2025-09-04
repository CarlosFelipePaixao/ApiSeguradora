package com.blip.BlipSeguros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

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
    @CPF
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank
    @Column(nullable = false)
    private String firstname;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String carro_marca;

    @NotBlank
    @Column(nullable = false)
    private String carro_modelo;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer carro_ano;

    /** Normaliza CPF antes de salvar/atualizar: guarda só os dígitos */
    @PrePersist
    @PreUpdate
    private void normalizeCpf() {
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("\\D", "");
        }
    }
}
