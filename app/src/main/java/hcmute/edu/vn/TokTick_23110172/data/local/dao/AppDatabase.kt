package hcmute.edu.vn.TokTick_23110172.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory
import hcmute.edu.vn.TokTick_23110172.data.local.dao.TaskDao

@Database(entities = [Task::class, ListCategory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "toktick_database"
                )
                    // Cho phép destroy database cũ nếu bạn tăng version (phù hợp lúc đang dev)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}