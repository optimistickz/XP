package com.tongji.exam.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * created by kz on
 */
@Data
@Entity
public class Record {
    @Id
    private Integer traceid;
}
