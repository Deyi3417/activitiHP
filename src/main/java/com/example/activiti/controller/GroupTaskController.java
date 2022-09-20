package com.example.activiti.controller;

import com.example.activiti.entity.TaskRepresentation;
import com.example.activiti.entity.vo.TaskHandoverVO;
import com.example.activiti.service.ProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组任务
 *
 * @author : HP
 * @date : 2022/9/19
 */
@RestController
@RequestMapping("/grouptask")
@Api(tags = "GroupTaskController")
public class GroupTaskController {

    @Autowired
    private ProcessService processService;

    @PostMapping("/obtain")
    @ApiOperation("获取组任务")
    public Object findGroupTaskList(@RequestParam String processDefinitionKey, @RequestParam String candidateUserName) {
        List<TaskRepresentation> taskList = processService.findGroupTask(processDefinitionKey, candidateUserName);
        return taskList;
    }

    @PostMapping("/claimTask")
    @ApiOperation("拾取任务 参数1：taskId 参数2：candidateUserName")
    public Object claimTask(@RequestParam String taskId, @RequestParam String candidateUserName) {
        boolean flag = processService.claimTask(taskId, candidateUserName);
        return flag;
    }

    @PostMapping("/claimTask02")
    @ApiOperation("拾取任务 参数1：taskId 参数2：candidateUserName")
    public Object claimTask02(@RequestParam String taskId, @RequestParam String candidateUserName) {
        boolean flag = processService.claimTask02(taskId, candidateUserName);
        return flag;
    }

    @PostMapping("/returnTask")
    @ApiOperation("归还任务 参数1：taskId 参数2：assignee")
    public Object returnTask(@RequestParam String taskId, @RequestParam String assignee) {
        boolean flag = processService.returnTask(taskId, assignee);
        return flag;
    }

    @PostMapping("/taskHandover")
    @ApiOperation("归还任务 参数: taskHandoverVO")
    public Object taskHandover(@RequestBody TaskHandoverVO taskHandoverVO) {
        String taskId = taskHandoverVO.getTaskId();
        String assignee = taskHandoverVO.getAssignee();
        String candidateUser = taskHandoverVO.getCandidateUser();
        boolean flag = processService.taskHandover(taskId, assignee, candidateUser);
        return flag;
    }
}
