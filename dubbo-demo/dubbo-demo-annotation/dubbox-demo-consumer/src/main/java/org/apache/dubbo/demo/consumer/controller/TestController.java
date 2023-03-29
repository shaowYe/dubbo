package org.apache.dubbo.demo.consumer.controller;

import org.apache.dubbo.demo.consumer.comp.DemoServiceComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
