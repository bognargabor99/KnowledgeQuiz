package hu.bme.mobweb.knowledgequiz.data

import androidx.room.*

@Dao
interface ScoreDao {
    @Query("SELECT * FROM highscores")
    fun getAll(): List<Score>

    @Insert
    fun insert(score: Score): Long

    @Query("DELETE from highscores")
    fun removeAll()

    @Delete
    fun delete(item: Score)
}