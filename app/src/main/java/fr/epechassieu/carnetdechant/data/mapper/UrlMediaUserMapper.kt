package fr.epechassieu.carnetdechant.data.mapper

import fr.epechassieu.carnetdechant.data.database.entities.UrlMediaUserEntity
import fr.epechassieu.carnetdechant.domain.model.UrlMediaUser

// Entity -> Domain
fun UrlMediaUserEntity.toDomain(): UrlMediaUser {
    return UrlMediaUser(
        id = id,
        songId = songId,
        url = url
    )
}

// Domain -> Entity
fun UrlMediaUser.toEntity(): UrlMediaUserEntity {
    return UrlMediaUserEntity(
        id = id,
        songId = songId,
        url = url
    )
}