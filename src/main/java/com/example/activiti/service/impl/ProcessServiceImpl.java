package com.example.activiti.service.impl;

import com.example.activiti.config.ICustomProcessDiagramGenerator;
import com.example.activiti.config.WorkflowConstants;
import com.example.activiti.entity.HistoryInstInfoDTO;
import com.example.activiti.entity.ProcessDTO;
import com.example.activiti.entity.TaskRepresentation;
import com.example.activiti.service.ProcessService;
import com.example.activiti.util.JumpAnyWhereCmd;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cmd.DeleteTaskCmd;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private ProcessEngine processEngine;

    @Resource
    private IdentityService identityService;

    @Resource
    private ManagementService managementService;

    @Override
    public List<ProcessDTO> getProcessDefByKey(String processDefinitionKey) {
        // ?????? processDefinitionQuery ??????
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.processDefinitionKey(processDefinitionKey)
                // ?????????????????????
                .orderByProcessDefinitionVersion()
                // ??????
                .desc()
                // ??????????????????
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
        // ???????????? casecade??????
        repositoryService.deleteDeployment(deploymentId,true);
    }

    @Override
    public void getResource(String processDefinitionKey) {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessDefinitionVersion().desc().list();
        // ??????????????????
        ProcessDefinition processDefinition = list.get(0);
        log.info("========processDefinition========{}", processDefinition);
        // ????????????ID
        String deploymentId = processDefinition.getDeploymentId();
        // png ????????????
        String diagramResourceName = processDefinition.getDiagramResourceName();
        log.info("========diagramResourceName========{}", diagramResourceName);
        // ?????? ??????ID ??? ???????????? ?????????????????????
        InputStream pngInputStream = repositoryService.getResourceAsStream(deploymentId, diagramResourceName);

        // xml bpmn20??????
        String resourceName = processDefinition.getResourceName();
        log.info("========resourceName========{}", resourceName);
        InputStream bpmnInputStream = repositoryService.getResourceAsStream(deploymentId, resourceName);

        // ?????? outputStream ???
        File pngFile = new File("D:/tmp/activiti/vacation0916.png");
        File bpmnFile = new File("D:/tmp/activiti/vacation.bpmn20.xml");

        try {
            FileOutputStream pngOutputStream = new FileOutputStream(pngFile);
            FileOutputStream bpmnOutputStream = new FileOutputStream(bpmnFile);

            // ?????????????????????
            int pngCopy = IOUtils.copy(pngInputStream, pngOutputStream);
            int bpmnCopy = IOUtils.copy(bpmnInputStream, bpmnOutputStream);
            log.info("====pngCopy===={}====bpmnCopy===={}",pngCopy,bpmnCopy);

            // ?????????
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
                // ??????actInst???
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                // ????????????????????????
                .orderByHistoricActivityInstanceStartTime().desc()
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

    @Override
    public List<TaskRepresentation> findGroupTask(String processDefinitionKey, String candidateUserName) {
        // ???????????????
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskCandidateUser(candidateUserName)
                .list();
        List<TaskRepresentation> resultTasks = new ArrayList<>(taskList.size());
        for (Task task : taskList) {
            resultTasks.add(new TaskRepresentation(task.getId(),task.getName()));
        }
        log.info("=========resultTasks========={}", resultTasks);
        return resultTasks;
    }

    @Override
    public boolean claimTask(String taskId, String candidateUserName) {
        // ????????????
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                // ????????? ?????????
                .taskCandidateUser(candidateUserName)
                .singleResult();
        if (task != null) {
            // ????????????
            taskService.claim(taskId, candidateUserName);
            log.info("taskId==={}  ?????????==={}",taskId, candidateUserName);
            return true;
        }
        return false;
    }

    @Override
    public boolean claimTask02(String taskId, String candidateUserName) {
        Task task = taskService.createTaskQuery()
                .processInstanceId("7501")
                .taskId(taskId).singleResult();
        if (task != null) {
            log.info("========claimTask02========7501====taskId:{}, taskName:{}",taskId, task.getName());
            taskService.claim(taskId, candidateUserName);
            return true;
        }
        return false;
    }

    @Override
    public boolean returnTask(String taskId, String assignee) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                // ??????????????? ?????????
                .taskAssignee(assignee)
                .singleResult();
        if (task != null) {
            taskService.setAssignee(taskId,null);
            log.info("{}==taskId  ???????????????====", taskId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean taskHandover(String taskId, String assignee, String candidateUser, String comment) {
        // ??????taskId???assignee??????????????????
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(assignee)
                .singleResult();
        String processInstanceId = task.getProcessInstanceId();
        if (task != null) {
            // ????????????????????????????????????????????? userId????????????????????????????????????????????????
            identityService.setAuthenticatedUserId(assignee);
            taskService.addComment(taskId, task.getProcessInstanceId(), comment);

            managementService.executeCommand(new JumpAnyWhereCmd(task.getId(), task.getTaskDefinitionKey(),"doTransfer"));
            Task newTask = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
            if (newTask == null) {
                throw new RuntimeException("newTask??????");
            }
            taskService.setAssignee(newTask.getId(), candidateUser);
            log.info("{}====??????????????????", taskId);
            return true;
        }
        return false;
    }

    @Override
    public void readResource(String pProcessInstanceId, HttpServletResponse response) throws Exception{
        // ?????????????????????
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        String processDefinitionId = "";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(pProcessInstanceId).singleResult();
        if(processInstance == null) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(pProcessInstanceId).singleResult();
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        } else {
            processDefinitionId = processInstance.getProcessDefinitionId();
        }
        ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition pd = pdq.processDefinitionId(processDefinitionId).singleResult();

        String resourceName = pd.getDiagramResourceName();

        if(resourceName.endsWith(".png") && StringUtils.isEmpty(pProcessInstanceId) == false)
        {
            getActivitiProccessImage(pProcessInstanceId,response);
            //ProcessDiagramGenerator.generateDiagram(pde, "png", getRuntimeService().getActiveActivityIds(processInstanceId));
        }
        else
        {
            // ??????????????????
            InputStream resourceAsStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), resourceName);

            // ?????????????????????????????????
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        }
    }


    /**
     * ????????????????????????????????????????????????????????????
     */
    public void getActivitiProccessImage(String pProcessInstanceId, HttpServletResponse response) {
        //logger.info("[??????]-?????????????????????");
        try {
            //  ????????????????????????
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(pProcessInstanceId).singleResult();

            if (historicProcessInstance == null) {
                //throw new BusinessException("??????????????????ID[" + pProcessInstanceId + "]????????????????????????????????????");
            }
            else {
                // ??????????????????
                ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                        .getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

                // ??????????????????????????????????????????????????????????????????????????????????????????
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(pProcessInstanceId).orderByHistoricActivityInstanceId().asc().list();

                // ??????????????????ID??????
                List<String> executedActivityIdList = new ArrayList<String>();
                int index = 1;
                //logger.info("???????????????????????????ID");
                for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                    executedActivityIdList.add(activityInstance.getActivityId());

                    //logger.info("???[" + index + "]??????????????????=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
                    index++;
                }

                BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

                // ?????????????????????
                List<String> flowIds = new ArrayList<String>();
                // ???????????????????????? (getHighLightedFlows??????????????????)
                flowIds = getHighLightedFlows(bpmnModel,processDefinition, historicActivityInstanceList);

//                // ??????????????????????????????
//                ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
//                //????????????
//                InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds,"??????","????????????","??????",null,2.0);

                Set<String> currIds = runtimeService.createExecutionQuery().processInstanceId(pProcessInstanceId).list()
                        .stream().map(e->e.getActivityId()).collect(Collectors.toSet());

                ICustomProcessDiagramGenerator diagramGenerator = (ICustomProcessDiagramGenerator) processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
                InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", executedActivityIdList,
                        flowIds, "??????", "??????", "??????", null, 1.0, new Color[] { WorkflowConstants.COLOR_NORMAL, WorkflowConstants.COLOR_CURRENT }, currIds);

                response.setContentType("image/png");
                OutputStream os = response.getOutputStream();
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                imageStream.close();
            }
            //logger.info("[??????]-?????????????????????");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //logger.error("????????????-????????????????????????" + e.getMessage());
            //throw new BusinessException("????????????????????????" + e.getMessage());
        }
    }


    private List<String> getHighLightedFlows(BpmnModel bpmnModel,ProcessDefinitionEntity processDefinitionEntity,List<HistoricActivityInstance> historicActivityInstances) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //24?????????
        List<String> highFlows = new ArrayList<String>();// ????????????????????????flowId

        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            // ?????????????????????????????????
            // ?????????????????????????????????
            FlowNode activityImpl = (FlowNode)bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(i).getActivityId());


            List<FlowNode> sameStartTimeNodes = new ArrayList<FlowNode>();// ?????????????????????????????????????????????
            FlowNode sameActivityImpl1 = null;

            HistoricActivityInstance activityImpl_ = historicActivityInstances.get(i);// ???????????????
            HistoricActivityInstance activityImp2_ ;

            for(int k = i + 1 ; k <= historicActivityInstances.size() - 1; k++) {
                activityImp2_ = historicActivityInstances.get(k);// ?????????1?????????

                if ( activityImpl_.getActivityType().equals("userTask") && activityImp2_.getActivityType().equals("userTask") &&
                        df.format(activityImpl_.getStartTime()).equals(df.format(activityImp2_.getStartTime()))   ) //??????usertask???????????????????????????????????????????????????????????????????????????????????????
                {

                }
                else {
                    sameActivityImpl1 = (FlowNode)bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(k).getActivityId());//????????????????????????????????????
                    break;
                }

            }
            sameStartTimeNodes.add(sameActivityImpl1); // ????????????????????????????????????????????????????????????
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// ?????????????????????
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// ?????????????????????

                if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))  )
                {// ???????????????????????????????????????????????????????????????
                    FlowNode sameActivityImpl2 = (FlowNode)bpmnModel.getMainProcess().getFlowElement(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                }
                else
                {// ????????????????????????
                    break;
                }
            }
            List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows() ; // ?????????????????????????????????

            for (SequenceFlow pvmTransition : pvmTransitions)
            {// ???????????????????????????
                FlowNode pvmActivityImpl = (FlowNode)bpmnModel.getMainProcess().getFlowElement( pvmTransition.getTargetRef());// ?????????????????????????????????????????????????????????????????????????????????id?????????????????????
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }

        }
        return highFlows;

    }
}
