CREATE TABLE franquicias (
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

CREATE TABLE sucursales (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre        VARCHAR(255) NOT NULL,
    franquicia_id BIGINT NOT NULL REFERENCES franquicias (id) ON DELETE CASCADE
);

CREATE INDEX idx_sucursales_franquicia_id ON sucursales (franquicia_id);

CREATE TABLE productos (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL,
    stock       INTEGER NOT NULL CHECK (stock >= 0),
    sucursal_id BIGINT NOT NULL REFERENCES sucursales (id) ON DELETE CASCADE
);

CREATE INDEX idx_productos_sucursal_id ON productos (sucursal_id);
