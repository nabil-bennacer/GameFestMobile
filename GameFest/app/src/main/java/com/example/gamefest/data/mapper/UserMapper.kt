package com.example.gamefest.data.mapper

import com.example.gamefest.data.local.entity.UserEntity
import com.example.gamefest.data.remote.dto.UserDto

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        role = this.role
    )
}