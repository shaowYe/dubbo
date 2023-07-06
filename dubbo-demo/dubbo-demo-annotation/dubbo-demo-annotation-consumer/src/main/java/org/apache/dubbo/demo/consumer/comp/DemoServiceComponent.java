/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.demo.consumer.comp;

import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.demo.GreetingService;
import org.apache.dubbo.demo.bean.ComplicatedReq;
import org.apache.dubbo.demo.bean.ComplicatedResp;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@Component("demoServiceComponent")
public class DemoServiceComponent {
    @DubboReference(check = false)
    private DemoService demoService;

    @DubboReference(check = false)
    private GreetingService greetingService;

    public String sayHello(String name) {
        RpcContext.getContext().setAttachment("okr", "uyun-okr-sayhello");
        return demoService.sayHello(name);
    }


    public String login(String name, String password) {
        return demoService.login(name, password);

    }

    public String greet() {
        RpcContext.getContext().setAttachment("okr", "uyun-okr-greet");
        return greetingService.hello();
    }

    public String classes() {
        Locale locale = demoService.javaClass(new Locale("ru", "russian"));
        Locale locale2 = demoService.javaClasses("张三", "coda2", new Locale("ru", "russian"));
        return locale.getCountry() + ":" + locale2.getCountry();
    }

    public String com() {
        ComplicatedReq complicatedReq = new ComplicatedReq();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("first");
        complicatedReq.setStringList(strings);
        complicatedReq.setName("name");
        complicatedReq.setAge(10);
        complicatedReq.setValue("value");
        HashMap<Integer, String> stringHashMap = new HashMap<>();
        stringHashMap.put(1, "1name");
        complicatedReq.setStringMap(stringHashMap);
        ComplicatedReq.InnerClass innerClass = new ComplicatedReq.InnerClass();
        innerClass.setInnerName("innname");
        innerClass.setInnerValue("invalue");
        complicatedReq.setInnerClass(innerClass);
        ComplicatedResp complicatedResp = demoService.queryComplicated(complicatedReq);
        return JSONObject.toJSONString(complicatedResp);

    }

}
