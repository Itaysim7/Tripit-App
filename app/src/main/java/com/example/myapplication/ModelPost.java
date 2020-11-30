package com.example.myapplication;

public class ModelPost
{
    String dep_date;
    String ret_date;
    String dest;
    String gender;
    String age;
    String type;
    String description;
    String post_date;
    //constructor
    public ModelPost()
    {

    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setDep_date(String dep_date) {
        this.dep_date = dep_date;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRet_date(String ret_date) {
        this.ret_date = ret_date;
    }

    public String getAge() {
        return age;
    }

    public String getDep_date() {
        return dep_date;
    }

    public String getDescription() {
        return description;
    }

    public String getDest() {
        return dest;
    }

    public String getGender() {
        return gender;
    }

    public String getPost_date() {
        return post_date;
    }

    public String getRet_date() {
        return ret_date;
    }

    public String getType() {
        return type;
    }
}

