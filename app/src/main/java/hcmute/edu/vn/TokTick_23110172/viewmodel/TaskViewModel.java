package hcmute.edu.vn.TokTick_23110172.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Tag;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;
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

    public LiveData<List<Task>> getTasksByListId(int listId) {
        return repository.getTasksByListId(listId);
    }

    public void insert(Task task) {
        repository.insertTask(task);
    }

    public void insertWithTags(Task task, List<Tag> tags) {
        repository.insertTaskWithTags(task, tags);
    }

    public void update(Task task) {
        repository.updateTask(task);
    }

    public void delete(Task task) {
        repository.deleteTask(task);
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
