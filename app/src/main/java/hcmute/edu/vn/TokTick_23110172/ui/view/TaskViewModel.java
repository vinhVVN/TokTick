package hcmute.edu.vn.TokTick_23110172.ui.view;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.SubTask;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Tag;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.TaskFullDetails;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.TaskWithSubtasks;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;

public class TaskViewModel extends ViewModel {

    private final TaskRepository repository;
    private final LiveData<List<Task>> allTasks;
    private final LiveData<List<ListCategory>> allCategories;
    private final LiveData<List<Tag>> allTags;

    public TaskViewModel(TaskRepository repository) {
        this.repository = repository;
        this.allTasks = repository.getAllTasks();
        this.allCategories = repository.getAllCategories();
        this.allTags = repository.getAllTags();
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

    public void updateListCategories(List<ListCategory> categories) {
        repository.updateListCategories(categories);
    }

    public LiveData<ListCategory> getListCategoryById(int id) {
        return repository.getListCategoryById(id);
    }

    public void updateListCategory(ListCategory category) {
        repository.updateListCategory(category);
    }

    public void deleteListCategory(ListCategory category) {
        repository.deleteListCategory(category);
    }

    public LiveData<List<Task>> getTasksByListId(int listId) {
        return repository.getTasksByListId(listId);
    }

    public LiveData<TaskWithSubtasks> getTaskWithSubtasksById(int taskId) {
        return repository.getTaskWithSubtasksById(taskId);
    }

    public LiveData<TaskFullDetails> getTaskFullDetailsById(int taskId) {
        return repository.getTaskFullDetailsById(taskId);
    }

    public void insert(Task task) {
        repository.insertTask(task);
    }

    public void insertWithTags(Task task, List<Tag> tags) {
        repository.insertTaskWithTags(task, tags);
    }

    public void insertTaskWithDetails(Task task, List<Tag> tags, List<String> subTasks) {
        repository.insertTaskWithDetails(task, tags, subTasks);
    }

    public void update(Task task) {
        repository.updateTask(task);
    }

    public void updateTaskWithSubtasks(Task task, List<SubTask> subTasks) {
        repository.updateTaskWithSubtasks(task, subTasks);
    }

    public void updateTaskFullDetails(Task task, List<SubTask> subTasks, List<Tag> tags) {
        repository.updateTaskFullDetails(task, subTasks, tags);
    }

    public void delete(Task task) {
        repository.deleteTask(task);
    }

    public void deleteSubTask(SubTask subTask) {
        repository.deleteSubTask(subTask);
    }

    public void insertCategory(ListCategory category) {
        repository.insertListCategory(category);
    }

    public void insertTag(Tag tag) {
        repository.insertTag(tag);
    }

    public static class TaskViewModelFactory implements ViewModelProvider.Factory {
        private final TaskRepository repository;

        public TaskViewModelFactory(TaskRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(TaskViewModel.class)) {
                return (T) new TaskViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
