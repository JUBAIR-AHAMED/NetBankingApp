package com.netbanking.util;

import com.netbanking.enums.Role;

public class UserDetailsLocal {
    private static final ThreadLocal<UserDetailsLocal> threadLocal = ThreadLocal.withInitial(UserDetailsLocal::new);

    private Long userId;
    private Role role;
    private Long branchId;

    private UserDetailsLocal() {}

    public static UserDetailsLocal get() {
        return threadLocal.get();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = Role.valueOf(role);
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public static void clear() {
    	threadLocal.remove();
    }
}
