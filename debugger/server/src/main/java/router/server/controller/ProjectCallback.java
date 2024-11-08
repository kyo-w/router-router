package router.server.controller;

import router.server.entity.ProjectEntity;

/**
 * 所有操作都必须在项目级下操作
 */
public interface ProjectCallback {
    ApiResponse callback(ProjectEntity project);
}