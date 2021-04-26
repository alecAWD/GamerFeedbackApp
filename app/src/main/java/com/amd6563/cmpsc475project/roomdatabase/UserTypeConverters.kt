package com.amd6563.cmpsc475project.roomdatabase

import androidx.room.TypeConverter
import java.util.*

class UserTypeConverters {
    @TypeConverter
    fun toUUID(uuid: String?) : UUID?{
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?) : String?{
        return uuid?.toString()
    }
}