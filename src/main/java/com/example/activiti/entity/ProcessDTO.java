package com.example.activiti.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : HP
 * @date : 2022/9/16
 */
@Data
@ApiModel(value = "ProcessDTO")
public class ProcessDTO implements Serializable {

    private static final long serialVersionUID = -3594378425049473136L;

    @ApiModelProperty(name = "id", value = "定义的实例ID")
    private String id;

    @ApiModelProperty(name = "name", value = "定义的实例名称")
    private String name;

    @ApiModelProperty(name = "key", value = "定义的实例key")
    private String key;

    @ApiModelProperty(name = "version", value = "定义的实例版本号")
    private int version;

    @ApiModelProperty(name = "deploymentId", value = "定义的实例部署ID")
    private String deploymentId;
}
