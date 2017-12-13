package org.learn.open.rest.filter;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;

/**   
 * This class is used for  all request auth filter
 * 一致性验证规则(加密)
 * 
 * @author jack  
 * @version   
 *       1.0, 2015年5月8日 下午1:31:55   
 */
//预先处理
@PreMatching
public class UserAuthClientRequestFilter implements ClientRequestFilter {
	/* (non-Javadoc)
	 * @see javax.ws.rs.client.ClientRequestFilter#filter(javax.ws.rs.client.ClientRequestContext)
	 */
	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		System.out.println("客户端用户权限验证逻辑开始---------------------");
		/*
		 * ...
		 * */
	
		System.out.println("客户端用户权限验证逻辑结束---------------------");
	}

}
