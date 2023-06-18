package org.example.mvc;

import org.example.mvc.enums.RequestMethod;
import org.example.mvc.handler.adapter.AnnotationHandlerAdapter;
import org.example.mvc.handler.adapter.HandlerAdapter;
import org.example.mvc.handler.adapter.SimpleControllerHandlerAdapter;
import org.example.mvc.handler.mapping.AnnotationHandlerMapping;
import org.example.mvc.handler.mapping.HandlerKey;
import org.example.mvc.handler.mapping.HandlerMapping;
import org.example.mvc.handler.mapping.RequestMappingHandlerMapping;
import org.example.mvc.view.JspViewResolver;
import org.example.mvc.view.ModelAndView;
import org.example.mvc.view.View;
import org.example.mvc.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * tomcat 이 실행할 수 있으려면 servlet 이어야 함
 */
@WebServlet("/")//root path; 어떤 경로를 입력해도 DispatcherServlet 을 경유하도록
public class DispatcherServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    private List<HandlerMapping> handlerMappings;
    private List<ViewResolver> viewResolvers;
    private List<HandlerAdapter> handlerAdapters;

    /**
     * tomcat 이 servlet 을 singleton 으로 생성할 때, init() 메서드를 한번 호출함
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        RequestMappingHandlerMapping rmhm = new RequestMappingHandlerMapping();
        rmhm.init();
        AnnotationHandlerMapping ahm = new AnnotationHandlerMapping("org.example");
        ahm.init();
        handlerMappings = List.of(rmhm, ahm);

        SimpleControllerHandlerAdapter scha = new SimpleControllerHandlerAdapter();
        AnnotationHandlerAdapter aha = new AnnotationHandlerAdapter();
        handlerAdapters = List.of(scha, aha);

        viewResolvers = Collections.singletonList(new JspViewResolver());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("[DispatcherServlet] service started.");

        try {
            RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod());
            String requestURI = request.getRequestURI();

            HandlerKey handlerKey = new HandlerKey(requestMethod, requestURI);

            Object handler = handlerMappings.stream()
                    .filter(hm -> hm.findHandler(handlerKey) != null)
                    .map(hm -> hm.findHandler(handlerKey))
                    .findFirst()
                    .orElseThrow(() -> new ServletException("No handler for [" + requestMethod + ", " + requestURI + "]"));

            HandlerAdapter handlerAdapter = handlerAdapters.stream()
                    .filter(ha -> ha.supports(handler))
                    .findFirst()
                    .orElseThrow(() -> new ServletException("No adapter for handler [" + handler + "]"));

            ModelAndView modelAndView = handlerAdapter.handle(request, response, handler);

            for (ViewResolver viewResolver : viewResolvers) {
                View view = viewResolver.resolveView(modelAndView.getView());
                view.render(modelAndView.getModel(), request, response);
            }
        } catch (Exception e) {
            log.error("exception occurred:", e);
            throw new ServletException(e);
        }
    }
}
