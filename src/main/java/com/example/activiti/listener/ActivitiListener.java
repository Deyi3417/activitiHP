package com.example.activiti.listener;

import com.example.activiti.common.enums.ActivitiInst;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * Activiti 监听器
 *
 * @author : liudy23
 * @data : 2022/9/18
 */
@Slf4j
public class ActivitiListener implements TaskListener {

    /**
     * 指定流程节点负责人 使用监听器设置节点审批人
     *
     * @param delegateTask 委派任务
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("=============ActivitiListener执行了=============");
        if (ActivitiInst.listener_01.getTaskDefinitionKey().equals(delegateTask.getTaskDefinitionKey())) {
            // 创建OA审批
            delegateTask.setAssignee("紫云");
            System.out.println("紫云");
        } else if (ActivitiInst.listener_02.getTaskDefinitionKey().equals(delegateTask.getTaskDefinitionKey())) {
            // 所长审批
            delegateTask.setAssignee("慕青");
            System.out.println("慕青");
        } else if (ActivitiInst.listener_03.getTaskDefinitionKey().equals(delegateTask.getTaskDefinitionKey())) {
            // 院长审批
            delegateTask.setAssignee("苏韵");
            System.out.println("苏韵");
        }
    }
}
