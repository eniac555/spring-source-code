package com.source.a08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 一个单例bean，注入其他scope的bean，会出现问题，解决方式是加@Lazy注解
 * 原因：对于单例对象来讲，依赖注入仅发生了一次，后续再没有用到多例的 F，因此 E 用的始终是第一次依赖注入的 F
 *      代理对象虽然还是同一个，但当每次**使用代理对象的任意方法**时，由代理创建新的 f 对象
 */
@RestController
public class MyController {

    //加了lazy，注入的是代理对象，打印时调用了每个代理对象的toString方法，内部会反射调用到object的toString方法
    //object是JDK中的类，就会报错
    @Lazy
    @Autowired
    private BeanForRequest beanForRequest;

    @Lazy
    @Autowired
    private BeanForSession beanForSession;

    @Lazy
    @Autowired
    private BeanForApplication beanForApplication;


    @GetMapping(value = "/test", produces = "text/html")
    public String test(HttpServletRequest request, HttpSession session) {
        ServletContext sc = request.getServletContext();
        String sb = "<ul>" +
                    "<li>" + "request scope:" + beanForRequest + "</li>" +
                    "<li>" + "session scope:" + beanForSession + "</li>" +
                    "<li>" + "application scope:" + beanForApplication + "</li>" +
                    "</ul>";
        return sb;
        //每次刷新请求，beanForRequest对应的输出会变化，
        //证明了request：生命周期和request请求一样，请求开始创建bean，结束销毁

        //切换浏览器，beanForSession变化，不同浏览器会维护不同的session会话

        //
    }

}
