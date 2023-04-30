package com.source.a11;
import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理
 */
public class JdkProxyDemo {

    interface Foo {
        void foo();
    }

    static final class Target implements Foo {
        //下面生成的代理对象和Target是兄弟关系，故这里改成final没有影响
        public void foo() {
            System.out.println("target foo");
        }
    }



    // jdk 只能针对 接口 代理
    // cglib 都可以
    //代理对象和Target是兄弟关系
    public static void main(String[] param) throws IOException {
        // 目标对象
        Target target = new Target();

        //普通类：Java源代码--编译成Java字节码--类加载并使用
        //代理类：没有源码，故直接生成字节码，然后加载使用

        //类加载器
        ClassLoader loader = JdkProxyDemo.class.getClassLoader(); // 用来加载在运行期间动态生成的字节码
        //参数1：类加载器  参数2：生成的代理要实现什么接口  参数3：代理类的增强方法，即代理类被调用时实际执行的方法
        Foo proxy = (Foo) Proxy.newProxyInstance(loader, new Class[]{Foo.class}, (p, method, args) -> {
            //p, method, args：代理对象本身，代理类正在执行的方法对象，参数列表
            System.out.println("before...");
            // 目标.方法(参数)
            //上下 相一致
            // 方法.invoke(目标, 参数);
            Object result = method.invoke(target, args);//反射调用
            System.out.println("after....");
            return result; // 让代理也返回目标方法执行的结果
        });

        System.out.println(proxy.getClass());

        proxy.foo();

        System.in.read();
    }
}