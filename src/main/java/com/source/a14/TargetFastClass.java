package com.source.a14;

import com.source.a13.Target;
import org.springframework.cglib.core.Signature;


//正常应该继承FastClass这个父类，但是需要重写的方法太多，就自己实现

// MethodProxy.create 运行时会生成对应的 FastClass 这个类
//MethodProxy.create(Target.class, Proxy.class, "()V", "save", "saveSuper")，
// 1.根据参数里面的方法签名获取每个方法对应的编号
// 2.methodProxy.invoke() 会直接调用TargetFastClass中的invoke方法，从而避免了反射调用
public class TargetFastClass {
    static Signature s0 = new Signature("save", "()V");
    static Signature s1 = new Signature("save", "(I)V");
    static Signature s2 = new Signature("save", "(J)V");


    /*
        Target
            save()              0
            save(int)           1
            save(long)          2
        signature 包括方法名字、参数返回值
     */

    // 获取目标方法的编号
    public int getIndex(Signature signature) {
        if (s0.equals(signature)) {
            return 0;
        } else if (s1.equals(signature)) {
            return 1;
        } else if (s2.equals(signature)) {
            return 2;
        }
        return -1;
    }


    // 根据getIndex返回的方法编号, 正常调用目标对象方法
    public Object invoke(int index, Object target, Object[] args) {
        if (index == 0) {
            ((Target) target).save();
            return null;
        } else if (index == 1) {
            ((Target) target).save((int) args[0]);
            return null;
        } else if (index == 2) {
            ((Target) target).save((long) args[0]);
            return null;
        } else {
            throw new RuntimeException("无此方法");
        }
    }

    public static void main(String[] args) {
        TargetFastClass fastClass = new TargetFastClass();
        int index = fastClass.getIndex(new Signature("save", "(I)V"));
        System.out.println(index);
        fastClass.invoke(index, new Target(), new Object[]{100});
    }
}
