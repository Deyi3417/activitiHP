package com.example.activiti.service;

import com.example.activiti.entity.HistoryInstInfoDTO;
import com.example.activiti.entity.ProcessDTO;
import com.example.activiti.entity.TaskRepresentation;

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
     *
     * @param processDefinitionKey 流程定义实例key
     */
    void getResource(String processDefinitionKey);

    /**
     * 获取历史信息
     *
     * @param processInstanceId 流程实例ID
     * @return List<HistoryInstInfoDTO>
     */
    List<HistoryInstInfoDTO> getHistoryInfo(String processInstanceId);

    /**
     * 获取组任务-如果 act_ru_task 表中字段 ASSIGNEE_ 有值，则根据候选人无法获取到任务
     *
     * @param processDefinitionKey 流程定义key
     * @param candidateUserName    候选人名称
     * @return List<TaskRepresentation>
     */
    List<TaskRepresentation> findGroupTask(String processDefinitionKey, String candidateUserName);

    /**
     * 拾取任务：将 act_ru_task 表的字段 ASSIGNEE_ 赋值
     *
     * @param taskId            任务ID
     * @param candidateUserName 候选人
     * @return true or false
     */
    boolean claimTask(String taskId, String candidateUserName);

    boolean claimTask02(String taskId, String candidateUserName);


    /**
     * 归还任务: 将 act_ru_task 表的字段 ASSIGNEE_ 置为 null
     *
     * @param taskId   任务ID
     * @param assignee 已指派的人
     * @return true or false
     */
    boolean returnTask(String taskId, String assignee);

    /**
     * 任务交接： 将当前任务指派给另外一个人做
     * 将 assignee 的任务指派给 candidateUser 审批
     *
     * @param taskId 任务ID
     * @param assignee 当前做任务的人
     * @param candidateUser 要交接、转派的人
     * @return true or false
     */
    boolean taskHandover(String taskId, String assignee, String candidateUser);
}
