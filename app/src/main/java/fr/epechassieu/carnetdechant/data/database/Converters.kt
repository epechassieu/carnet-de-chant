package fr.epechassieu.carnetdechant.data.database

import androidx.room.TypeConverter
import fr.epechassieu.carnetdechant.domain.model.Category

/**
 * Type converters to allow Room to reference complex data types.
 *
 * This class provides methods to convert between a list of [Category] objects
 * and a comma-separated [String] for database storage.
 */
class Converters {

    @TypeConverter
    fun fromCategoryList(categories: List<Category>): String {
        return categories.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toCategoryList(data: String): List<Category> {
        if (data.isBlank()) return emptyList()
        //return data.split(",").map { Category.valueOf(it) }
        return data.split(",").mapNotNull { Category.valueOf(it.trim())
        try {
            Category.valueOf(it.trim())
        } catch (e: IllegalArgumentException) {
            null
        }}

    }
}