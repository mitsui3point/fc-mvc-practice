package org.example.mvc.handler.mapping;

import org.example.mvc.controller.*;
import org.example.mvc.enums.RequestMethod;

import java.util.HashMap;
import java.util.Map;

public class RequestMappingHandlerMapping implements HandlerMapping {
    private Map<HandlerKey, Controller> mappings = new HashMap<>();

    public RequestMappingHandlerMapping() {

    }

    public void init() {
        //mappings.put(new HandlerKey(RequestMethod.GET, "/"), new HomeController());
        mappings.put(new HandlerKey(RequestMethod.GET, "/users"), new UserListController());
        mappings.put(new HandlerKey(RequestMethod.POST, "/users"), new UserCreateController());
        mappings.put(new HandlerKey(RequestMethod.GET, "/user/form"), new ForwardController("/user/form"));
    }

    @Override
    public Object findHandler(HandlerKey handlerKey) {
        return mappings.get(handlerKey);
    }

}
