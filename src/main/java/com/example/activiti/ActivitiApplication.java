package com.example.activiti;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author HP
 * @date 2022/11/04
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@MapperScan("com.example.activiti.mapper")
public class ActivitiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivitiApplication.class, args);
		System.out.println("========start success========");
	}

}
