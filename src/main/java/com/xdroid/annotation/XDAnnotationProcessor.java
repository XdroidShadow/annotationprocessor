package com.xdroid.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * 注释解析器
 * 参考:https://www.cnblogs.com/yaoxiaowen/p/6753964.html
 */
public class XDAnnotationProcessor extends AbstractProcessor {
    private static final String TAG = "XDAnnotationProcessor";
    private Messager messager;
    private String line = "-------------------------------------------------------------------------------------";
    private String vLine = "| ";

    public void parser(Class<?> c) {
        Objects.requireNonNull(c);
        Method[] methods = c.getMethods();
        for (Method m : methods) {
            XDTip annotation = m.getAnnotation(XDTip.class);
            if (annotation != null) {


            }
        }
    }


    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        messager = env.getMessager();
//        printMsg(line);
        printMsg(String.format("%sxdroid-build-information", vLine));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        handleTip(env, XDImportant.class);
        handleTip(env, XDModify.class);
        handleTip(env, XDTip.class);
//        printMsg(line);
        return false;
    }

    private void handleTip(RoundEnvironment env, Class<? extends Annotation> a) {
        try {
            //得到使用了 【XDTip】 注解的元素
            Set<? extends Element> eleStrSet = env.getElementsAnnotatedWith(a);
            for (Element item : eleStrSet) {
                //获取注解所在类对象  getEnclosingElement
                String ownerClass = item.getEnclosingElement().getSimpleName().toString();
                String annotationType = "";
                String elementName = item.getSimpleName().toString();
                String elementInfo;
                //通过item.getKind()来判断类型
                if (item.getKind() == ElementKind.METHOD) {//方法
                    annotationType = "METHOD";
                    elementInfo = "(" + executableElementToString((ExecutableElement) item) + ")";
                } else if (item.getKind() == ElementKind.FIELD) {//变量
                    annotationType = "FIELD";
                    elementInfo = "=" + variableToString((VariableElement) item);
                } else if (item.getKind() == ElementKind.PARAMETER) {//参数
                    annotationType = "PARAMETER";
                    ownerClass = item.getEnclosingElement().getEnclosingElement().getSimpleName().toString() + "/"
                            + item.getEnclosingElement().getSimpleName().toString();
                    elementInfo = "=" + variableToString((VariableElement) item);
                } else {
                    annotationType = "else";
                    elementInfo = "";
                }
                String info = String.format("%s(%s)/%s%s  %s/%s",
                        ownerClass, a.getSimpleName(), elementName, elementInfo, getAnnotationValue(item,a), annotationType);
                printMsg(String.format("%s%s", vLine, info));
            }
        } catch (Exception e) {
            printMsg(e.getMessage());
        }
    }

    private String getAnnotationValue(Element item, Class<? extends Annotation> a) {
        String value = "";
        switch (a.getSimpleName()) {
            case "XDTip":
                value = item.getAnnotation(XDTip.class).value();
                break;
            case "XDImportant":
                value = item.getAnnotation(XDImportant.class).value();
                break;
            case "XDModify":
                value = item.getAnnotation(XDModify.class).value();
                break;
        }
        return value;
    }

    private String getAnnotationType(Class<? extends Annotation> a) {
        String value = "";
        switch (a.getSimpleName()) {
            case "XDTip":
                value = "提示";
                break;
            case "XDImportant":
                value = "注意";
                break;
            case "XDModify":
                value = "修改";
                break;
        }
        return value;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotaions = new TreeSet<>();
        annotaions.add("com.xdroid.annotation.XDTip");
        annotaions.add("com.xdroid.annotation.XDImportant");
        annotaions.add("com.xdroid.annotation.XDModify");
        annotaions.add("com.xdroid.annotation.XDlocalvar");
        return annotaions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 我在代码中使用了 javax.annotation.processing.Messager来输出一些log信息，
     * 因为这个过程是在编译时输出的，所以System.out.println()就没用了，
     * 这个输出信息是给使用了该处理器的第三方程序员看的，不是给该处理器的作者看的。
     */
    private void printMsg(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    /**
     * 数据 tostring 【方法】
     */
    private String executableElementToString(ExecutableElement executableElement) {
        List<? extends VariableElement> parameters = executableElement.getParameters();
        StringBuilder sb = new StringBuilder();
        int size = parameters.size();
        VariableElement element;
        for (int i = 0; i < size - 1; i++) {
            element = parameters.get(i);
            sb.append(element.getSimpleName());
            sb.append(",");
        }
        if (size > 0) {
            element = parameters.get(size - 1);
            sb.append(element.getSimpleName());
        }
        return sb.toString();
    }

    private String variableToString(VariableElement item) {
        return "" + item.getConstantValue();
    }


}
