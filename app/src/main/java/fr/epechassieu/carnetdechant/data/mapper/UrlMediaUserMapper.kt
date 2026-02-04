package fr.epechassieu.carnetdechant.data.mapper

import fr.epechassieu.carnetdechant.data.database.entities.UrlMediaUserEntity
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser

/**
 * Maps a [UrlMediaUserEntity] database entity to its corresponding [UrlMediaUser] domain model.
 *
 * @return A domain representation of the user media URL.
 */
fun UrlMediaUserEntity.toDomain(): UrlMediaUser {
    return UrlMediaUser(
        id = id,
        songId = songId,
        url = url
    )
}

/**
 * Converts a [UrlMediaUser] domain model to a [UrlMediaUserEntity] database entity.
 *
 * @return A new [UrlMediaUserEntity] containing the data from this domain model.
 */
fun UrlMediaUser.toEntity(): UrlMediaUserEntity {
    return UrlMediaUserEntity(
        id = id,
        songId = songId,
        url = url
    )
}