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

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import org.apache.dubbo.demo.DemoService;

import org.apache.dubbo.demo.bean.ComplicatedReq;
import org.apache.dubbo.demo.bean.ComplicatedResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Override
    public String sayHello(String name) {
        logger.info("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        RpcContext.getContext().getAttachments().entrySet().forEach(e -> logger.info(e.getKey() + "=" + e.getValue()));
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
        return null;
    }

    @Override
    public Locale javaClass(Locale locale) {
        return new Locale("EN","England");
    }

    @Override
    public Locale javaClasses(String name, String code, Locale locale) {
        return new Locale("EN2","England2");
    }
}
