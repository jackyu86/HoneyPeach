package com.open.configs.web.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsStatics;

import com.open.configs.web.util.AppUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ValueStack;

public class AppInteceptor implements Interceptor {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5362585589965470280L;

	/**
	 * tokens
	 */
	private List<String> supperTokens;
	
	
	public List<String> getSupperTokens() {
		return supperTokens;
	}

	public void setSupperTokens(List<String> supperTokens) {
		this.supperTokens = supperTokens;
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init() {

	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext actionContext = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);
		//HttpServletResponse response = (HttpServletResponse) actionContext.get(StrutsStatics.HTTP_RESPONSE);

		String path = request.getParameter("path");
		String password = request.getParameter("password");
		root = new HashMap<String, Object>();
		String url = request.getRequestURI();
		if(url.contains("getChildren")){
			return invocation.invoke();
		}
		if (StringUtils.isNotEmpty(path)) {
			if(path.contains(" ")){
				root.put("success", false);
				root.put("result", "path error!!!");
				root.put("msg", "path error!!!");
				root.put("time", System.currentTimeMillis());
				invocation.getStack().set("root", root);
				return "JSON";
			}
			String app;
			try {
				app = AppUtils.getApp(path);
			} catch (Exception e1) {
				root.put("success", false);
				root.put("result", "path error!!!");
				root.put("msg", "获取app出错!!!");
				root.put("time", System.currentTimeMillis());
				invocation.getStack().set("root", root);
				return "JSON";
			}
			
			if(!auth(app, password)){
					ValueStack valueStack = actionContext.getValueStack();
					valueStack.set("error", "password error!!!");
					
					
					root.put("success", false);
					root.put("result", "用户没有权限");
					root.put("msg", "用户没有权限");
					root.put("time", System.currentTimeMillis());
					invocation.getStack().set("root", root);
					return "JSON";
			
			}
		}

		return invocation.invoke();
	}
	
	/**
	 * 验证密码
	 * @param app
	 * @param password
	 * @return
	 */
	private boolean auth(String app, String password) {
		if(StringUtils.isEmpty(password)){
			return false;
		}
		if(supperPass(password)){
			return true;
		}
		
		if("auth".equalsIgnoreCase(app) && !supperPass(password)){
			return false;
		}
		if(password.equalsIgnoreCase(getToken(app))){
			return true;
		}
		return false;
	}
	
	/**
	 * supperToken check
	 * @param password
	 * @return
	 */
	public boolean supperPass(String password){
		if(CollectionUtils.isEmpty(supperTokens)){
			return false;
		}
		return supperTokens.contains(password);
	}
	
	private Map<String, Object> root;

	public Map<String, Object> getRoot() {
		return root;
	}

	public void setRoot(Map<String, Object> root) {
		this.root = root;
	}
	
	/**
	 * create token by app and random
	 * @param app
	 * @return
	 */
	public static String getToken(String app){
		return DigestUtils.shaHex(app + "===random===");
	}
	
	public static void main(String[] args) {
		String app = "sku";
		System.out.println(app + "===>" + getToken(app));
		
		app = "customer";
		System.out.println(app + "===>" + getToken(app));
		
		app = "app1";
		System.out.println(app + "===>" + getToken(app));
		
		app = "orderworkflow";
		System.out.println(app + "===>" + getToken(app));
		
		//ordererp
		app = "ordererp";
		System.out.println(app + "===>" + getToken(app));
		
		app = "redis-monitor";
		System.out.println(app + "===>" + getToken(app));
		
		//orderstateworker
		app = "orderstateworker";
		System.out.println(app + "===>" + getToken(app));
		
		app = "promo-tianying";
		System.out.println(app + "===>" + getToken(app));

        app = "payment-gw";
        System.out.println(app + "===>" + getToken(app));

	}

}
