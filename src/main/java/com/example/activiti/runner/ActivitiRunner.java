package com.example.activiti.runner;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.test.ActivitiRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author : HP
 * @date : 2022/9/14
 */
@Component
@Slf4j
public class ActivitiRunner implements CommandLineRunner {

    @Resource
    private TaskService taskService;

    @Resource
    private RepositoryService repositoryService;



    @Override
    public void run(String... args) throws Exception {

        log.info("========Starting CommandLineRunner========");
        System.out.println("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
        System.out.println("Number of tasks: " + taskService.createTaskQuery().count());
        System.out.println("Number of tasks after process start: " + taskService.createTaskQuery().count());
        log.info("========End CommandLineRunner========");

    }
}
