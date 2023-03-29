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

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.demo.GreetingService;
import org.springframework.stereotype.Component;
import uyun.pacific.api.query.QueryBuilder;
import uyun.pacific.api.query.QueryOperator;
import uyun.pacific.api.query.QueryParams;
import uyun.pacific.api.query.QueryResult;
import uyun.pacific.resource.api.entity.object.ResObject;
import uyun.pacific.resource.api.service.ResObjectService;

import java.util.Locale;

@Component("demoServiceComponent")
public class DemoServiceComponent {
    @Reference(check = false)
    private DemoService demoService;

    @Reference(check = false)
    private GreetingService greetingService;


    public String sayHello(String name) {
        RpcContext.getContext().setAttachment("okr", "uyun-okr-sayhello");
        return demoService.sayHello(name);
    }

    public String greet() {
        RpcContext.getContext().setAttachment("okr", "uyun-okr-greet");
        return greetingService.hello();
    }

    public String error(String error){
        RpcContext.getContext().setAttachment("okr", "uyun-okr-error");
        return demoService.error(error);
    }

    public String javaClass(){

        Locale locale = demoService.javaClass(new Locale("jp", "Japan"));
        Locale locale1 = demoService.javaClasses("李二", "cc", new Locale("kr", "koran"));
        return locale1.getCountry();

    }



}
