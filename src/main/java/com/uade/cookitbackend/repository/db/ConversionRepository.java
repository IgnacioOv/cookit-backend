package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, Integer> {
    Optional<Conversion> findByUnidadOrigenIdUnidadAndUnidadDestinoIdUnidad(Integer idUnidadOrigen, Integer idUnidadDestino);
}
