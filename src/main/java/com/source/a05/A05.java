package com.source.a05;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/*
    BeanFactory 后处理器的作用：为BeanFactory提供扩展
 */

public class A05 {
    private static final Logger log = LoggerFactory.getLogger(A05.class);

    public static void main(String[] args) throws IOException {

        // ⬇️GenericApplicationContext 是一个【干净】的容器
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);


//        context.registerBean(ConfigurationClassPostProcessor.class);
//        // 解析 @ComponentScan @Bean @Import @ImportResource
//
//        context.registerBean(MapperScannerConfigurer.class, bd -> { // @MapperScanner
//            bd.getPropertyValues().add("basePackage", "com.source.a05.mapper");
//        }); //---------------MyBatis



        //手动实现ComponentScan ---@ComponentScan
        ComponentScan componentScan = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);
        if (componentScan != null) {
            for (String s : componentScan.basePackages()) {
                //System.out.println(s);//包名
                //com.source.a05.component-->classpath*:com/source/a05/**/*.class
                String path = "classpath*:" + s.replace(".", "/") + "/**/*.class";
                //System.out.println(path);
                CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
                Resource[] resources = context.getResources(path);
                AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
                for (Resource resource : resources) {
                    MetadataReader reader = factory.getMetadataReader(resource);
                    //输出对应包下的所有类
                    //System.out.println("类名为：" + reader.getClassMetadata().getClassName());
                    AnnotationMetadata metadata = reader.getAnnotationMetadata();
                    //输出对应包下的加了Component注解的类
                    //System.out.println("是否加Component"+metadata.hasAnnotation(Component.class.getName()));
                    //System.out.println("是否加Component派生"+metadata.hasMetaAnnotation(Component.class.getName()));
                    if (metadata.hasAnnotation(Component.class.getName())
                            || metadata.hasMetaAnnotation(Component.class.getName())) {
                        AbstractBeanDefinition bd = BeanDefinitionBuilder
                                .genericBeanDefinition(reader.getClassMetadata().getClassName())
                                .getBeanDefinition();
                        //加入bean工厂
                        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
                        String beanName = generator.generateBeanName(bd, beanFactory);
                        beanFactory.registerBeanDefinition(beanName, bd);
                    }
                }
            }
        }


        //手动实现读取配置类中的bean ---@Bean
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
        MetadataReader reader = factory.getMetadataReader(
                new ClassPathResource("com/source/a05/Config.class"));
        Set<MethodMetadata> methods = reader.getAnnotationMetadata()
                .getAnnotatedMethods(Bean.class.getName());
        for (MethodMetadata method : methods) {
            System.out.println(method);
            String initMethod = method.getAnnotationAttributes(Bean.class.getName())
                    .get("initMethod").toString();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
            builder.setFactoryMethodOnBean(method.getMethodName(), "config");
            //设置自动装配模式，不然sqlSessionFactoryBean会报错，因为他有参数
            builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
            // 用于处理这种 @Bean(initMethod = "init")
            if (initMethod.length()>0){
                builder.setInitMethodName(initMethod);
            }
            AbstractBeanDefinition bd = builder.getBeanDefinition();
            context.getDefaultListableBeanFactory().registerBeanDefinition(method.getMethodName(), bd);
        }






//        context.registerBean(ComponentScanPostProcessor.class); // 解析 @ComponentScan
//
//        context.registerBean(AtBeanPostProcessor.class); // 解析 @Bean
        context.registerBean(MapperPostProcessor.class); // 解析 Mapper 接口


        // ⬇️初始化容器
        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }

//        Mapper1 mapper1 = context.getBean(Mapper1.class);
//        Mapper2 mapper2 = context.getBean(Mapper2.class);

        // ⬇️销毁容器
        context.close();

        /*
            学到了什么
                a. @ComponentScan, @Bean, @Mapper 等注解的解析属于核心容器(即 BeanFactory)的扩展功能
                b. 这些扩展功能由不同的 BeanFactory 后处理器来完成, 其实主要就是补充了一些 bean 定义
         */
    }
}
