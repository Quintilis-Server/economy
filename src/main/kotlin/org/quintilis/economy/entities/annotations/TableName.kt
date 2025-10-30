package org.quintilis.economy.entities.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class TableName(val name: String)

