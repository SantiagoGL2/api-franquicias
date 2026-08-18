package com.franquicias.nequi.infrastructure.postgresql;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This module has no main application class of its own (it isn't
 * executable — see build.gradle) so integration tests need a
 * {@code @SpringBootConfiguration} anchor to build a context against. This
 * scans the module's own packages only: entity/repository/mapper/adapter.
 */
@SpringBootApplication
class TestApplication {
}
