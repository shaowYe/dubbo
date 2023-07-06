package org.apache.dubbo.demo.consumer.controller;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.demo.bean.ComplicatedReq;
import org.apache.dubbo.demo.bean.ComplicatedResp;
import org.apache.dubbo.demo.consumer.comp.DemoServiceComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class TestController {

    @Autowired
    private DemoServiceComponent demoServiceComponent;

    @GetMapping("/test")
    @ResponseBody
    public String callPaaSDubbo() {
        String r1 = demoServiceComponent.sayHello("wangyl");
        String r2 = demoServiceComponent.greet();
        return r1 + r2;
    }

    @GetMapping("/excetion")
    @ResponseBody
    public String callError() {
        String em = "";
        try {
            demoServiceComponent.error("rrrer");
        } catch (Exception e) {
            em = e.getMessage();
            e.printStackTrace();
        }
        return em;
    }


    @GetMapping("/class")
    @ResponseBody
    public String javaClass() {

        return demoServiceComponent.javaClass();

    }
    @GetMapping("/com")
    @ResponseBody
    public String com() {
        ComplicatedReq complicatedReq = new ComplicatedReq();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("first");
        complicatedReq.setStringList(strings);
        complicatedReq.setName("name");
        complicatedReq.setAge(10);
        complicatedReq.setValue("value");
        HashMap<Integer, String> stringHashMap = new HashMap<>();
        stringHashMap.put(1,"1name");
        complicatedReq.setStringMap(stringHashMap);
        ComplicatedReq.InnerClass  innerClass = new ComplicatedReq.InnerClass();
        innerClass.setInnerName("innname");
        innerClass.setInnerValue("invalue");
        complicatedReq.setInnerClass(innerClass);
        ComplicatedResp com = demoServiceComponent.com(complicatedReq);
        return JSON.toJSONString(com);

    }

}
