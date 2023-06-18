package org.example.mvc.handler.mapping;

public interface HandlerMapping {
    Object findHandler(HandlerKey handlerKey);
}
