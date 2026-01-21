package fr.epechassieu.carnetdechant.data.database

import androidx.room.TypeConverter
import fr.epechassieu.carnetdechant.domain.model.Category

class Converters {

    @TypeConverter
    fun fromCategoryList(categories: List<Category>): String {
        return categories.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toCategoryList(data: String): List<Category> {
        if (data.isBlank()) return emptyList()
        return data.split(",").map { Category.valueOf(it) }
    }
}