package com.walkspring.services;

import com.walkspring.exceptions.EmptyOptionalException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConversionService1 {
    public <T> T getEntityFromOptional(Optional<T> optional) throws EmptyOptionalException {
        if (optional.isEmpty()){
            throw new EmptyOptionalException("Unexpected empty Optional");
        }
        return optional.get();
    }

}
