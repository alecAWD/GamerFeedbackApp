package com.amd6563.cmpsc475project.roomdatabase

import android.media.Image
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*
import java.util.UUID.*

@Entity(tableName="user")
data class UserEntity(val id: UUID = randomUUID(),
                      @PrimaryKey var username: String,
                      var password: String,
                      var type: Boolean)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            childColumns = ["username"],
            parentColumns = ["username"]
        )
    ]
)
data class Game(@PrimaryKey val id: UUID = UUID.randomUUID(),
                var gameTitle: String,
                var gameReleaseDate : String,
                var gameDeveloper : String,
                var gameImage : ByteArray?,
                var username: String)
@Entity
data class FeedBack(@PrimaryKey val id: UUID = randomUUID(),
                    var gameTitle: String,
                    var feedbackTitle: String,
                    var feedback: String,
                    var username: String)