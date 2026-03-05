package hcmute.edu.vn.TokTick_23110172.adapter;

import java.util.Objects;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;

public abstract class SidebarItem {
    public static class Header extends SidebarItem {
        private final String title;

        public Header(String title) {
            this.title = title;
        }

        public String getTitle() { return title; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Header header = (Header) o;
            return Objects.equals(title, header.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title);
        }
    }

    public static class MenuItem extends SidebarItem {
        private final ListCategory category;
        private final int taskCount;

        public MenuItem(ListCategory category, int taskCount) {
            this.category = category;
            this.taskCount = taskCount;
        }

        public ListCategory getCategory() { return category; }
        public int getTaskCount() { return taskCount; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MenuItem menuItem = (MenuItem) o;
            return taskCount == menuItem.taskCount && Objects.equals(category, menuItem.category);
        }

        @Override
        public int hashCode() {
            return Objects.hash(category, taskCount);
        }
    }
}
