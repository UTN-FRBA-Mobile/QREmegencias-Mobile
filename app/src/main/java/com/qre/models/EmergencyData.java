package com.qre.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by federico on 22/09/17.
 */
public class EmergencyData {

    private String sex;
    private String bloodType;
    private int age;
    private List<String> allergies = new ArrayList<>();
    private List<String> pathologies = new ArrayList<>();
    private String uuid;
    private String contactName;
    private String contactPhone;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public List<String> getPathologies() {
        return pathologies;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

}