package com.blip.BlipSeguros.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "clientes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank                    // não pode ser vazio/nulo
    @CPF                        // valida dígitos e dígito verificador
    @Column(nullable = false, unique = true, length = 11) // guarda só 11 dígitos
    private String cpf;


    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Apolice apolice;


    @NotNull
    @Column(nullable = false)
    private String  Veiculo;
}

