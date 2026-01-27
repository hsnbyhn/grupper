package com.grupper

import com.grupper.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureCORS()
    configureStatusPages()
    configureCallLogging()
    configureDatabase()
    configureRouting()
}
