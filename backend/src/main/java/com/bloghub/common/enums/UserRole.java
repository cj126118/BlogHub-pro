package com.bloghub.common.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {

    USER("user", "普通用户"),
    ADMIN("admin", "管理员");

    private final String value;
    private final String displayName;

    UserRole(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static UserRole fromValue(String value) {
        for (UserRole role : values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        return USER;
    }
}
