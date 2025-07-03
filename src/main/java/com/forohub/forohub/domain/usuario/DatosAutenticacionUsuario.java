package com.forohub.forohub.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DatosAutenticacionUsuario(
        @NotBlank
        String correoElectronico,
        @NotBlank
        String contrasena
) {
}
