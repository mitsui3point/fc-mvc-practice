package org.example.mvc;

import org.example.mvc.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * tomcat 이 실행할 수 있으려면 servlet 이어야 함
 */
@WebServlet("/")//root path; 어떤 경로를 입력해도 DispatcherServlet 을 경유하도
public class DispatcherServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private RequestMappingHandlerMapping handlerMapping;

    /**
     * tomcat 이 servlet 을 singleton 으로 생성할 때, init() 메서드를 한번 호출함
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        handlerMapping = new RequestMappingHandlerMapping();
        handlerMapping.init();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("[DispatcherServlet] service started.");

        try {
            Controller handler = handlerMapping.findHandler(request.getRequestURI());
            String viewName = handler.handleRequest(request, response);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewName);
            requestDispatcher.forward(request, response);
        } catch (Exception e) {
            log.error("exception occurred:", e);
            throw new ServletException(e);
        }

    }
}
