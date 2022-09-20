package com.example.activiti.service;

import com.example.activiti.entity.StartProcessInstanceDTO;
import com.example.activiti.entity.vo.StartProcessInstanceVO;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * @author : HP
 * @date : 2022/9/14
 */
public interface ActivitiService {

    /**
     * 启动流程
     */
    public void startProcess();

    /**
     * 获取任务
     *
     * @param assignee 指派着
     * @return List<Task>
     */
    public List<Task> getTasks(String assignee);

    /**
     * 部署流程
     *
     * @param bpmnName        名称
     * @param filePathAndName 路径
     * @return Deployment
     */
    Deployment deployBpmn(String bpmnName, String filePathAndName);

    /**
     * 开始工作流
     *
     * @param workFlowName 工作流名称
     * @return Boolean
     */
    boolean startHoliday(String workFlowName);

    /**
     * 部署流程
     *
     * @param bpmnName        名称
     * @param filePathAndName bpmn20.xml 路径
     * @param pngPath         png图片路径
     * @return result
     */
    Deployment deployBpmn(String bpmnName, String filePathAndName, String pngPath);

    /**
     * 部署流程实例
     *
     * @param processDefinitionKey 流程实例定义key
     * @return result
     */
    boolean startFlowWork(String processDefinitionKey);

    /**
     * 获取个人任务
     *
     * @param processDefinitionKey 定义的流程实例key
     * @param assignee             指派人
     * @return
     */
    List<Task> getTasks(String processDefinitionKey, String assignee);


    /**
     * 完成个人任务
     *
     * @param taskId 任务id
     */
    void completeTask(String taskId);

    /**
     * 完成个人任务
     *
     * @param processDefinitionKey 定义的流程实例key
     * @param assignee             指派人
     * @return 已完成的任务
     */
    Task completeTask(String processDefinitionKey, String assignee);

    /**
     * 开启流程实例
     *
     * @param processInstanceVO 开启流程实例参数
     * @return result
     */
    boolean startProcessInstanceWithVariable(StartProcessInstanceVO processInstanceVO);

    /**
     * 开始流程实例
     *
     * @param startProcessInstanceDTO 开启流程实例所带参数，请假天数，指派人等参数
     * @return boolean-是否开启成功
     */
    boolean startProcessInstanceWithVariable(StartProcessInstanceDTO startProcessInstanceDTO);

    /**
     * 开始流程实例
     *
     * @param processDefinitionKey 流程定义实例key
     * @param variables            开启流程实例所需参数
     * @return true or false
     */
    boolean startProcessInstanceWithVariable(String processDefinitionKey, Map<String, Object> variables);
}
