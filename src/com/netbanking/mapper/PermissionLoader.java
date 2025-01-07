package com.netbanking.mapper;

import org.yaml.snakeyaml.Yaml;

import com.netbanking.enumHelper.UserAccessibleMethods;

import java.io.InputStream;
import java.util.*;

public class PermissionLoader {
    public static Map<UserAccessibleMethods, Map<String, Set<String>>> loadPermissions(String yamlFilePath) {
        Yaml yaml = new Yaml();
        Map<UserAccessibleMethods, Map<String, Set<String>>> permissions = new HashMap<>();
        try (InputStream inputStream = PermissionLoader.class.getClassLoader().getResourceAsStream(yamlFilePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("YAML file not found: " + yamlFilePath);
            }

            Map<String, Map<String, List<String>>> yamlData = yaml.load(inputStream);
            
            yamlData.forEach((role, paths) -> {
                UserAccessibleMethods userRole = UserAccessibleMethods.valueOf(role.toUpperCase());
                Map<String, Set<String>> pathPermissions = new HashMap<>();

                paths.forEach((path, methods) -> {
                    pathPermissions.put(path, new HashSet<>(methods));
                });

                permissions.put(userRole, pathPermissions);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissions;
    }
}