package study.hank.com.router_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import study.hank.com.annotation.ZRoute;
import study.hank.com.annotation.RouterConst;
import study.hank.com.annotation.facade.enums.RouteType;
import study.hank.com.annotation.facade.model.RouteMeta;


//注册APT
@AutoService(Processor.class)
//指定apt支持的java版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RouterProcessor extends AbstractProcessor {

    //文件工具
    private Filer mFiler;
    private String moduleName;
    private Elements elementUtils;//元素辅助类
    private Types typeUtils;//类辅助类


    /**
     * 必须重写的第一个方法，init。 重写时，获取内部关键的一些对象引用。包括文件辅助类Filer,类结构元素类Elements等。
     * <p>
     * 下面的代码，我还获取了options，这是可选参数。
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        Map<String, String> options = processingEnvironment.getOptions();
        moduleName = options.get("moduleName");
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    /**
     * 必须重写的第二个方法 process
     * <p>
     * 当发现我们自定义的注解被使用之后，process方法就会被执行，相当于普通类的main方法
     * <p>
     * 作用是：采集使用注册注解的信息，生成Java文件
     *
     * @param annotations 注解处理器所要处理的注解集合
     * @param roundEnv    回合环境，能够查询出被指定注解 注解的元素
     * @return true，处理了注解； false 未处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!annotations.isEmpty()) {
            //获取使用了指定 注解的类,指定Route
            //一个module中，有几处使用了Route注解，set的size就是几
            Set<? extends Element> routeCommElemtents = roundEnv.getElementsAnnotatedWith(ZRoute.class);
            generatedClassComm(RouterConst.GENERATION_PACKAGE_NAME, routeCommElemtents);
            return true;
        }
        return false;
    }

    //文件就要生成这样的内容
//        class ZRouter implements IRouterComm{
//            @Override
//            public void onLoad(Map<String, RouteMeta> routes) {
//                routes.put("/activity/chart2", RouteMeta.getInstance().destination(Chart2Activity.class).routeType(RouteType.ACTIVITY));
//                routes.put("/fragment/homeFragment", RouteMeta.getInstance().destination(HomeFragment.class).routeType(RouteType.FRAGMENT));
//            }
//        }
    private void generatedClassComm(String packageName, Set<? extends Element> routeElements) {
        if (routeElements.isEmpty()) {
            return;
        }
        // Map<String,RouteMeta>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(//ClassName rawType, TypeName... typeArguments
                ClassName.get(Map.class),//参数类
                ClassName.get(String.class),//参数的参数。。。省略号，后面的参数类别可以不限数量
                ClassName.get(RouteMeta.class));

        // Map<String,RouteMeta> routeComm
        ParameterSpec parameterSpec = ParameterSpec.builder(parameterizedTypeName, "routeComm").build();

        // public void onLoad(Map<String,RouteMeta> routeComm)
        MethodSpec.Builder loadPath = MethodSpec.methodBuilder("onLoad")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec);
        // 给这个方法添加方法体

        // 预定义要判定的类型(为什么要判定？因为 使用这个注解的可能是Activity，Fragment或者IProvider的子类,要区别对待)
        TypeMirror typeActivity = elementUtils.getTypeElement(RouteType.ACTIVITY.getClassName()).asType();//
        TypeMirror typeFragment = elementUtils.getTypeElement(RouteType.FRAGMENT.getClassName()).asType();//
        TypeMirror typeFragmentV4 = elementUtils.getTypeElement(RouteType.FRAGMENT_V4.getClassName()).asType();//
        TypeMirror typeProvider = elementUtils.getTypeElement(RouteType.PROVIDER.getClassName()).asType();//
        ClassName routeTypeClassName = ClassName.get(RouteType.class);
        RouteType routeType;
        for (Element element : routeElements) {
            ZRoute routeComm = element.getAnnotation(ZRoute.class);//拿到检测到的注解，因为拿到它之后，就能取注解内的参数值
            TypeMirror tm = element.asType();//拿到当前类

            //这里要进行判断，因为要知道当前使用这个注解的是Activity还是Fragment
            if (typeUtils.isSubtype(tm, typeActivity)) {//参数1，是不是参数2的子类
                //如果这次是Activity
                routeType = RouteType.ACTIVITY;
            } else if (typeUtils.isSubtype(tm, typeFragment) || typeUtils.isSubtype(tm, typeFragmentV4)) {//fragment有多个版本，这里要兼容
                //如果这次是Fragment
                routeType = RouteType.FRAGMENT;
            } else if (typeUtils.isSubtype(tm, typeProvider)) {
                routeType = RouteType.PROVIDER;
            } else {
                routeType = RouteType.UNKNOWN;
            }

            loadPath.addStatement("routeComm.put($S,RouteMeta.getInstance().destination($T.class).routeType($T." + routeType + ").path($S))",
                    routeComm.value(),//如果使用注解的地方写的是@routeComm("test"),那这个routeComm.value的值就是字符串 test
                    element,//使用这个注解的类
                    routeTypeClassName,
                    routeComm.value());//ZRoute
        }

        //现在，构建类头
        TypeElement routePath = elementUtils.getTypeElement(RouterConst.SUPER_ROUTER_INTERFACE);//要构建的类，的父类
        try {
            TypeSpec typeSpec = TypeSpec.classBuilder(moduleName + "_RouteComm")
                    .addSuperinterface(ClassName.get(routePath))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(loadPath.build())
                    .build();
            JavaFile.builder(packageName, typeSpec).build().writeTo(mFiler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 必须重写的第三个方法： 我们的注解解析类能够解析哪些注解，这里设定好
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ZRoute.class.getCanonicalName());
        return types;
    }

    /**
     * 可选重写的第四个方法：我们能够支持的参数是哪些，也要设定好
     *
     * @return
     */
    @Override
    public Set<String> getSupportedOptions() {
        Set<String> options = new LinkedHashSet<>();
        options.add("moduleName");
        return options;
    }
}
