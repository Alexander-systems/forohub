package com.forohub.forohub.domain.topico;


import com.forohub.forohub.domain.curso.Curso;
import com.forohub.forohub.domain.curso.CursoRepository;
import com.forohub.forohub.domain.usuario.Usuario;
import com.forohub.forohub.domain.usuario.UsuarioRepository;
import com.forohub.forohub.infra.errores.ValidacionDeIntegridad;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CursoRepository cursoRepository;

    public Topico registrar(DatosRegistroTopico datos) {
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ValidacionDeIntegridad("Ya existe un tópico con el mismo título y mensaje.");
        }

        Usuario autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new ValidacionDeIntegridad("Autor no encontrado con ID: " + datos.autorId()));
        Curso curso = cursoRepository.findById(datos.cursoId())
                .orElseThrow(() -> new ValidacionDeIntegridad("Curso no encontrado con ID: " + datos.cursoId()));

        Topico topico = new Topico(datos, autor, curso);
        return topicoRepository.save(topico);
    }

    public Page<DatosListadoTopico> listarTopicos(Pageable paginacion) {
        return topicoRepository.findAll(paginacion).map(DatosListadoTopico::new);
    }

    public Page<DatosListadoTopico> buscarTopicosPorCursoYAno(String nombreCurso, int ano, Pageable paginacion) {
        return topicoRepository.findByCursoNombreAndFechaCreacionYear(nombreCurso, ano, paginacion).map(DatosListadoTopico::new);
    }

    public DatosRespuestaTopico obtenerTopicoPorId(Long id) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico no encontrado con ID: " + id));
        return new DatosRespuestaTopico(topico);
    }

    @Transactional
    public DatosRespuestaTopico actualizarTopico(Long id, DatosActualizacionTopico datosActualizacion) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico no encontrado con ID: " + id));

        String nuevoTitulo = datosActualizacion.titulo() != null ? datosActualizacion.titulo() : topico.getTitulo();
        String nuevoMensaje = datosActualizacion.mensaje() != null ? datosActualizacion.mensaje() : topico.getMensaje();

        if (!topico.getTitulo().equals(nuevoTitulo) || !topico.getMensaje().equals(nuevoMensaje)) {
            if (topicoRepository.existsByTituloAndMensaje(nuevoTitulo, nuevoMensaje)) {
                throw new ValidacionDeIntegridad("Ya existe otro tópico con el mismo título y mensaje.");
            }
        }

        Usuario nuevoAutor = null;
        if (datosActualizacion.autorId() != null) {
            nuevoAutor = usuarioRepository.findById(datosActualizacion.autorId())
                    .orElseThrow(() -> new ValidacionDeIntegridad("Nuevo autor no encontrado con ID: " + datosActualizacion.autorId()));
        }

        Curso nuevoCurso = null;
        if (datosActualizacion.cursoId() != null) {
            nuevoCurso = cursoRepository.findById(datosActualizacion.cursoId())
                    .orElseThrow(() -> new ValidacionDeIntegridad("Nuevo curso no encontrado con ID: " + datosActualizacion.cursoId()));
        }

        topico.actualizarDatos(datosActualizacion, nuevoAutor, nuevoCurso);
        return new DatosRespuestaTopico(topico);
    }

    @Transactional
    public void eliminarTopico(Long id) {
        if (!topicoRepository.existsById(id)) {
            throw new EntityNotFoundException("Tópico no encontrado con ID: " + id + ". No se puede eliminar.");
        }
        topicoRepository.deleteById(id);
    }
}