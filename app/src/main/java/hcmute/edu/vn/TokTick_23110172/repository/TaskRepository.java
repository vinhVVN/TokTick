package hcmute.edu.vn.TokTick_23110172.repository;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hcmute.edu.vn.TokTick_23110172.data.local.dao.TaskDao;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.SubTask;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Tag;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.TaskFullDetails;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.TaskTagCrossRef;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.TaskWithSubtasks;

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

    public void insertTaskWithDetails(Task task, List<Tag> tags, List<String> subTaskTitles) {
        executorService.execute(() -> {
            long taskId = taskDao.insertTask(task);
            if (tags != null && !tags.isEmpty()) {
                for (Tag tag : tags) {
                    taskDao.insertTaskTagCrossRef(new TaskTagCrossRef((int) taskId, tag.getTagId()));
                }
            }
            if (subTaskTitles != null && !subTaskTitles.isEmpty()) {
                List<SubTask> subTasks = new ArrayList<>();
                for (String title : subTaskTitles) {
                    if (title != null && !title.trim().isEmpty()) {
                        subTasks.add(new SubTask((int) taskId, title, false));
                    }
                }
                if (!subTasks.isEmpty()) {
                    taskDao.insertSubTasks(subTasks);
                }
            }
        });
    }

    public LiveData<List<TaskWithSubtasks>> getTasksWithSubtasks() {
        return taskDao.getTasksWithSubtasks();
    }

    public LiveData<TaskWithSubtasks> getTaskWithSubtasksById(int taskId) {
        return taskDao.getTaskWithSubtasksById(taskId);
    }

    public LiveData<TaskFullDetails> getTaskFullDetailsById(int taskId) {
        return taskDao.getTaskFullDetailsById(taskId);
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.updateTask(task));
    }

    public void updateTaskWithSubtasks(Task task, List<SubTask> subTasks) {
        executorService.execute(() -> {
            taskDao.updateTask(task);
            if (subTasks != null) {
                taskDao.insertSubTasks(subTasks);
            }
        });
    }

    public void updateTaskFullDetails(Task task, List<SubTask> subTasks, List<Tag> tags) {
        executorService.execute(() -> {
            taskDao.updateTask(task);
            if (subTasks != null) {
                taskDao.insertSubTasks(subTasks);
            }
            taskDao.deleteTagsForTask(task.getId());
            if (tags != null) {
                for (Tag tag : tags) {
                    taskDao.insertTaskTagCrossRef(new TaskTagCrossRef(task.getId(), tag.getTagId()));
                }
            }
        });
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> taskDao.deleteTask(task));
    }

    public void insertListCategory(ListCategory category) {
        executorService.execute(() -> taskDao.insertListCategory(category));
    }

    public void updateListCategory(ListCategory category) {
        executorService.execute(() -> taskDao.updateListCategory(category));
    }

    public void deleteListCategory(ListCategory category) {
        executorService.execute(() -> taskDao.deleteListCategory(category));
    }

    public LiveData<ListCategory> getListCategoryById(int id) {
        return taskDao.getListCategoryById(id);
    }

    public void updateListCategories(List<ListCategory> categories) {
        executorService.execute(() -> {
            for (ListCategory category : categories) {
                taskDao.updateListCategory(category);
            }
        });
    }

    public void insertTag(Tag tag) {
        executorService.execute(() -> taskDao.insertTag(tag));
    }

    public void deleteSubTask(SubTask subTask) {
        executorService.execute(() -> taskDao.deleteSubTask(subTask));
    }
}
