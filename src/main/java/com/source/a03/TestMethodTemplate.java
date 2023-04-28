package com.source.a03;

import java.util.ArrayList;
import java.util.List;

//Template Method Pattern：提高现有代码的扩展能力
public class TestMethodTemplate {

    public static void main(String[] args) {
        MyBeanFactory beanFactory = new MyBeanFactory();
        beanFactory.addBeanPostProcessor(bean -> System.out.println("解析 @Autowired"));

        beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public void inject(Object bean) {
                System.out.println("解析 @Resource");
            }
        });
        //对上面进行简化
        beanFactory.addBeanPostProcessor(bean -> System.out.println("解析 @Resource"));
        beanFactory.getBean();
    }



    // 模板方法  Template Method Pattern
    static class MyBeanFactory {
        public Object getBean() {
            Object bean = new Object();
            System.out.println("构造 " + bean);
            System.out.println("依赖注入 " + bean); // @Autowired, @Resource
            for (BeanPostProcessor processor : processors) {
                processor.inject(bean);
            }
            System.out.println("初始化 " + bean);
            return bean;
        }

        private List<BeanPostProcessor> processors = new ArrayList<>();

        public void addBeanPostProcessor(BeanPostProcessor processor) {
            processors.add(processor);
        }

    }


    //有了这个接口，不管怎么加扩展功能，都不用修改MyBeanFactory中的getBean 方法
    interface BeanPostProcessor {
        void inject(Object bean); // 对依赖注入阶段的扩展
    }
}
