package com.example.activiti.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : HP
 * @date : 2022/9/14
 */
@Data
@ApiModel("TaskRepresentation")
public class TaskRepresentation implements Serializable {

    private static final long serialVersionUID = -3750089861001814571L;

    @ApiModelProperty(name = "id", value = "任务ID")
    private String id;

    @ApiModelProperty(name = "name", value = "任务名称")
    private String name;

    public TaskRepresentation(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
