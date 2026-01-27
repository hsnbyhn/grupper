package com.grupper.data.database

import com.grupper.data.database.tables.CommentsTable
import com.grupper.data.database.tables.GroupsTable
import com.grupper.data.database.tables.PostsTable
import com.grupper.data.database.tables.TagsTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init(environment: ApplicationEnvironment) {
        val config = environment.config

        val driverClassName = config.property("database.driverClassName").getString()
        val jdbcUrl = config.property("database.jdbcUrl").getString()
        val username = config.property("database.username").getString()
        val password = config.property("database.password").getString()
        val maxPoolSize = config.propertyOrNull("database.maxPoolSize")?.getString()?.toInt() ?: 10

        dataSource = createHikariDataSource(
            driverClassName = driverClassName,
            jdbcUrl = jdbcUrl,
            username = username,
            password = password,
            maxPoolSize = maxPoolSize
        )

        Database.connect(dataSource)

        // Create tables on startup (for development)
        // In production, use migrations instead
        transaction {
            createTables()
        }

        environment.log.info("Database connected: $jdbcUrl")
    }

    private fun createHikariDataSource(
        driverClassName: String,
        jdbcUrl: String,
        username: String,
        password: String,
        maxPoolSize: Int
    ): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            this.maximumPoolSize = maxPoolSize
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            // Connection pool settings
            this.minimumIdle = 2
            this.idleTimeout = 30000      // 30 seconds
            this.maxLifetime = 1800000    // 30 minutes
            this.connectionTimeout = 30000 // 30 seconds

            // Validation
            this.validate()
        }

        return HikariDataSource(hikariConfig)
    }

    private fun createTables() {
        SchemaUtils.create(
            GroupsTable,
            TagsTable,
            PostsTable,
            CommentsTable
        )
    }

    fun close() {
        if (::dataSource.isInitialized) {
            dataSource.close()
        }
    }
}
