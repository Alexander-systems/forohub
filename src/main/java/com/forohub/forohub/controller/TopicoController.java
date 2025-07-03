package com.forohub.forohub.controller;

import com.forohub.forohub.domain.topico.*;
import com.forohub.forohub.infra.errores.ValidacionDeIntegridad;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;

    @PostMapping
    @Transactional
    public ResponseEntity<DatosRespuestaTopico> registrarTopico(@RequestBody @Valid DatosRegistroTopico datosRegistro,
                                                                UriComponentsBuilder uriBuilder) {
        Topico topicoRegistrado = topicoService.registrar(datosRegistro);

        DatosRespuestaTopico datosRespuesta = new DatosRespuestaTopico(
                topicoRegistrado.getId(),
                topicoRegistrado.getTitulo(),
                topicoRegistrado.getMensaje(),
                topicoRegistrado.getFechaCreacion(),
                topicoRegistrado.getStatus(),
                topicoRegistrado.getAutor().getNombre(),
                topicoRegistrado.getCurso().getNombre()
        );

        URI url = uriBuilder.path("/topicos/{id}").buildAndExpand(topicoRegistrado.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuesta);
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listarTopicos(@PageableDefault(size = 10, sort = "fechaCreacion") Pageable paginacion) {
        Page<DatosListadoTopico> page = topicoService.listarTopicos(paginacion);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<DatosListadoTopico>> buscarTopicos(
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) Integer ano,
            @PageableDefault(size = 10, sort = "fechaCreacion") Pageable paginacion) {

        if (nombreCurso != null && ano != null) {
            Page<DatosListadoTopico> page = topicoService.buscarTopicosPorCursoYAno(nombreCurso, ano, paginacion);
            return ResponseEntity.ok(page);
        } else if (nombreCurso != null || ano != null) {
            throw new ValidacionDeIntegridad("Para buscar por curso y año, ambos parámetros 'nombreCurso' y 'ano' son obligatorios.");
        } else {
            return listarTopicos(paginacion);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaTopico> detallarTopico(@PathVariable Long id) {
        DatosRespuestaTopico datosTopico = topicoService.obtenerTopicoPorId(id);
        return ResponseEntity.ok(datosTopico);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosRespuestaTopico> actualizarTopico(@PathVariable Long id,
                                                                 @RequestBody @Valid DatosActualizacionTopico datosActualizacion) {
        DatosRespuestaTopico topicoActualizado = topicoService.actualizarTopico(id, datosActualizacion);
        return ResponseEntity.ok(topicoActualizado);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminarTopico(@PathVariable Long id) {
        topicoService.eliminarTopico(id);
        return ResponseEntity.noContent().build();
    }
}