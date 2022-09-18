package com.example.activiti.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : HP
 * @date : 2022/9/14
 */
@Data
public class TaskRepresentation implements Serializable {

    private static final long serialVersionUID = -3750089861001814571L;

    private String id;

    private String name;

    public TaskRepresentation(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
