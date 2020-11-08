package hu.bme.mobweb.knowledgequiz.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "highscores")
data class Score(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "player") val player: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "goodAnswers") val goodAnswers: Int,
    @ColumnInfo(name = "time") val time: Int
) {
    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass)
            return false
        other as Score

        if (id != other.id)
            return false
        if (player != other.player)
            return false
        if (category != other.category)
            return false
        if (goodAnswers != other.goodAnswers)
            return false
        if (time != other.time)
            return false
        return true
    }
}