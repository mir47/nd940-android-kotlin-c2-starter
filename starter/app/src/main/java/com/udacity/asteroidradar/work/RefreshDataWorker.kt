package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.db.AsteroidDatabase
import com.udacity.asteroidradar.domain.AsteroidRepository
import com.udacity.asteroidradar.util.getNextSevenDaysFormattedDates
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters):
        CoroutineWorker(appContext, params) {

    companion object {
        const val WORKER_NAME = "RefreshDataWorker"
    }

    // TODO: worker not working
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext)
        val repository = AsteroidRepository(database.asteroidDao, database.imageDao)

        return try {
            val dates = getNextSevenDaysFormattedDates()
            repository.refreshAsteroids(dates)
            repository.purgeAsteroidsBeforeDate(dates.first())
            repository.refreshImage()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
