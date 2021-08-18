package cn.edu.zjut.entity;

import lombok.Data;

@Data
public class UserPasswordInfo {
    private Integer id;

    private String encryptPassword;

    private Integer userId;

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword == null ? null : encryptPassword.trim();
    }
}