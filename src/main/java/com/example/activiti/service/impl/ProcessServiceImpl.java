package com.example.activiti.service.impl;

import com.example.activiti.entity.HistoryInstInfoDTO;
import com.example.activiti.entity.ProcessDTO;
import com.example.activiti.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : HP
 * @date : 2022/9/16
 */
@Service
@Slf4j
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Override
    public List<ProcessDTO> getProcessDefByKey(String processDefinitionKey) {
        // 获取 processDefinitionQuery 对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.processDefinitionKey(processDefinitionKey)
                // 根据版本号排序
                .orderByProcessDefinitionVersion()
                // 倒序
                .desc()
                // 查询所有内容
                .list();
        List<ProcessDTO> processDTOS = new ArrayList<>(processDefinitionKey.length());
        for (ProcessDefinition definition : processDefinitionList) {
            ProcessDTO processDTO = new ProcessDTO();
            log.info("========definition.getId()======={}", definition.getId());
            log.info("========definition.getName()======={}", definition.getName());
            log.info("========definition.getKey()======={}", definition.getKey());
            log.info("========definition.getVersion()======={}", definition.getVersion());
            log.info("========definition.getDeploymentId()======={}", definition.getDeploymentId());
            processDTO.setId(definition.getId());
            processDTO.setName(definition.getName());
            processDTO.setKey(definition.getKey());
            processDTO.setVersion(definition.getVersion());
            processDTO.setDeploymentId(definition.getDeploymentId());
            processDTOS.add(processDTO);
        }
        return processDTOS;
    }

    @Override
    public void deleteProcessBydeploymentId(String deploymentId) {
        // 级联删除 casecade串联
        repositoryService.deleteDeployment(deploymentId,true);
    }

    @Override
    public void getResource(String processDefinitionKey) {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessDefinitionVersion().desc().list();
        // 获取流程实例
        ProcessDefinition processDefinition = list.get(0);
        log.info("========processDefinition========{}", processDefinition);
        // 流程部署ID
        String deploymentId = processDefinition.getDeploymentId();
        // png 图片资源
        String diagramResourceName = processDefinition.getDiagramResourceName();
        log.info("========diagramResourceName========{}", diagramResourceName);
        // 通过 部署ID 和 文件名字 获取图片的资源
        InputStream pngInputStream = repositoryService.getResourceAsStream(deploymentId, diagramResourceName);

        // xml bpmn20资源
        String resourceName = processDefinition.getResourceName();
        log.info("========resourceName========{}", resourceName);
        InputStream bpmnInputStream = repositoryService.getResourceAsStream(deploymentId, resourceName);

        // 构造 outputStream 流
        File pngFile = new File("D:/tmp/activiti/vacation0916.png");
        File bpmnFile = new File("D:/tmp/activiti/vacation.bpmn20.xml");

        try {
            FileOutputStream pngOutputStream = new FileOutputStream(pngFile);
            FileOutputStream bpmnOutputStream = new FileOutputStream(bpmnFile);

            // 输入输出流转化
            int pngCopy = IOUtils.copy(pngInputStream, pngOutputStream);
            int bpmnCopy = IOUtils.copy(bpmnInputStream, bpmnOutputStream);
            log.info("====pngCopy===={}====bpmnCopy===={}",pngCopy,bpmnCopy);

            // 关闭流
            bpmnOutputStream.close();
            pngOutputStream.close();
            pngInputStream.close();
            bpmnInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<HistoryInstInfoDTO> getHistoryInfo(String processInstanceId) {
        HistoricActivityInstanceQuery historyQuery = historyService.createHistoricActivityInstanceQuery();
        List<HistoricActivityInstance> historyInfos = historyQuery
                // 查询actInst表
                .processInstanceId(processInstanceId)
                // 根据开始事件排序
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        List<HistoryInstInfoDTO> historyInstInfoDTOS = new ArrayList<>(historyInfos.size());
        for (HistoricActivityInstance history : historyInfos) {
            HistoryInstInfoDTO dto = new HistoryInstInfoDTO();
            dto.setActivityId(history.getActivityId());
            dto.setActivityName(history.getActivityName());
            dto.setProcessDefinitionId(history.getProcessDefinitionId());
            dto.setProcessInstanceId(history.getProcessInstanceId());
            dto.setStartTime(history.getStartTime());
            dto.setEndTime(history.getEndTime());
            historyInstInfoDTOS.add(dto);
        }
        return historyInstInfoDTOS;
    }
}
