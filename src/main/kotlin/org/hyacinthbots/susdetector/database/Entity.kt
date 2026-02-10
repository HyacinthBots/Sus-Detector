package org.hyacinthbots.susdetector.database

/**
 * Used for Database Entities, provides a primary identifying field to every table, with a type ([ID]) that can be
 * determined at declaration.
 */
@Suppress("PropertyName", "VariableNaming")
interface Entity<ID> {
    val _id: ID
}
