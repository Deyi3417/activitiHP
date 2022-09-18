package com.example.activiti.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 启动流程实例vo
 * @author : liudy23
 * @data : 2022/9/18
 */
@Data
@ApiModel("StartProcessInstanceVO")
public class StartProcessInstanceVO implements Serializable {

    private static final long serialVersionUID = 6638990989473028169L;

    @ApiModelProperty(name = "processDefinitionKey", value = "流程定义key")
    private String processDefinitionKey;

    @ApiModelProperty(name = "assignee0", value = "创建出差申请")
    private String assignee0;

    @ApiModelProperty(name = "assignee1", value = "经理审批")
    private String assignee1;

    @ApiModelProperty(name = "assignee2", value = "总经理审批")
    private String assignee2;

    @ApiModelProperty(name = "assignee3", value = "财务审批")
    private String assignee3;
}
