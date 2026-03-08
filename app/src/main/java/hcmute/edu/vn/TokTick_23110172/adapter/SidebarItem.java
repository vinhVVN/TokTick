package hcmute.edu.vn.TokTick_23110172.adapter;

import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;

public class SidebarItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_SMART_FILTER = 1;
    public static final int TYPE_USER_LIST = 2;

    public int type;
    public String headerText; // Cho TYPE_HEADER

    // Cho TYPE_SMART_FILTER
    public int smartFilterId;
    public String smartFilterName;
    public String smartFilterIcon;

    // Cho TYPE_USER_LIST
    public ListCategory userList;

    public int taskCount;

    // Các ID cho Smart Filters
    public static final int ID_TODAY = 101;
    public static final int ID_TOMORROW = 102;
    public static final int ID_NEXT_7_DAYS = 103;
    public static final int ID_THIS_WEEK = 104;
    public static final int ID_UNSCHEDULED = 105;

    private SidebarItem(int type) {
        this.type = type;
    }

    public static SidebarItem createHeader(String text) {
        SidebarItem item = new SidebarItem(TYPE_HEADER);
        item.headerText = text;
        return item;
    }

    public static SidebarItem createSmartFilter(int id, String name, String icon, int count) {
        SidebarItem item = new SidebarItem(TYPE_SMART_FILTER);
        item.smartFilterId = id;
        item.smartFilterName = name;
        item.smartFilterIcon = icon;
        item.taskCount = count;
        return item;
    }

    public static SidebarItem createUserList(ListCategory listCategory, int count) {
        SidebarItem item = new SidebarItem(TYPE_USER_LIST);
        item.userList = listCategory;
        item.taskCount = count;
        return item;
    }
}
