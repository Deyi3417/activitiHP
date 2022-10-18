package com.example.activiti.controller;

import com.example.activiti.entity.HistoryInstInfoDTO;
import com.example.activiti.entity.ProcessDTO;
import com.example.activiti.service.ProcessService;
import io.swagger.annotations.Api;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author : HP
 * @date : 2022/9/16
 */
@RestController
@RequestMapping("/process")
@Api(tags = "ProcessController")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @GetMapping("/list")
    public List<ProcessDTO> getProcess(@RequestParam String processDefinitionKey) {
        List<ProcessDTO> processDTOS = processService.getProcessDefByKey(processDefinitionKey);
        return processDTOS;
    }

    @DeleteMapping("/delete/{deploymentId}")
    public Object delete(@PathVariable(value = "deploymentId") String deploymentId) {
        processService.deleteProcessBydeploymentId(deploymentId);
        return true;
    }

    @GetMapping("/resource")
    public Object getResource(@RequestParam String processDefinitionKey) {
        processService.getResource(processDefinitionKey);
        return true;
    }

    @GetMapping("/history/{processInstanceId}")
    public List<HistoryInstInfoDTO> getHistoryInfo(@PathVariable(value = "processInstanceId") String processInstanceId) {
        List<HistoryInstInfoDTO> historyInfo = processService.getHistoryInfo(processInstanceId);
        return historyInfo;
    }

    @GetMapping("read-resource")
    public void readResource(String processInstanceId, HttpServletResponse response) {
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        String processDefinitionId = "";


    }

}
