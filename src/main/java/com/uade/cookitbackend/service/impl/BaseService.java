package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public abstract class BaseService<T, ID> {
    
    protected abstract JpaRepository<T, ID> getRepository();
    protected abstract ErrorCode getNotFoundErrorCode();
    protected abstract String getEntityName();
    
    protected T findByIdOrThrow(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getNotFoundErrorCode(), 
                        getEntityName() + " not found with id: " + id
                ));
    }
    
    protected Optional<T> findByIdOptional(ID id) {
        return getRepository().findById(id);
    }
    
    protected boolean existsById(ID id) {
        return getRepository().existsById(id);
    }
    
    protected void deleteByIdOrThrow(ID id) {
        if (!existsById(id)) {
            throw new ResourceNotFoundException(
                    getNotFoundErrorCode(), 
                    getEntityName() + " not found with id: " + id
            );
        }
        getRepository().deleteById(id);
    }
}