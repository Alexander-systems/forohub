package com.forohub.forohub.domain.topico;

public record DatosActualizacionTopico(
        String titulo,
        String mensaje,
        Long autorId,
        Long cursoId,
        StatusTopico status
) {
}