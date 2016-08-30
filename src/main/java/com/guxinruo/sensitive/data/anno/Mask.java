package com.guxinruo.sensitive.data.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来打码
 * Created by 顾冬煜
 * Date : 2016-08-04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mask {
    /**
     * 前置不需要打码的长度
     * @return
     */
    int prefixNoMaskLen() default 0;

    /**
     * 后置不需要打码的长度
     * @return
     */
    int suffixNoMaskLen() default 0;

    /**
     * 用什么打码
     * @return
     */
    String maskStr() default "*";
}
