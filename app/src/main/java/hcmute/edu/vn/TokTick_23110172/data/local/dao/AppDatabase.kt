package hcmute.edu.vn.TokTick_23110172.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(entities = [Task::class, ListCategory::class], version = 5, exportSchema = false)
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
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            triggerPopulate()
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            triggerPopulate()
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun triggerPopulate() {
            INSTANCE?.let { database ->
                CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                    populateDatabase(database.taskDao())
                }
            }
        }

        suspend fun populateDatabase(taskDao: TaskDao) {
            // 1. Tạo các Smart List cố định
            taskDao.insertListCategory(ListCategory(name = "Today", iconName = "ic_today", isSmartList = true))
            taskDao.insertListCategory(ListCategory(name = "Tomorrow", iconName = "ic_tomorrow", isSmartList = true))
            taskDao.insertListCategory(ListCategory(name = "Next 7 Days", iconName = "ic_next7", isSmartList = true))

            // 2. Tạo các danh mục mặc định
            val inboxId = taskDao.insertListCategory(ListCategory(name = "Inbox", iconName = "ic_inbox", isSmartList = false)).toInt()
            taskDao.insertListCategory(ListCategory(name = "Work Tasks", iconName = "ic_work", isSmartList = false))
            taskDao.insertListCategory(ListCategory(name = "Study Goals", iconName = "ic_study", isSmartList = false))

            // 3. Chèn task mẫu
            taskDao.insertTask(Task(
                title = "Chào mừng bạn đến với TokTick!",
                listId = inboxId,
                dueDate = System.currentTimeMillis()
            ))
        }
    }
}
