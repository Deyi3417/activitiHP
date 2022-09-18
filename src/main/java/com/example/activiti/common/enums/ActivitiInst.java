package com.example.activiti.common.enums;

/**
 * 定义流程
 * 
 * @author liudy23
 * @data 2022/09/18
 */
public enum ActivitiInst
{
    listener_01("lis_1", "创建OA审批"),
    listener_02("lis_2", "所长审批"),
    listener_03("lis_3", "院长审批");

    private final String taskDefinitionKey;
    private final String name;

    ActivitiInst(String taskDefinitionKey, String name) {
        this.taskDefinitionKey = taskDefinitionKey;
        this.name = name;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public String getName() {
        return name;
    }
}
