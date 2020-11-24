package xyz.biandeshen.gatewaylimiting.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @FileName: UrlFilter
 * @Author: fjp
 * @Date: 2020/11/24 11:11
 * @Description: url过滤器
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/24           版本号
 */
public class UrlFilter implements GlobalFilter, Ordered {
	
	/**
	 * Process the Web request and (optionally) delegate to the next {@code WebFilter}
	 * through the given {@link GatewayFilterChain}.
	 *
	 * @param exchange
	 * 		the current server exchange
	 * @param chain
	 * 		provides a way to delegate to the next filter
	 *
	 * @return {@code Mono<Void>} to indicate when request processing is complete
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String url = request.getURI().getPath();
		//TODO 拦截特定URL地址
		System.out.println("url:"+url);
		return chain.filter(exchange);
	}
	
	@Override
	public int getOrder() {
		return 2;
	}
}