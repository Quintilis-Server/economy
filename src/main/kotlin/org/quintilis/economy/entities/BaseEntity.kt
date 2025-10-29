package org.quintilis.economy.entities

import org.quintilis.economy.entities.annotations.*
import org.quintilis.economy.managers.DatabaseManager
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

private val KProperty1<*, *>.columnName: String
    get() = this.findAnnotation<Column>()?.name ?: this.name

abstract class BaseEntity {
    val tableName: String = this::class.findAnnotation<TableName>()?.name
        ?: throw IllegalArgumentException("A classe ${this::class.simpleName} não tem @TableName")

    val primaryKeyProperty = this::class.memberProperties
        .find { it.hasAnnotation<PrimaryKey>() }
        ?: this::class.memberProperties.find { it.name == "id"}
        ?: throw IllegalArgumentException("A classe ${this::class.simpleName} não tem @PrimaryKey")

    val primaryKeyColumnName: String = primaryKeyProperty.columnName



    fun save(){
        // Pega todas as propriedades da class
        val properties = this::class.primaryConstructor?.parameters
            ?.mapNotNull { param ->
                this::class.memberProperties.find { prop -> prop.name == param.name && !prop.hasAnnotation<Transient>() }
            }
            ?: emptyList()
        // Transforma para colunas sql
        val columns = properties.joinToString(", ") { it.columnName }
        // Transforma para tipos nomeados do jdbi
        val namedParams = properties.joinToString(", ") { ":${it.name}" }
        // cria o sql
        val updateSet = properties.filter { it.name != primaryKeyProperty.name }
            .joinToString(", ") { "${it.columnName} = :${it.name}" }

        val sql = """
            INSERT INTO $tableName ($columns)
            VALUES ($namedParams)
            ON CONFLICT ($primaryKeyColumnName) DO UPDATE SET
            $updateSet
        """.trimIndent()

        DatabaseManager.jdbi.useHandle<Exception> { handle ->
            handle.createUpdate(sql)
                .bindBean(this)
                .execute()
        }
    }
}