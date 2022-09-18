package com.example.activiti.controller;

import com.example.activiti.entity.TaskRepresentation;
import com.example.activiti.service.ActivitiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : HP
 * @date : 2022/9/14
 */
@RestController
@Slf4j
@Api(tags = "ActivitiController")
public class ActivitiController {

    @Autowired
    private ActivitiService activitiService;

    @PostMapping("/deploy")
    @ApiOperation("部署时无png")
    public boolean deploy(@RequestParam String bpmnName, @RequestParam String filePathAndName) {
        Deployment deployment = activitiService.deployBpmn(bpmnName, filePathAndName);
        log.info("deployment:" + deployment);
        if (StringUtils.isBlank(deployment.getId())) {
            return false;
        }
        return true;
    }

    @PostMapping("/deploy02")
    @ApiOperation("部署时包含png")
    public boolean deploy(@RequestParam String bpmnName, @RequestParam String filePathAndName, @RequestParam String pngPath) {
        Deployment deployment = activitiService.deployBpmn(bpmnName, filePathAndName, pngPath);
        log.info("deployment:" + deployment);
        if (StringUtils.isBlank(deployment.getId())) {
            return false;
        }
        return true;
    }

    /**
     * 开始流程-传递holiday.bpmn20.xml 中的id a=0914
     *
     * @param workFlowName
     * @return
     */
    @PostMapping("startHoliday")
    public Object startHoliday(@RequestParam(value = "workFlowName") String workFlowName) {
        boolean flag = activitiService.startHoliday(workFlowName);
        return flag;
    }

    @PostMapping("/start")
    @ApiOperation("启动流程实例")
    public Object startFlowWork(@RequestParam(value = "processDefinitionKey") String processDefinitionKey) {
        boolean flag = activitiService.startFlowWork(processDefinitionKey);
        return flag;
    }

    @GetMapping("/tasks/{assignee}")
    @ApiOperation("获取个人任务")
    public Object getPersonalTasks(@PathVariable(value = "assignee") String assignee) {
        List<Task> tasks = activitiService.getTasks(assignee);
        log.info("========获取个人任务：tasks========{}", tasks);
        ArrayList<TaskRepresentation> resultTasks = new ArrayList<>();
        for (Task task : tasks) {
            resultTasks.add(new TaskRepresentation(task.getId(), task.getName()));
        }
        return resultTasks;
    }

    @GetMapping("/tasks")
    @ApiOperation("获取个人任务-assignee-定义的流程实例processDefinitionKey")
    public List<TaskRepresentation> getPersonalTasksByKeyAndAssignee(@RequestParam String assignee, @RequestParam String processDefinitionKey) {
        List<Task> tasks = activitiService.getTasks(processDefinitionKey, assignee);
        log.info("========获取个人任务：tasks========{}", tasks);
        ArrayList<TaskRepresentation> resultTasks = new ArrayList<>();
        for (Task task : tasks) {
            resultTasks.add(new TaskRepresentation(task.getId(), task.getName()));
        }
        return resultTasks;
    }

    @GetMapping("/complete/{taskId}")
    @ApiOperation("完成个人任务")
    public Object completeTasks(@PathVariable(value = "taskId") String taskId) {
        activitiService.completeTask(taskId);
        return true;
    }

    @GetMapping("/complete")
    @ApiOperation("完成个人任务优化-定义的流程实例processDefinitionKey--assignee")
    public Object completeTasksByKeyAndAssignee(@RequestParam String assignee, @RequestParam String processDefinitionKey) {
        // 返回完成的任务
        Task task = activitiService.completeTask(processDefinitionKey, assignee);
        if (task == null) {
            return "该流程实例下用户"+assignee+"当前无任务";
        }
        TaskRepresentation taskRepresentation = new TaskRepresentation(task.getId(), task.getName());
        return taskRepresentation;
    }


    @PostMapping("/process")
    @ApiOperation("process")
    public void startProcessInstance() {
        activitiService.startProcess();
    }

    @GetMapping("/test")
    @ApiOperation("测试")
    public String testAction() {
        return "I am OK";
    }


}
