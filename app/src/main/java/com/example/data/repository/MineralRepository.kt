package com.example.data.repository

import com.example.data.local.MineralDao
import com.example.data.local.MineralDatabaseCallback
import com.example.data.local.MineralEntity
import kotlinx.coroutines.flow.Flow

class MineralRepository(private val mineralDao: MineralDao) {

    val allMinerals: Flow<List<MineralEntity>> = mineralDao.getAllMinerals()
    val favoriteMinerals: Flow<List<MineralEntity>> = mineralDao.getFavoriteMinerals()
    val hazardousMinerals: Flow<List<MineralEntity>> = mineralDao.getHazardousMinerals()

    fun getMineralById(id: Int): Flow<MineralEntity?> = mineralDao.getMineralById(id)

    fun searchMinerals(
        query: String,
        colorCategory: String?,
        luster: String?,
        classification: String?,
        minHardness: Double,
        maxHardness: Double
    ): Flow<List<MineralEntity>> {
        return mineralDao.searchMinerals(
            query = query.trim(),
            colorCategory = if (colorCategory.isNullOrBlank() || colorCategory == "All") null else colorCategory,
            luster = if (luster.isNullOrBlank() || luster == "All") null else luster,
            classification = if (classification.isNullOrBlank() || classification == "All") null else classification,
            minHardness = minHardness,
            maxHardness = maxHardness
        )
    }

    suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        mineralDao.setFavorite(id, isFavorite)
    }

    suspend fun updateNotes(id: Int, notes: String) {
        mineralDao.updateNotes(id, notes)
    }

    suspend fun ensureDatabasePopulated() {
        if (mineralDao.getCount() == 0) {
            MineralDatabaseCallback(kotlinx.coroutines.GlobalScope) { mineralDao }.populateDatabase(mineralDao)
        }
    }
}
