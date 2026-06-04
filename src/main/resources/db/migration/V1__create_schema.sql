CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(255),
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'EXTERNO')),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    token VARCHAR(512) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_usuario ON refresh_tokens(usuario_id);

CREATE TABLE empresas (
    nit VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    direccion VARCHAR(500),
    telefono VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    caracteristicas TEXT,
    empresa_nit VARCHAR(20) NOT NULL REFERENCES empresas(nit) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_productos_empresa ON productos(empresa_nit);

CREATE TABLE precios_moneda (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    moneda VARCHAR(10) NOT NULL CHECK (moneda IN ('COP', 'USD', 'EUR', 'GBP')),
    precio DECIMAL(18, 2) NOT NULL CHECK (precio >= 0),
    UNIQUE (producto_id, moneda)
);

CREATE TABLE producto_categoria (
    producto_id BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    categoria_id BIGINT NOT NULL REFERENCES categorias(id) ON DELETE CASCADE,
    PRIMARY KEY (producto_id, categoria_id)
);

CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefono VARCHAR(50)
);

CREATE TABLE ordenes (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    empresa_nit VARCHAR(20) NOT NULL REFERENCES empresas(nit),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'ENVIADA', 'ENTREGADA', 'CANCELADA')),
    total DECIMAL(18, 2)
);

CREATE INDEX idx_ordenes_cliente ON ordenes(cliente_id);
CREATE INDEX idx_ordenes_empresa ON ordenes(empresa_nit);

CREATE TABLE orden_producto (
    orden_id BIGINT NOT NULL REFERENCES ordenes(id) ON DELETE CASCADE,
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(18, 2) NOT NULL CHECK (precio_unitario >= 0),
    PRIMARY KEY (orden_id, producto_id)
);
