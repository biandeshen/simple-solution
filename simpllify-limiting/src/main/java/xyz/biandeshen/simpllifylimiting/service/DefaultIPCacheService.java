package xyz.biandeshen.simpllifylimiting.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @FileName: DefaultIPCacheService
 * @Author: fjp
 * @Date: 2020/11/19 14:09
 * @Description: 默认的ip缓存策略服务
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/19           版本号
 */
@Service
public class DefaultIPCacheService implements IPCacheService {
	
	private LoadingCache<String, RateLimiter> ipRequestCaches;
	
	/**
	 * 各ip共享的速率限制
	 */
	private RateLimiter IPShareRateLimiter;
	
	/**
	 * 默认限流ip qps 100
	 */
	public DefaultIPCacheService() {
		this(false);
	}
	
	/**
	 * 默认限流ip qps 100
	 */
	public DefaultIPCacheService(boolean enableIPLimitShare) {
		this(enableIPLimitShare, RateLimiter.create(200));
	}
	
	/**
	 * 默认限流ip qps 100
	 *
	 * @param rateLimiter
	 * 		提供一个速率限制
	 */
	public DefaultIPCacheService(boolean enableIPLimitShare, RateLimiter rateLimiter) {
		IPShareRateLimiter = rateLimiter;
		ipRequestCaches = CacheBuilder.newBuilder()
				                  // 设置缓存个数
				                  .maximumSize(1000)
				                  // 写后过期时间
				                  .expireAfterWrite(1, TimeUnit.MINUTES)
				                  // 构建方法
				                  .build(new CacheLoader<String, RateLimiter>() {
					                  @Override
					                  public RateLimiter load(String s) {
						                  // 新的IP初始化，各ip速率限制独立计算
						                  if (enableIPLimitShare) {
							                  return IPShareRateLimiter;
						                  } else {
							                  // (限流每秒100个令牌响应,即1s 100个令牌)
							                  return rateLimiter;
						                  }
					                  }
				                  });
	}
	
	
	@Override
	public RateLimiter getIPLimiter(String ipAddr) throws ExecutionException {
		return ipRequestCaches.get(ipAddr);
	}
	
	
}