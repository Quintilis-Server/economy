package org.quintilis.economy.entites.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class TableName(val name: String)

