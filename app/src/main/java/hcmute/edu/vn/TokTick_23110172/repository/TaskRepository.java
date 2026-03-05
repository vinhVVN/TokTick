package hcmute.edu.vn.TokTick_23110172.repository;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hcmute.edu.vn.TokTick_23110172.data.local.dao.TaskDao;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;

public class TaskRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private final LiveData<List<ListCategory>> allCategories;
    private final ExecutorService executorService;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
        this.allTasks = taskDao.getAllTasks();
        this.allCategories = taskDao.getAllCategories();
        // Sử dụng SingleThreadExecutor hoặc FixedThreadPool để xử lý background tasks
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<ListCategory>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<Task>> getTasksByListId(int listId) {
        return taskDao.getTasksByListId(listId);
    }

    public void insertTask(Task task) {
        executorService.execute(() -> taskDao.insertTask(task));
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.updateTask(task));
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> taskDao.deleteTask(task));
    }

    public void insertListCategory(ListCategory category) {
        executorService.execute(() -> taskDao.insertListCategory(category));
    }
}
