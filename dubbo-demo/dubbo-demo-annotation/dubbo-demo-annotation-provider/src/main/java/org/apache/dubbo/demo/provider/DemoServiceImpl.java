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
package org.apache.dubbo.demo.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.demo.bean.ComplicatedReq;
import org.apache.dubbo.demo.bean.ComplicatedResp;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@DubboService
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Override
    public String sayHello(String name) {
        logger.info("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        RpcContext.getContext().getObjectAttachments().entrySet().forEach(e -> logger.info(e.getKey() + "=" + e.getValue()));
        return "Hello " + name + ", response from provider: " + RpcContext.getContext().getLocalAddress();
    }

    @Override
    public String login(String name, String password) {
        System.out.println("name :" + name + "password:" + password);
        return "ok";
    }

    @Override
    public CompletableFuture<String> sayHelloAsync(String name) {
        return null;
    }

    @Override
    public String error(String error) {
        throw new RuntimeException("this is an exception from provider");

    }

    @Override
    public ComplicatedResp queryComplicated(ComplicatedReq complicatedReq) {
        logger.info("请求参数: " + JSONObject.toJSONString(complicatedReq));

        ComplicatedResp complicatedResp = null;
        complicatedResp = new ComplicatedResp();
        List<String> stringList = complicatedReq.getStringList();
        Map<Integer,String> stringMap= complicatedReq.getStringMap();
        stringList.add("000");
        stringMap.put(99,"valuee");
        complicatedResp.setStringList(stringList);
        complicatedResp.setAge(complicatedReq.getAge());
        complicatedResp.setName(complicatedReq.getName());
        complicatedResp.setValue(complicatedReq.getValue());
        complicatedResp.setStringMap(stringMap);
        ComplicatedResp.InnerClassResp innerClassResp= new ComplicatedResp.InnerClassResp();
        innerClassResp.setInnerName("innn");
        innerClassResp.setInnerValue("innervalue");
        complicatedResp.setInnerClassResp(innerClassResp);
        logger.info("响应参数：" + JSONObject.toJSONString(complicatedResp));
        return complicatedResp;
    }

    @Override
    public Locale javaClass(Locale locale) {
        return new Locale("EN", "England");
    }

    @Override
    public Locale javaClasses(String name, String code, Locale locale) {
        return new Locale("EN", "England");
    }

}
