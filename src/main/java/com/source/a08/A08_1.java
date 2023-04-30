package com.source.a08;

import com.source.a08.sub.E;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/*
    如果 jdk > 8, 运行时请添加 --add-opens java.base/java.lang=ALL-UNNAMED
 */
@ComponentScan("com.source.a08.sub")
public class A08_1 {

    private static final Logger log = LoggerFactory.getLogger(A08_1.class);

    public static void main(String[] args) {
        //验证在单例bean上注入不同scope的bean时，新注入的bean的scope会失效，并提出四种解决办法

        //解决方法虽然不同，但理念上殊途同归，都是延迟其他scope的bean的获取
        //即运行时才会获取其他scope类型的bean

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(A08_1.class);

        //通过在被注入（E）的成员变量上添加@Lazy注解解决scope失效
        E e = context.getBean(E.class);
        log.debug("{}", e.getF1().getClass());
        log.debug("{}", e.getF1());
        log.debug("{}", e.getF1());
        log.debug("{}", e.getF1());

        //通过添加Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
        //一个代理配置解决，加在需要注入的bean类名（F2）上
        log.debug("{}", e.getF2().getClass());
        log.debug("{}", e.getF2());
        log.debug("{}", e.getF2());
        log.debug("{}", e.getF2());

        //通过注入对象工厂  ObjectFactory<F3> f3  + 调用方法获取对象 f3.getObject()
        log.debug("{}", e.getF3());
        log.debug("{}", e.getF3());

        //通过注入容器  ApplicationContext context  +  调用方法获取对象 context.getBean()
        log.debug("{}", e.getF4());
        log.debug("{}", e.getF4());

        context.close();
        /*
            学到了什么
                a. 单例注入其它 scope 的四种解决方法
                b. 解决方法虽然不同, 但理念上殊途同归: 都是推迟其它 scope bean 的获取
         */
    }
}
