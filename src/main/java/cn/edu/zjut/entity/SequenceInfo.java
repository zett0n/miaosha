package cn.edu.zjut.entity;

import lombok.Data;

@Data
public class SequenceInfo {
    private String name;

    private Integer currentValue;

    private Integer step;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}