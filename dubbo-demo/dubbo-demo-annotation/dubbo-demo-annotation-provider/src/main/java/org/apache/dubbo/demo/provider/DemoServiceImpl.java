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
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.demo.bean.ComplicatedReq;
import org.apache.dubbo.demo.bean.ComplicatedResp;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
    public CompletableFuture<String> sayHelloAsync(String name) {
        return null;
    }

    @Override
    public String error(String error) {
        throw new RuntimeException("this is an exception from provider");

    }

    @Override
    public ComplicatedResp queryComplicated(ComplicatedReq complicatedReq) {
        logger.info("请求参数: " + JSON.toJSONString(complicatedReq));

        ComplicatedResp complicatedResp = new ComplicatedResp();
        complicatedResp.setName(complicatedReq.getName());
        complicatedResp.setAge(complicatedReq.getAge() + 1);
        complicatedResp.setValue(complicatedReq.getValue());
        ComplicatedResp.InnerClassResp classResp = new ComplicatedResp.InnerClassResp();
        classResp.setInnerValue(complicatedReq.getInnerClass().getInnerValue());
        classResp.setInnerName(complicatedReq.getInnerClass().getInnerName());
        complicatedResp.setInnerClassResp(classResp);
        List<String> stringList = complicatedReq.getStringList();
        stringList.add("000");
        complicatedResp.setStringList(stringList);
        return complicatedResp;
    }

}
