package com.netbanking.util;

import com.netbanking.enums.Role;

public class UserDetailsLocal {
    private static ThreadLocal<UserDetailsLocal> threadLocal = new ThreadLocal<>();

    private Long userId;
    private Role role;
    private Long branchId;

    public static UserDetailsLocal get() {
        return threadLocal.get();  // Now it can return null after removal
    }

    public static void set(UserDetailsLocal userDetails) {
        threadLocal.set(userDetails);  // Explicitly set the instance when needed
    }

    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        UserDetailsLocal instance = get();
        if (instance != null) {
            instance.userId = userId;
        }
    }

    public Role getRole() {
        return role;
    }

    public void setRole(String role) {
        UserDetailsLocal instance = get();
        if (instance != null) {
            instance.role = Role.valueOf(role);
        }
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        UserDetailsLocal instance = get();
        if (instance != null) {
            instance.branchId = branchId;
        }
    }

    public static void clear() {
        threadLocal.remove();
    }
}
