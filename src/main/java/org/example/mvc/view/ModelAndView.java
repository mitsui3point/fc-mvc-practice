package org.example.mvc.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private final Object view;
    private Map<String, Object> model = new HashMap<>();

    public ModelAndView(String viewName) {
        this.view = viewName;
    }

    public Map<String, ?> getModel() {
        return Collections.unmodifiableMap(model);
    }

    public String getView() {
        return this.view instanceof String ? (String) this.view : null;// 왜 굳이 타입체크를 하는가? 스프링에 그렇게 구현되어있기 때문에..
    }
}
