package hu.bme.mobweb.knowledgequiz.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import hu.bme.mobweb.knowledgequiz.R

@Entity(tableName = "highscores")
data class Score(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "player") val player: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "goodAnswers") val goodAnswers: Int,
    @ColumnInfo(name = "time") val time: Int
)