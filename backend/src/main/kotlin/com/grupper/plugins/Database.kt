package com.grupper.plugins

import com.grupper.data.database.DatabaseFactory
import io.ktor.server.application.*

fun Application.configureDatabase() {
    DatabaseFactory.init(environment)

    // Register shutdown hook to close database connection pool
    environment.monitor.subscribe(ApplicationStopped) {
        DatabaseFactory.close()
    }
}
