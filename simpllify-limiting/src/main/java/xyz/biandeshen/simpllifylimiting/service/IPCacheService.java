package xyz.biandeshen.simpllifylimiting.service;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @FileName: IPCacheService
 * @Author: fjp
 * @Date: 2020/11/19 14:07
 * @Description: IP的缓存策略接口
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/19           版本号
 */
public interface IPCacheService {
	RateLimiter getIPLimiter(String ipAddr) throws Exception;
}