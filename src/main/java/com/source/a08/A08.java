package com.source.a08;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
    singleton, prototype, request, session, application

    一个单例bean，注入其他scope的bean，会出现scope失效问题，解决方式是加@Lazy注解
    原因：对于单例对象来讲，依赖注入仅发生了一次，后续再没有用到多例的 F，因此 E 用的始终是第一次依赖注入的 F
         代理对象虽然还是同一个，但当每次**使用代理对象的任意方法**时，由代理创建新的 f 对象

    singleton: 每次获取都是同一个对象
    prototype：每次获取都产生新的对象
    request：生命周期和request请求一样，请求开始创建bean，结束销毁
    session：会话开始创建bean，结束销毁bean
    application：应用开始创建bean，结束销毁

    jdk >= 9 如果反射调用 jdk 中方法
    加了lazy，注入的是代理对象，打印时调用了每个代理对象的toString方法，内部会反射调用到object的toString方法
    object是JDK中的类，就会报错
    解决方法：1.更换jdk  2.重写toString  3.运行时添加 --add-opens java.base/java.lang=ALL-UNNAMED
    jdk <= 8 不会有问题

    演示 request, session, application 作用域
    打开不同的浏览器, 刷新 http://localhost:8080/test 即可查看效果
    如果 jdk > 8, 运行时添加 --add-opens java.base/java.lang=ALL-UNNAMED
 */

@SpringBootApplication
public class A08 {
    public static void main(String[] args) {
        SpringApplication.run(A08.class, args);
        /*
            总结一下：
                a. 有几种 scope  5种
                b. 在 singleton 中使用其它几种 scope 的方法
                c. 其它 scope 的销毁
                    1. 可以将通过 server.servlet.session.timeout=10s 观察 session bean 的销毁
                    2. ServletContextScope 销毁机制疑似实现有误
         */
    }
}
