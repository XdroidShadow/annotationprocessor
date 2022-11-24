package com.xdroid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.LOCAL_VARIABLE})
public @interface XDlocalvar {

   /**
    *   提示信息
    */
   String value() default "";

}
