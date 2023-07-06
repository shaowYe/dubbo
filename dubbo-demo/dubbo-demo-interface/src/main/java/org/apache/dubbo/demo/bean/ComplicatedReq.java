package org.apache.dubbo.demo.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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


    private Map<Integer, String> stringMap;

    public ComplicatedReq() {

    }

    public ComplicatedReq(String name, Integer age, String value, List<String> stringList, InnerClass innerClass, Map<Integer, String> stringMap) {
        this.name = name;
        this.age = age;
        this.value = value;
        this.stringList = stringList;
        this.innerClass = innerClass;
        this.stringMap = stringMap;
    }

    public static class InnerClass implements Serializable {
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

    public Map<Integer, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<Integer, String> stringMap) {
        this.stringMap = stringMap;
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
