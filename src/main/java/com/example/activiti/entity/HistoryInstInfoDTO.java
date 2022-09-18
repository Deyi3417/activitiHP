package com.example.activiti.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 刘德意
 * @Date 2022/09/18
 */
@Data
@ApiModel("处理流程实例历史信息DTO")
public class HistoryInstInfoDTO implements Serializable {

    private static final long serialVersionUID = -1191637438906564670L;

    @ApiModelProperty(name = "activityId", value = "节点ID")
    private String activityId;

    @ApiModelProperty(name = "activityName", value = "节点name")
    private String activityName;

    @ApiModelProperty(name = "processDefinitionId", value = "定义的流程ID")
    private String processDefinitionId;

    @ApiModelProperty(name = "processInstanceId", value = "流程实例ID")
    private String processInstanceId;

    @ApiModelProperty(name = "startTime", value = "流程节点开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(name = "endTime", value = "流程节点结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;;

}
