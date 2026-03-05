package hcmute.edu.vn.TokTick_23110172.adapter;

import java.util.Objects;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;

public abstract class TaskItem {
    public static class Header extends TaskItem {
        private final String title;
        private final int count;
        private boolean isExpanded;

        public Header(String title, int count, boolean isExpanded) {
            this.title = title;
            this.count = count;
            this.isExpanded = isExpanded;
        }

        public String getTitle() { return title; }
        public int getCount() { return count; }
        public boolean isExpanded() { return isExpanded; }
        public void setExpanded(boolean expanded) { isExpanded = expanded; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Header header = (Header) o;
            return count == header.count && isExpanded == header.isExpanded && Objects.equals(title, header.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, count, isExpanded);
        }
    }

    public static class TaskData extends TaskItem {
        private final Task task;

        public TaskData(Task task) {
            this.task = task;
        }

        public Task getTask() { return task; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TaskData taskData = (TaskData) o;
            return Objects.equals(task, taskData.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task);
        }
    }
}
