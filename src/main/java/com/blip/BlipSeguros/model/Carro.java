// Carro.java
package com.blip.BlipSeguros.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(
        name = "carros",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_carros_cliente_marca_modelo_ano",
                        columnNames = {"cliente_id","marca","modelo","ano"}
                )
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Carro {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false)
    private String marca;

    @NotBlank @Column(nullable = false)
    private String modelo;

    @NotNull @Min(1900) @Max(2100)
    @Column(nullable = false)
    private Integer ano;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "cliente_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_carros_cliente")
    )
    @JsonIgnore                 // <-- evita Cliente dentro de Carro (corta a recursão)
    private Cliente cliente;

    // Opcional: ainda expõe o clienteId na resposta sem precisar de DTO
    @Transient
    @JsonProperty("clienteId")
    public Long getClienteId() {
        return (cliente != null ? cliente.getId() : null);
    }
}
