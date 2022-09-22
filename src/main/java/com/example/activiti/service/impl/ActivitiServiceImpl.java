package com.example.activiti.service.impl;

import com.example.activiti.entity.Evection;
import com.example.activiti.entity.StartProcessInstanceDTO;
import com.example.activiti.entity.vo.StartProcessInstanceVO;
import com.example.activiti.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @Override
    public void startProcess() {
        ProcessInstance test = runtimeService.startProcessInstanceByKey("test");
        log.info("--{}--{}--{}--{}--{}", test.getBusinessKey(), test.getDescription(), test.getProcessDefinitionId(), test.getId(), test.getActivityId());
    }

    @Override
    public Deployment deployBpmn(String bpmnName, String filePathAndName) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);
        Deployment deploy = repositoryService.createDeployment().name(bpmnName).addClasspathResource(filePathAndName).deploy();
        log.info("========部署完成========{}", deploy);

        return deploy;
    }

    @Override
    public List<Task> getTasks(String assignee) {
        List<Task> list = taskService.createTaskQuery().taskAssignee(assignee).list();
        log.info("========list:========{}", list);
        return list;
    }

    @Override
    public List<Task> getTasks(String processDefinitionKey, String assignee) {
        List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskAssignee(assignee).list();
        log.info("========tasks:========{}", tasks);
        // 打印部分信息
        for (Task task : tasks) {
            log.info("========流程实例ID:task.getProcessInstanceId()========{}", task.getProcessInstanceId());
            log.info("========任务ID:task.getId()========{}", task.getId());
            log.info("========任务名称:task.getName()========{}", task.getName());
            log.info("========任务负责人:task.getAssignee()========{}", task.getAssignee());
        }
        return tasks;
    }

    @Override
    public void completeTask(String taskId) {
        taskService.complete(taskId);
        // todo 更新businessKey关联的业务信息
    }

    /**
     * 完成任务，判断当前用户是否由权限
     *
     * @param taskId   任务id
     * @param assignee 做任务的用户
     */
    public void completeTask2(String taskId, String assignee) {
        // 完成任务前，需要校验负责人可以完成当前任务
        // 根据任务id 和 任务负责人查询当前任务，如果查到该用户由权限，就完成
        Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(assignee).singleResult();
        Evection evection = new Evection();
        evection.setNum(2D);
        evection.setReason("任务办理时设置变量");
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("evection", evection);
        if (task != null) {
            // 完成任务时，设置流程变量参数
            taskService.complete(taskId, variables);
            log.info("========完成任务========{}", task.getId());
        } else {
            log.info("========{}没有任务，无法完成任务========", assignee);
        }

    }

    @Override
    public Task completeTask(String processDefinitionKey, String assignee) {
        // 根据流程实例 key 和 指派人 获取任务
        Task task = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskAssignee(assignee).singleResult();
        if (task == null) {
            return null;
        }
        log.info("========流程实例ID:task.getProcessInstanceId()========{}", task.getProcessInstanceId());
        log.info("========任务ID:task.getId()========{}", task.getId());
        log.info("========任务名称:task.getName()========{}", task.getName());
        log.info("========任务负责人:task.getAssignee()========{}", task.getAssignee());
        taskService.complete(task.getId());
        return task;
    }

    @Override
    public Task completeTask(String taskId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        /*
        当任务成功执行时调用，所需的任务参数由最终用户给出。
        参数：
        taskId – 要完成的任务的 id，不能为空。
        变量——任务参数。可以为 null 或为空。
         */
        if (task != null) {
            taskService.complete(taskId, variables);
            return task;
        }
        return null;
    }

    @Override
    public Deployment deployBpmn(String bpmnName, String filePathAndName, String pngPath) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);
        Deployment deploy = repositoryService.createDeployment().name(bpmnName).addClasspathResource(filePathAndName).addClasspathResource(pngPath).deploy();
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
            String taskId = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).singleResult().getId();
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
            String taskId = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult().getId();
            log.info("=======taskId========{}", taskId);
            // 自动完成第一个提交
            taskService.complete(taskId);
            return true;
        }
        return false;
    }

    /**
     * 启动流程的过程中，添加businessKey
     * 绑定 BusinessKey
     */
    public void addBusinessKey(String processDefinitionKey, String businessKey) {
        // 第一个参数：流程定义的key  第二个参数：businessKey
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        String businessKeyRes = processInstance.getBusinessKey();
        log.info("========businessKey========{}", businessKeyRes);
    }

    /**
     * 根据定义的流程实例key和assignee获取businessKey
     *
     * @param processDefinitionKey 流程实例key
     * @param assignee             指派人
     */
    public void findProcessInstance(String processDefinitionKey, String assignee) {
        // 查询流程定义的对象
        Task task = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskAssignee(assignee).singleResult();
        // 使用task对象获取实例ID
        String processInstanceId = task.getProcessInstanceId();
        // 使用实例ID, 获取流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 使用processInstance, 得到businessKey
        String businessKey = processInstance.getBusinessKey();
        log.info("========businessKey========{}", businessKey);
    }

    @Override
    public boolean startProcessInstanceWithVariable(StartProcessInstanceVO processInstanceVO) {
        String processDefinitionKey = processInstanceVO.getProcessDefinitionKey();
        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee0", processInstanceVO.getAssignee0());
        variables.put("assignee1", processInstanceVO.getAssignee1());
        variables.put("assignee2", processInstanceVO.getAssignee2());
        variables.put("assignee3", processInstanceVO.getAssignee3());
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        // 打印流程实例信息
        return printProcessInstanceInfo(instance);
    }

    @Override
    public boolean startProcessInstanceWithVariable(StartProcessInstanceDTO startProcessInstanceDTO) {
        String processDefinitionKey = startProcessInstanceDTO.getProcessDefinitionKey();
        HashMap<String, Object> variables = new HashMap<>();
        // 指派的人
        variables.put("assignee0", startProcessInstanceDTO.getAssignee0());
        variables.put("assignee1", startProcessInstanceDTO.getAssignee1());
        variables.put("assignee2", startProcessInstanceDTO.getAssignee2());
        variables.put("assignee3", startProcessInstanceDTO.getAssignee3());
        // 出差天数，不同的出差天数，流程节点不同
        Double num = startProcessInstanceDTO.getNum();
        Evection evection = new Evection();
        evection.setNum(num);
        evection.setReason(startProcessInstanceDTO.getReason());
        variables.put("evection", evection);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        boolean flag = printProcessInstanceInfo(instance);
        return flag;
    }

    @Override
    public boolean startProcessInstanceWithVariable(String processDefinitionKey, Map<String, Object> variables) {
        /*
        开始流程实例，会产生一个流程实例ID，将该流程实例ID进行存储到业务表即可，方便后续的调用
        使用给定键在流程定义的最新版本中启动一个新流程实例。
        可以提供业务键来将流程实例与具有明确业务含义的特定标识符相关联。
        例如，在订单流程中，业务键可以是订单 ID。然后可以使用此业务密钥轻松查找该流程实例，
        processdefinitionKey-businessKey 的组合必须是唯一的。
        参数：
        processDefinitionKey – 流程定义的键，不能为空。
        businessKey – 唯一标识上下文或给定流程定义中的流程实例的键。
        variables - 要传递的变量，可以为 null。
         */
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        // runtimeService.startProcessInstanceByKey(processDefinitionKey,"businessKey",variables);
        return printProcessInstanceInfo(processInstance);
    }

    /**
     * 启动流程实例时，打印流程实例信息
     *
     * @param instance 流程实例
     * @return true or false
     */
    private boolean printProcessInstanceInfo(ProcessInstance instance) {
        if (instance.getId().length() > 0) {
            // 获取流程启动产生的taskId
            String taskId = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).singleResult().getId();
            log.info("=======taskId========{}", taskId);
            log.info("=======instance.getProcessInstanceId()========{}", instance.getProcessInstanceId());
            log.info("=======instance.getProcessDefinitionId()========{}", instance.getProcessDefinitionId());
            Map<String, Object> processVariables = instance.getProcessVariables();
            log.info("=======processVariables========{}", processVariables);
            return true;
        }
        return false;
    }


    /**
     * 单个流程的挂起和激活
     */
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

    /**
     * 多个流程的挂起和激活
     */
    public void suspendAllProcessInstance() {
        // 流程实例挂起-所有
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("a0914").singleResult();
        boolean suspended = processDefinition.isSuspended();
        String definitionId = processDefinition.getId();
        if (suspended) {
            // 如果是挂起，激活操作 流程定义ID 是否激活 激活日期
            repositoryService.activateProcessDefinitionById(definitionId, true, null);
        } else {
            // 如果是激活，挂起操作 流程定义ID 是否挂起 挂起日期
            repositoryService.suspendProcessDefinitionById(definitionId, true, null);
        }
    }

    public void reject() {

    }


}
