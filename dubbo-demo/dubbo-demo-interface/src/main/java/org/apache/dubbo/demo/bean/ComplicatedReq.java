package org.apache.dubbo.demo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author ysw
 * @date 2023/3/3 10:43
 */

public class ComplicatedReq implements Serializable {

    private String name;

    private Integer age;

    private String value;

    private List<String> stringList;

    private InnerClass innerClass;

    public static class InnerClass implements Serializable{
        private String innerName;

        private String innerValue;

        public String getInnerName() {
            return innerName;
        }

        public void setInnerName(String innerName) {
            this.innerName = innerName;
        }

        public String getInnerValue() {
            return innerValue;
        }

        public void setInnerValue(String innerValue) {
            this.innerValue = innerValue;
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public InnerClass getInnerClass() {
        return innerClass;
    }

    public void setInnerClass(InnerClass innerClass) {
        this.innerClass = innerClass;
    }
}