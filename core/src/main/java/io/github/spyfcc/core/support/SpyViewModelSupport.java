package io.github.spyfcc.core.support;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.spyfcc.core.config.PropsConfig;

public final class SpyViewModelSupport {
	
	   private SpyViewModelSupport() {
	    }

	    public static Map<String, Object> baseModel(String contextPath, PropsConfig props) {
	        Map<String, Object> model = new LinkedHashMap<>();
	        model.put("trafficSpyUiPath", SpyPathSupport.fullUiPath(contextPath, props));
	        return model;
	    }

	    public static Map<String, Object> pageModel(String contextPath, PropsConfig props, String pageTitle) {
	        Map<String, Object> model = baseModel(contextPath, props);
	        model.put("pageTitle", pageTitle);
	        return model;
	    }

}
