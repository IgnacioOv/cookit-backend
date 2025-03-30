package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.MultimediaDTO;
import com.uade.cookitbackend.entity.Multimedia;

public interface MultimediaService {
    Multimedia guardarMultimedia(MultimediaDTO multimediaDTO);
}
