DROP TABLE IF EXISTS compilations_events;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS events_view;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    --confirmed_requests BIGINT DEFAULT 0,
    created_on TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(7000),
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    location_lat FLOAT NOT NULL,
    location_lon FLOAT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INT DEFAULT 0,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE NOT NULL,
    state VARCHAR(10) NOT NULL,
    title VARCHAR(120) NOT NULL,

    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_initiator FOREIGN KEY (initiator_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS events_view (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_ip VARCHAR(20) NOT NULL,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    requestor_id BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT uq_request UNIQUE(event_id, requestor_id),
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE RESTRICT,
    CONSTRAINT fk_requestor FOREIGN KEY (requestor_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGSERIAL PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events (
    event_id BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE RESTRICT,
    CONSTRAINT fk_compilation FOREIGN KEY (compilation_id) REFERENCES compilations(id) ON DELETE RESTRICT
);