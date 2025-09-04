package com.blip.BlipSeguros.repository;

import com.blip.BlipSeguros.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarroRepository extends JpaRepository<Carro, Long> {

    @Query("select c from Carro c where c.cliente.id = :clienteId")
    List<Carro> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("select (count(c) > 0) from Carro c where c.id = :carroId and c.cliente.id = :clienteId")
    boolean existsByIdAndClienteId(@Param("carroId") Long carroId, @Param("clienteId") Long clienteId);

    boolean existsByIdAndCliente_Id(Long carroId, Long clienteId);


}
