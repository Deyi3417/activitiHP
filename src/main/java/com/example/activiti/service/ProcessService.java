package com.example.activiti.service;

import com.example.activiti.entity.ProcessDTO;

import java.util.List;

/**
 * @author : HP
 * @date : 2022/9/16
 */
public interface ProcessService {

    /**
     * 通过 processDefinitionKey 获取流程实例 by
     *
     * @param processDefinitionKey
     * @return
     */
    List<ProcessDTO> getProcessDefByKey(String processDefinitionKey);

    /**
     * 根据部署ID删除流程实例
     *
     * @param deploymentId 部署ID
     */
    void deleteProcessBydeploymentId(String deploymentId);

    /**
     * 获取文件资源信息
     * @param processDefinitionKey 流程定义实例key
     */
    void getResource(String processDefinitionKey);

    void getHistoryInfo(String processInstanceId);
}
