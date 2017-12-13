package org.learn.open.rest.filter;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.learn.open.rest.bind.ErrorLog;


/**
 * This class is used for ... 错误日志拦截插件
 * 服务端，客户端
 * 名称绑定 
 * 作用范围建议:已经出现错误容错处理
 * 作用域:rest资源类 
 * @author jack
 * @version 1.0, 2015年5月9日 下午12:28:16
 */
@ErrorLog
@Priority(Priorities.USER)
public class ErrorLogRequestFilter implements ClientRequestFilter,
		ContainerRequestFilter{
	private static final Logger LOGGER = Logger
			.getLogger(ErrorLogRequestFilter.class);
	private static final String NOTIFICATION_PREFIX = "* ";
	private static final String SERVER_REQUEST = "> ";
	private static final String CLIENT_REQUEST = "/ ";
	private static final AtomicLong logSequence = new AtomicLong(0);

	private StringBuilder prefixId(StringBuilder b, long id) {
		b.append(Long.toString(id)).append(" ");
		return b;
	}

	private void printRequestLine(final String prefix, StringBuilder b,
			long id, String method, URI uri) {
		prefixId(b, id).append(NOTIFICATION_PREFIX)
				.append("AirLog - Request received on thread ")
				.append(Thread.currentThread().getName()).append("\n");
		prefixId(b, id).append(prefix).append(method).append(" ")
				.append(uri.toASCIIString()).append("\n");
	}


	private void printPrefixedHeaders(final String prefix, StringBuilder b,
			long id, MultivaluedMap<String, String> headers) {
		for (Map.Entry<String, List<String>> e : headers.entrySet()) {
			List<?> val = e.getValue();
			String header = e.getKey();

			if (val.size() == 1) {
				prefixId(b, id).append(prefix).append(header).append(": ")
						.append(val.get(0)).append("\n");
			} else {
				StringBuilder sb = new StringBuilder();
				boolean add = false;
				for (Object s : val) {
					if (add) {
						sb.append(',');
					}
					add = true;
					sb.append(s);
				}
				prefixId(b, id).append(prefix).append(header).append(": ")
						.append(sb.toString()).append("\n");
			}
		}
	}

	/*
	 * 客户端日志
	 * 
	 * @see javax.ws.rs.client.ClientRequestFilter#filter(javax.ws.rs.client.
	 * ClientRequestContext)
	 */
	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		long id = logSequence.incrementAndGet();
		StringBuilder b = new StringBuilder();
		printRequestLine(CLIENT_REQUEST, b, id, requestContext.getMethod(),
				requestContext.getUri());
		printPrefixedHeaders(CLIENT_REQUEST, b, id,
				HeaderUtils.asStringHeaders(requestContext.getHeaders()));
		LOGGER.error(b.toString());
	}

	/*
	 * 服务端日志
	 * 
	 * @see
	 * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container
	 * .ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		long id = logSequence.incrementAndGet();
		StringBuilder b = new StringBuilder();
		printRequestLine(SERVER_REQUEST, b, id, requestContext.getMethod(),
				requestContext.getUriInfo().getRequestUri());
		printPrefixedHeaders(SERVER_REQUEST, b, id, requestContext.getHeaders());
		LOGGER.error(b.toString());
	}
	
}
