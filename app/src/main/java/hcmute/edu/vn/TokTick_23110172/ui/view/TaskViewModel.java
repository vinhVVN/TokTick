package hcmute.edu.vn.TokTick_23110172.ui.view;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hcmute.edu.vn.TokTick_23110172.adapter.SidebarItem;
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

    public List<SidebarItem> generateSidebarItems(List<Task> allTasks, List<ListCategory> userLists) {
        List<SidebarItem> items = new ArrayList<>();

        int todayCount = 0;
        int tomorrowCount = 0;
        int next7DaysCount = 0;
        int thisWeekCount = 0;
        int unscheduledCount = 0;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long todayStart = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_YEAR, 1);
        long tomorrowStart = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_YEAR, 1);
        long dayAfterTomorrowStart = cal.getTimeInMillis();

        cal.setTimeInMillis(todayStart);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        long next7DaysEnd = cal.getTimeInMillis();

        cal.setTimeInMillis(todayStart);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // Giả sử tuần bắt đầu từ Thứ 2 (Monday = 2)
        int daysUntilEndOfWeek = (8 - dayOfWeek) % 7;
        if (daysUntilEndOfWeek == 0) daysUntilEndOfWeek = 7;
        cal.add(Calendar.DAY_OF_YEAR, daysUntilEndOfWeek);
        long endOfWeek = cal.getTimeInMillis();

        for (Task task : allTasks) {
            if (task.isCompleted()) continue;

            Long dueDate = task.getDueDate();
            if (dueDate == null) {
                unscheduledCount++;
            } else {
                if (dueDate >= todayStart && dueDate < tomorrowStart) {
                    todayCount++;
                } else if (dueDate >= tomorrowStart && dueDate < dayAfterTomorrowStart) {
                    tomorrowCount++;
                }

                if (dueDate >= todayStart && dueDate < next7DaysEnd) {
                    next7DaysCount++;
                }
                if (dueDate >= todayStart && dueDate < endOfWeek) {
                    thisWeekCount++;
                }
            }
        }

        // 1. Header Filters - Sử dụng các icon hiện có trong drawable
        items.add(SidebarItem.createHeader("FILTERS"));
        items.add(SidebarItem.createSmartFilter(SidebarItem.ID_TODAY, "Today", "ic_alarm", todayCount));
        items.add(SidebarItem.createSmartFilter(SidebarItem.ID_TOMORROW, "Tomorrow", "ic_alarm", tomorrowCount));
        items.add(SidebarItem.createSmartFilter(SidebarItem.ID_NEXT_7_DAYS, "Next 7 Days", "ic_notes", next7DaysCount));
        items.add(SidebarItem.createSmartFilter(SidebarItem.ID_THIS_WEEK, "This Week", "ic_notes", thisWeekCount));
        items.add(SidebarItem.createSmartFilter(SidebarItem.ID_UNSCHEDULED, "Unscheduled", "ic_attach_file", unscheduledCount));

        // 2. Header Lists
        items.add(SidebarItem.createHeader("LISTS"));
        for (ListCategory category : userLists) {
            int count = 0;
            for (Task task : allTasks) {
                if (!task.isCompleted() && task.getListId() != null && task.getListId() == category.getId()) {
                    count++;
                }
            }
            items.add(SidebarItem.createUserList(category, count));
        }

        return items;
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
