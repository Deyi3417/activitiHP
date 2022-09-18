package com.example.activiti.service.impl;

import com.example.activiti.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : HP
 * @date : 2022/9/14
 */
@Service
@Slf4j
public class ActivitiServiceImpl implements ActivitiService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public void startProcess() {
        ProcessInstance test = runtimeService.startProcessInstanceByKey("test");
        log.info(
                "--{}--{}--{}--{}--{}", test.getBusinessKey(), test.getDescription(), test.getProcessDefinitionId(), test.getId(), test.getActivityId());
    }

    @Override
    public Deployment deployBpmn(String bpmnName, String filePathAndName) {
        Deployment deploy = repositoryService.createDeployment()
                .name(bpmnName)
                .addClasspathResource(filePathAndName)
                .deploy();
        log.info("========部署完成========{}", deploy);
        return deploy;
    }

    @Override
    public List<Task>  getTasks(String assignee) {
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee).list();
        log.info("========list:========{}", list);
        return list;
    }

    @Override
    public List<Task> getTasks(String processDefinitionKey, String assignee) {
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(assignee)
                .list();
        log.info("========tasks:========{}", tasks);
        // 打印部分信息
        for (Task task : tasks) {
            log.info("========流程实例ID:task.getProcessInstanceId()========{}",task.getProcessInstanceId());
            log.info("========任务ID:task.getId()========{}",task.getId());
            log.info("========任务名称:task.getName()========{}",task.getName());
            log.info("========任务负责人:task.getAssignee()========{}",task.getAssignee());
        }
        return tasks;
    }

    @Override
    public void completeTask(String taskId) {
        taskService.complete(taskId);
        // todo 更新businessKey关联的业务信息
    }

    @Override
    public Task completeTask(String processDefinitionKey, String assignee) {
        // 根据流程实例 key 和 指派人 获取任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(assignee)
                .singleResult();
        if (task == null) {
            return null;
        }
        log.info("========流程实例ID:task.getProcessInstanceId()========{}",task.getProcessInstanceId());
        log.info("========任务ID:task.getId()========{}",task.getId());
        log.info("========任务名称:task.getName()========{}",task.getName());
        log.info("========任务负责人:task.getAssignee()========{}",task.getAssignee());
        taskService.complete(task.getId());
        return task;
    }


    @Override
    public Deployment deployBpmn(String bpmnName, String filePathAndName, String pngPath) {
        Deployment deploy = repositoryService.createDeployment()
                .name(bpmnName)
                .addClasspathResource(filePathAndName)
                .addClasspathResource(pngPath)
                .deploy();
        log.info("========部署完成========{}", deploy);
        return deploy;
    }

    @Override
    public boolean startFlowWork(String processDefinitionKey) {
        // 默认使用最新部署的流程实例版本
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        log.info("========startFlowWork========{}", instance);
        log.info("========startFlowWork:流程实例ID========{}", instance.getId());
        log.info("========instance.getActivityId():当前活动ID========{}", instance.getActivityId());
        if (instance.getId().length() > 0) {
            // 获取流程启动产生的taskId
            String taskId = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId())
                    .singleResult().getId();
            log.info("=======taskId========{}", taskId);
            log.info("=======instance.getProcessInstanceId()========{}", instance.getProcessInstanceId());
            log.info("=======instance.getProcessDefinitionId()========{}", instance.getProcessDefinitionId());
            return true;
        }
        return false;
    }

    /**
     * 通过自定义的key启动
     *
     * @param workFlowName 工作流名称
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startHoliday(String workFlowName) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(workFlowName);

        // 添加业务key 到activiti表
        ProcessInstance leave = runtimeService.startProcessInstanceByKey("a0914", "99999");

        leave.getBusinessKey();
        log.info("========startHoliday========{}", processInstance);
        if (processInstance.getProcessInstanceId().length() > 0) {
            // 获取流程启动产生的taskId
            String taskId = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                    .singleResult().getId();
            log.info("=======taskId========{}", taskId);
            // 自动完成第一个提交
            taskService.complete(taskId);
            return true;
        }
        return false;
    }


    public void suspendSingleProcessInstance() {
        // 流程实例挂起-单个挂起
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId("27501").singleResult();
        boolean suspended = instance.isSuspended();
        String instanceId = instance.getId();
        if (suspended) {
            // 如果已暂停，则执行激活
            runtimeService.activateProcessInstanceById(instanceId);
        } else {
            // 如果是激活，则执行暂停
            runtimeService.suspendProcessInstanceById(instanceId);
        }
    }

    public void suspendAllProcessInstance() {
        // 流程实例挂起-所有
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("a0914").singleResult();
        boolean suspended = processDefinition.isSuspended();
        String definitionId = processDefinition.getId();
        if (suspended) {
            repositoryService.activateProcessDefinitionById(definitionId, true, null);
        } else {
            repositoryService.suspendProcessDefinitionById(definitionId,true,null);
        }
    }
}
