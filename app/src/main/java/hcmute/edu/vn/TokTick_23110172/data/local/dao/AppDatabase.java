package hcmute.edu.vn.TokTick_23110172.data.local.dao;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;

@Database(entities = {Task.class, ListCategory.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "toktick_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Instead of accessing INSTANCE directly which might be null,
                // we can rely on the fact that by the time this executes,
                // the build() should have finished or we can handle null safely.
                AppDatabase database = INSTANCE;
                if (database != null) {
                    TaskDao dao = database.taskDao();
                    populateDatabase(dao);
                }
            });
        }

        @Override
        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);
            databaseWriteExecutor.execute(() -> {
                AppDatabase database = INSTANCE;
                if (database != null) {
                    TaskDao dao = database.taskDao();
                    populateDatabase(dao);
                }
            });
        }
    };

    private static void populateDatabase(TaskDao taskDao) {
        // Use long for IDs returned by insert
        taskDao.insertListCategory(new ListCategory("Today", "ic_today", true));
        taskDao.insertListCategory(new ListCategory("Tomorrow", "ic_tomorrow", true));
        taskDao.insertListCategory(new ListCategory("Next 7 Days", "ic_next7", true));

        long inboxId = taskDao.insertListCategory(new ListCategory("Inbox", "ic_inbox", false));
        taskDao.insertListCategory(new ListCategory("Work Tasks", "ic_work", false));
        taskDao.insertListCategory(new ListCategory("Study Goals", "ic_study", false));

        taskDao.insertTask(new Task(
                "Chào mừng bạn đến với TokTick!",
                (int) inboxId,
                null,
                null,
                false,
                false,
                false,
                false
        ));
    }
}
