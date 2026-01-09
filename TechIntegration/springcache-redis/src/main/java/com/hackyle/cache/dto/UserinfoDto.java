package com.hackyle.cache.dto;

public class UserinfoDto {
    private long uid;
    private String name;
    private String gender;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return  "uid=" + uid +
                ", name='" + name +
                ", gender='" + gender;
    }
}
