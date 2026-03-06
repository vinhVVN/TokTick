package hcmute.edu.vn.TokTick_23110172.repository;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hcmute.edu.vn.TokTick_23110172.data.local.dao.TaskDao;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Tag;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.TaskTagCrossRef;

public class TaskRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private final LiveData<List<ListCategory>> allCategories;
    private final LiveData<List<Tag>> allTags;
    private final ExecutorService executorService;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
        this.allTasks = taskDao.getAllTasks();
        this.allCategories = taskDao.getAllCategories();
        this.allTags = taskDao.getAllTags();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<ListCategory>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<Tag>> getAllTags() {
        return allTags;
    }

    public LiveData<List<Task>> getTasksByListId(int listId) {
        return taskDao.getTasksByListId(listId);
    }

    public void insertTask(Task task) {
        executorService.execute(() -> taskDao.insertTask(task));
    }

    public void insertTaskWithTags(Task task, List<Tag> tags) {
        executorService.execute(() -> {
            long taskId = taskDao.insertTask(task);
            if (tags != null) {
                for (Tag tag : tags) {
                    taskDao.insertTaskTagCrossRef(new TaskTagCrossRef((int) taskId, tag.getTagId()));
                }
            }
        });
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

    public void insertTag(Tag tag) {
        executorService.execute(() -> taskDao.insertTag(tag));
    }
}
