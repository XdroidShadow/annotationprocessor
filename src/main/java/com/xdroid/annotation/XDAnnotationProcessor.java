package com.xdroid.annotation;

import com.xdroid.annotation.tool.XDStrings;

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

import static com.xdroid.annotation.tool.XDStrings.unitMultiArgs;

/**
 * 注释解析器
 * 参考:https://www.cnblogs.com/yaoxiaowen/p/6753964.html
 */
public class XDAnnotationProcessor extends AbstractProcessor {
    private static final String TAG = "XDAnnotationProcessor";
    private Messager messager;
    private String line = "-------------------------------------------------------------------------------------";
    private String vLine = "| ";
    private String sp = ":";

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
        handleTip(env, XDTodo.class);
        return false;
    }

    private void handleTip(RoundEnvironment env, Class<? extends Annotation> a) {
        try {
            //得到使用了 【XDTip】 注解的元素
            Set<? extends Element> eleStrSet = env.getElementsAnnotatedWith(a);
            for (Element item : eleStrSet) {
                //获取注解所在类对象  getEnclosingElement
                String ownerClass = item.getEnclosingElement().getSimpleName().toString();//XDTestAidl
                String annotationType = "";
                String elementName = item.getSimpleName().toString();
                String elementInfo = "";
                //通过item.getKind()来判断类型

                switch (item.getKind()){
                    case METHOD:
                        annotationType = "METHOD";
                        elementInfo = unitMultiArgs("(", executableElementToString((ExecutableElement) item), ")");
                        break;
                    case FIELD:
                        annotationType = "FIELD";
                        elementInfo = "=" + variableToString((VariableElement) item);
                        break;
                    case PARAMETER:
                        annotationType = "PARAMETER";
                        ownerClass = unitMultiArgs(item.getEnclosingElement().getEnclosingElement().getSimpleName().toString(),
                                ".", item.getEnclosingElement().getSimpleName().toString(), "()");
                        break;
                    case CLASS:
                        annotationType = "CLASS";
                        break;
                    default:
                        annotationType = "else";
                        break;
                }

                //警告: | XDTestAidl(XDTodo)/test()  自定义接口 onBind/2022年11月25日16:03:38/METHOD
                //警告: | IndexActivity/testAnnotation(XDTip)/info1 >  参数要注意/PARAMETER
//                String info = String.format("%s(@%s)%s%s  %s/%s",
//                        ownerClass, a.getSimpleName(), elementName, elementInfo, getAnnotationValue(item, a), annotationType);

                //警告: | XDTestAidl:@XDTodo:METHOD:test()  自定义接口onBind；2022年11月25日16:03:38
//                StringBuilder infoSb = new StringBuilder();
//                infoSb.append(ownerClass);//XDTestAidl
//                infoSb.append(sp);
//                infoSb.append("@");//@
//                infoSb.append(a.getSimpleName());//@XDTodo
//                infoSb.append(sp);
//                infoSb.append(annotationType);//METHOD
//                infoSb.append(sp);
//                infoSb.append(elementName);//testAnnotation()
//                infoSb.append(elementInfo);//参数
//                infoSb.append(sp);
//                infoSb.append(getAnnotationValue(item, a));//自定义接口onBind；2022年11月25日16:03:38

                String info = unitMultiArgs(ownerClass, sp, "@", sp, annotationType, sp, elementName, elementInfo, sp, getAnnotationValue(item, a));

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
            case "XDTodo":
                String time = item.getAnnotation(XDTodo.class).time();
                if (!time.isEmpty()) {
                    time = time;
                }
                StringBuilder tempSb = new StringBuilder();
                tempSb.append("【");
                tempSb.append("TODO:");
                tempSb.append(time);
                tempSb.append(" ");
                tempSb.append(item.getAnnotation(XDTodo.class).value());
                tempSb.append("】");
                value = tempSb.toString();
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
        annotaions.add("com.xdroid.annotation.XDTodo");
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
