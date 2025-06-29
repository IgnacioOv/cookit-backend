package com.uade.cookitbackend.exception;

/**
 * Enumeración de códigos internos de error, para identificar cada tipo de falla
 * de manera consistente.
 */
public enum ErrorCode {
    RECETA_NOT_FOUND,          // Cuando no se encuentra una receta
    USUARIO_NOT_FOUND,         // Cuando no se encuentra un usuario
    TIPO_RECETA_NOT_FOUND,     // Cuando no se encuentra el tipo de receta
    VALIDATION_FAILED,         // Errores de validación de campos
    INTERNAL_SERVER_ERROR,    // Cualquier otro error inesperado
    BAD_REQUEST       ,
    INVALID_CREDENTIALS,
    DUPLICATE_RESOURCE,
    INVALID_RESET_CODE,
    INVALID_VERIFICATION_CODE,
    UNAUTHORIZED,
    USER_NOT_ENABLED,
    CALIFICACION_NOT_FOUND,
    INGREDIENTE_NOT_FOUND,
    ALUMNO_NOT_FOUND,
    CRONOGRAMA_CURSO_NOT_FOUND,
    VACANTE_NOT_AVAILABLE,
    ALUMNO_ALREADY_REGISTERED,
    ALUMNO_NOT_REGISTERED,
    INSCRIPCION_CURSO_NOT_FOUND,
    DUPLICATE_RECIPE_NAME,
    DUPLICATE_FAVORITE,
    FAVORITES_LIMIT_EXCEEDED,
    FAVORITE_NOT_FOUND,
    RECETA_ALREADY_APPROVED,
    UNAUTHORIZED_OPERATION

}
