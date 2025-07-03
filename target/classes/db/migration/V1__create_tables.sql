CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE perfiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY    KEY (id)
);

CREATE TABLE usuario_perfiles (
    usuario_id BIGINT NOT NULL,
    perfil_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    CONSTRAINT fk_usuario_perfiles_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_usuario_perfiles_perfil_id FOREIGN KEY (perfil_id) REFERENCES perfiles(id)
);

CREATE TABLE cursos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE topicos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL UNIQUE,
    mensaje TEXT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    autor_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_topicos_autor_id FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    CONSTRAINT fk_topicos_curso_id FOREIGN KEY (curso_id) REFERENCES cursos(id),
    -- CORRECCIÓN: Asegúrate de que no haya un paréntesis adicional aquí
    UNIQUE KEY uk_titulo_mensaje (titulo, mensaje(255))
);

CREATE TABLE respuestas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    mensaje TEXT NOT NULL,
    topico_id BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    autor_id BIGINT NOT NULL,
    solucion TINYINT(1) DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_respuestas_topico_id FOREIGN KEY (topico_id) REFERENCES topicos(id),
    CONSTRAINT fk_respuestas_autor_id FOREIGN KEY (autor_id) REFERENCES usuarios(id)
);

-- Datos de ejemplo (se insertarán una vez que Flyway corra esta migración)
INSERT INTO usuarios (nombre, correo_electronico, contrasena) VALUES ('Usuario Demo', 'usuario@forohub.com', '$2a$10$sbfjvmvGGUHAVpNsTifGxO5WWeERD9ukaTDelhweRlpp0fa705aQy');
INSERT INTO usuarios (nombre, correo_electronico, contrasena) VALUES ('Admin Demo', 'admin@forohub.com', '$2a$10$Gv4A2mayOZD.zQzUmgMYRun.cGawm.SHVmdlYxhsF9unVu0BGbItm');
INSERT INTO perfiles (nombre) VALUES ('USUARIO');
INSERT INTO perfiles (nombre) VALUES ('ADMIN');

INSERT INTO usuario_perfiles (usuario_id, perfil_id) VALUES (1, 1); -- Usuario Demo es USUARIO
INSERT INTO usuario_perfiles (usuario_id, perfil_id) VALUES (2, 1); -- Admin Demo también es USUARIO
INSERT INTO usuario_perfiles (usuario_id, perfil_id) VALUES (2, 2); -- Admin Demo es ADMIN

INSERT INTO cursos (nombre, categoria) VALUES ('Desarrollo Web Fullstack', 'Programacion');
INSERT INTO cursos (nombre, categoria) VALUES ('Base de Datos con SQL', 'Tecnologia');

INSERT INTO topicos (titulo, mensaje, fecha_creacion, status, autor_id, curso_id)
VALUES ('Duda sobre Spring Boot Security', 'Estoy implementando Spring Security y tengo algunas dudas con la configuración de JWT.', NOW(), 'ABIERTO', 1, 1);

INSERT INTO topicos (titulo, mensaje, fecha_creacion, status, autor_id, curso_id)
VALUES ('Problema con consultas JPQL', 'Mis consultas JPQL están lentas, ¿algún consejo para optimizarlas?', NOW(), 'ABIERTO', 1, 2);

INSERT INTO topicos (titulo, mensaje, fecha_creacion, status, autor_id, curso_id)
VALUES ('Consejos para microservicios', 'Quiero iniciar con microservicios, ¿qué herramientas recomiendan?', NOW(), 'ABIERTO', 2, 1);