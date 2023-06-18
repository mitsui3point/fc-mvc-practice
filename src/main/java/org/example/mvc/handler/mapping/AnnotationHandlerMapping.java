package org.example.mvc.handler.mapping;

import org.example.mvc.annotation.Controller;
import org.example.mvc.annotation.RequestMapping;
import org.reflections.Reflections;

import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationHandlerMapping implements HandlerMapping {

    private final Object[] basePackages;
    private Map<HandlerKey, AnnotationHandler> handler = new HashMap<>();

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackages = basePackage;
    }

    public void init() throws ServletException {
        Set<Class<?>> clazzWithControllerAnnotation = new Reflections(basePackages).getTypesAnnotatedWith(Controller.class);
        clazzWithControllerAnnotation.forEach(clazz ->
                Arrays.stream(clazz.getDeclaredMethods()).forEach(declaredMethod -> {
                    RequestMapping requestMapping = declaredMethod.getDeclaredAnnotation(RequestMapping.class);

                    // @RequestMapping(value = "/", methods = RequestMethod.GET)
                    Arrays.stream(requestMapping.methods()).forEach(requestMethod ->
                            handler.put(new HandlerKey(requestMethod, requestMapping.value()), new AnnotationHandler(clazz, declaredMethod)));
                }));
    }

    @Override
    public Object findHandler(HandlerKey handlerKey) {
        return handler.get(handlerKey);
    }
}
