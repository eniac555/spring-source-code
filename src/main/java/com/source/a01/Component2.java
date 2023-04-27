package com.source.a01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Component2 {

    private static final Logger log = LoggerFactory.getLogger(Component2.class);

    // context.publishEvent(new UserRegisteredEvent(context));//发事件

    @EventListener//监听事件
    public void aaa(UserRegisteredEvent event) {
        log.debug("{}", event);
        log.debug("发送短信");
    }
}
