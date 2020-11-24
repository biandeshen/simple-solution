package xyz.biandeshen.gatewaylimiting.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * @FileName: IpFilter
 * @Author: fjp
 * @Date: 2020/11/24 11:05
 * @Description: ip过滤器
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/24           版本号
 */
@Slf4j
@Component
public class IpFilter implements GlobalFilter, Ordered {
	
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
		InetSocketAddress remoteAddress = request.getRemoteAddress();
		//TODO 设置ip白名单  或用IPUtil
		log.info("受限的ip:{}", remoteAddress.getHostName());
		return chain.filter(exchange);
	}
	
	@Override
	public int getOrder() {
		return 1;
	}
}