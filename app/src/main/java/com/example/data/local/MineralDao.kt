package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MineralDao {

    @Query("SELECT * FROM minerals ORDER BY name ASC")
    fun getAllMinerals(): Flow<List<MineralEntity>>

    @Query("SELECT * FROM minerals WHERE id = :id")
    fun getMineralById(id: Int): Flow<MineralEntity?>

    @Query("SELECT * FROM minerals WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteMinerals(): Flow<List<MineralEntity>>

    @Query("SELECT * FROM minerals WHERE isToxic = 1 OR isWaterSensitive = 1 ORDER BY name ASC")
    fun getHazardousMinerals(): Flow<List<MineralEntity>>

    /**
     * Multi-parametric search filtering by query, color, luster, hardness range, and classification
     */
    @Query("""
        SELECT * FROM minerals 
        WHERE (:query = '' OR name LIKE '%' || :query || '%' OR chemicalFormula LIKE '%' || :query || '%' OR geographicLocations LIKE '%' || :query || '%')
        AND (:colorCategory IS NULL OR :colorCategory = '' OR colorCategory = :colorCategory)
        AND (:luster IS NULL OR :luster = '' OR luster LIKE '%' || :luster || '%')
        AND (:classification IS NULL OR :classification = '' OR rockClassification = :classification)
        AND (hardnessMohsMax >= :minHardness AND hardnessMohsMin <= :maxHardness)
        ORDER BY name ASC
    """)
    fun searchMinerals(
        query: String,
        colorCategory: String?,
        luster: String?,
        classification: String?,
        minHardness: Double,
        maxHardness: Double
    ): Flow<List<MineralEntity>>

    @Query("SELECT COUNT(*) FROM minerals")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mineral: MineralEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(minerals: List<MineralEntity>)

    @Update
    suspend fun update(mineral: MineralEntity)

    @Query("UPDATE minerals SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)

    @Query("UPDATE minerals SET fieldNotes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Int, notes: String)
}
