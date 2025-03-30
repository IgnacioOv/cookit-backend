package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Multimedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultimediaRepository extends JpaRepository<Multimedia, Integer> {
}
