package com.example.app_marifin_javadroid.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

/**
 * Room Entity for Transaction Categories (System defaults and user custom categories).
 */
@Entity(
        tableName = "categories",
        indices = {
                @Index("user_id"),
                @Index("is_default")
        }
)
public class CategoryEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @Nullable
    @ColumnInfo(name = "user_id")
    private String userId; // Null for default system categories

    @Nullable
    @ColumnInfo(name = "parent_id")
    private String parentId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "icon")
    private String icon = "ic_category_default";

    @NonNull
    @ColumnInfo(name = "color")
    private String color = "#1E56A0";

    @NonNull
    @ColumnInfo(name = "type")
    private String type = "expense"; // 'expense', 'income', 'both'

    @ColumnInfo(name = "is_default")
    private boolean isDefault = false;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite = false;

    @ColumnInfo(name = "is_active")
    private boolean isActive = true;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt = new Date();

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt = new Date();

    public CategoryEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @androidx.room.Ignore
    public CategoryEntity(@NonNull String id, @Nullable String userId, @NonNull String name,
                          @NonNull String icon, @NonNull String color, @NonNull String type,
                          boolean isDefault, boolean isFavorite) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.type = type;
        this.isDefault = isDefault;
        this.isFavorite = isFavorite;
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @Nullable
    public String getUserId() { return userId; }
    public void setUserId(@Nullable String userId) { this.userId = userId; }

    @Nullable
    public String getParentId() { return parentId; }
    public void setParentId(@Nullable String parentId) { this.parentId = parentId; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull
    public String getIcon() { return icon; }
    public void setIcon(@NonNull String icon) { this.icon = icon; }

    @NonNull
    public String getColor() { return color; }
    public void setColor(@NonNull String color) { this.color = color; }

    @NonNull
    public String getType() { return type; }
    public void setType(@NonNull String type) { this.type = type; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    @NonNull
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(@NonNull Date updatedAt) { this.updatedAt = updatedAt; }
}
