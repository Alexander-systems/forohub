package com.forohub.forohub.domain.topico;

import com.forohub.forohub.domain.curso.Curso;
import com.forohub.forohub.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String mensaje;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    @Enumerated(EnumType.STRING)
    private StatusTopico status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    public Topico(DatosRegistroTopico datos, Usuario autor, Curso curso) {
        this.titulo = datos.titulo();
        this.mensaje = datos.mensaje();
        this.fechaCreacion = LocalDateTime.now();
        this.status = StatusTopico.ABIERTO; // Por defecto
        this.autor = autor;
        this.curso = curso;
    }

    public void actualizarDatos(DatosActualizacionTopico datosActualizacion, Usuario nuevoAutor, Curso nuevoCurso) {
        if (datosActualizacion.titulo() != null) {
            this.titulo = datosActualizacion.titulo();
        }
        if (datosActualizacion.mensaje() != null) {
            this.mensaje = datosActualizacion.mensaje();
        }
        if (nuevoAutor != null) {
            this.autor = nuevoAutor;
        }
        if (nuevoCurso != null) {
            this.curso = nuevoCurso;
        }
        if (datosActualizacion.status() != null) {
            this.status = datosActualizacion.status();
        }
    }
}