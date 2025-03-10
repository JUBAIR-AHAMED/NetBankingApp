package com.netbanking.enumHelper;

import java.util.Set;
import java.util.Map;
import com.netbanking.mapper.PermissionLoader;

public enum UserAccessibleMethods {
	CUSTOMER, EMPLOYEE, MANAGER;

    private static final Map<UserAccessibleMethods, Map<String, Set<String>>> rolePermissions;

    static {
        rolePermissions = PermissionLoader.loadPermissions("permissions.yml");
    }

    public static boolean isAuthorized(UserAccessibleMethods role, String path, String method) {
        Map<String, Set<String>> permissions = rolePermissions.get(role);
        Set<String> allowedMethods = permissions.get(path);
        return allowedMethods != null && allowedMethods.contains(method);
    }
    
    public static boolean isPathPresent(String path, String method) {
        Map<String, Set<String>> permissions = rolePermissions.get(UserAccessibleMethods.MANAGER);
        Set<String> allowedMethods = permissions.get(path);
        return allowedMethods != null;
    }
    
    public static boolean isMethodPresent(String path, String method) {
        Map<String, Set<String>> permissions = rolePermissions.get(UserAccessibleMethods.MANAGER);
        Set<String> allowedMethods = permissions.get(path);
        return allowedMethods != null && allowedMethods.contains(method);
    }
}