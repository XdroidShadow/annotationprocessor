package com.xdroid.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * 注释解析器
 * 参考:https://www.cnblogs.com/yaoxiaowen/p/6753964.html
 */
public class XDAnnotationProcessor extends AbstractProcessor {
    private static final String TAG = "com.xdroid.annotation.XDAnnotationProcessor";
    private Messager messager;

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

        printMsg(String.format("%s%s%s", TAG, "/", "xdroid注解解释器init"));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        //得到使用了 【com.xdroid.annotation.XDTip】 注解的元素
        Set<? extends Element> eleStrSet = env.getElementsAnnotatedWith(XDTip.class);
        for (Element eleStr : eleStrSet) {

            //因为我们知道SQLString元素的使用范围是在域上，所以这里我们进行了强制类型转换
            //VariableElement
            ExecutableElement eleStrVari = (ExecutableElement) eleStr;
            XDTip xdTip = eleStrVari.getAnnotation(XDTip.class);

            String info = String.format(">>>>  %s() - > %s", eleStrVari.getSimpleName(), xdTip.value());
            //怎么获取这个方法所在的类？
            printMsg(info);
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotaions = new TreeSet<>();
        annotaions.add("com.xdroid.annotation.XDTip");


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
//        try {
//            File file2Dir = new File("/sdcard/test/");
//            if (!file2Dir.exists()) {
//                file2Dir.mkdirs();
//            }
//
//            File file2File = new File("/sdcard/test/info.txt");
//            if (!file2File.exists()) {
//                file2File.createNewFile();
//            }
//
//            FileWriter fileWriter = new FileWriter(file2File);
//            fileWriter.write(msg);
//            fileWriter.flush();
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
