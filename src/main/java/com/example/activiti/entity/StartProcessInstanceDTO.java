package com.example.activiti.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : HP
 * @date : 2022/9/19
 */
@Data
@ApiModel("StartProcessInstanceDTO")
public class StartProcessInstanceDTO implements Serializable {

    private static final long serialVersionUID = -3345484268879159150L;

    @ApiModelProperty(name = "num", value = "出差天数")
    private Double num;

    @ApiModelProperty(name = "reason", value = "出差原因")
    private String reason;

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
