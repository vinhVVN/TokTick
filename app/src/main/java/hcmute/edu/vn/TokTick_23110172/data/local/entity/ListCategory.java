package hcmute.edu.vn.TokTick_23110172.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "list_categories")
public class ListCategory {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;         // Ví dụ: "Work Tasks", "Today"
    private String iconName;     // Lưu tên icon để hiển thị (VD: "ic_work")
    private boolean isSmartList;  // True nếu là Today/Next 7 Days, False nếu là List tự tạo
    private int orderIndex;      // Thứ tự hiển thị

    public ListCategory(String name, String iconName, boolean isSmartList) {
        this.name = name;
        this.iconName = iconName;
        this.isSmartList = isSmartList;
        this.orderIndex = 0;
    }

    // Default constructor for Room
    public ListCategory() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isSmartList() {
        return isSmartList;
    }

    public void setSmartList(boolean smartList) {
        isSmartList = smartList;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
