package com.example.activiti.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建流程实例带的POJO类
 *
 * @author : HP
 * @date : 2022/9/19
 */
@Data
@ApiModel("流程实例POJO类")
public class Evection implements Serializable {

    private static final long serialVersionUID = 7470632021149384380L;

    @ApiModelProperty(name = "id", value = "id")
    private Long id;

    @ApiModelProperty(name = "num", value = "出差天数")
    private Double num;

    @ApiModelProperty(name = "evectionName", value = "出差单名称")
    private String evectionName;

    @ApiModelProperty(name = "beginDate", value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginDate;

    @ApiModelProperty(name = "endDate", value = "结束日期 ")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(name = "destination", value = "目的地")
    private String destination;

    @ApiModelProperty(name = "reason", value = "出差原因")
    private String reason;

}
