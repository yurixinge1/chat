package com.xinge.chat.model;

/**
 * Created by xinge on 2017/9/24.
 */

public class User {
    private int id;
    private String name;
    private String password;
    private int age;
    private String picBase64;  // 头像图片转换成Bas64。弃用。
    private String pic_local_path;
    private String pic_url_path;
    private String signature;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getPicBase64() { return picBase64; }
    public void setPicBase64(String pic) { this.picBase64 = pic; }

    public String getSignature() { return signature; }
    public void setSignature(String sign) { this.signature= sign; }

    public String getPicLocalPath() { return pic_local_path; }
    public void setPicLocalPath(String localPic) { this.pic_local_path = localPic; }

    public String getPicUrlPath() { return pic_url_path; }
    public void setPicUrlPath(String urlPic) { this.pic_url_path = urlPic; }

    @Override
    public String toString() {
        return "User={" + "\"id\": " + id + ", \"name\": " + name + ", \"password\": " + password + ", \"age\": " + age +"}";
    }
}
