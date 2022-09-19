package com.example.activiti.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务交接
 * @author : HP
 * @date : 2022/9/19
 */
@Data
@ApiModel("任务交接：TaskHandoverVO")
public class TaskHandoverVO implements Serializable {

    private static final long serialVersionUID = -8056636289007127812L;

    @ApiModelProperty(name = "taskId", value = "任务ID")
    private String taskId;

    @ApiModelProperty(name = "assignee", value = "做任务的指派人")
    private String assignee;

    @ApiModelProperty(name = "candidateUser", value = "候选人，交接人")
    private String candidateUser;
}
