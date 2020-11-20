package xyz.biandeshen.simpllifylimiting.aspect;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.biandeshen.simpllifylimiting.anno.RateLimit;
import xyz.biandeshen.simpllifylimiting.service.IPCacheService;
import xyz.biandeshen.simpllifylimiting.utils.IPUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fjp
 * @Title: RateLimitAspect
 * @ProjectName simple-solution
 * @Description: TODO
 * @date 2020/11/1910:47
 */

@Slf4j
@Aspect
@Scope
@Component
//@Profile("dev") //生效环境
public class RateLimitAspect {
	
	@Autowired
	private IPCacheService ipCacheService;
	
	/**
	 * 用来存放不同接口的RateLimiter(key为接口名称，value为RateLimiter)
	 */
	private ConcurrentHashMap<String, RateLimiter> map = new ConcurrentHashMap<>();
	
	@Pointcut("@annotation(xyz.biandeshen.simpllifylimiting.anno.RateLimit)")
	public void serviceLimit() {
	}
	
	@Around("serviceLimit()")
	public Object around(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
		Object obj = null;
		//获取拦截的方法名
		Signature sig = joinPoint.getSignature();
		//获取拦截的方法名
		MethodSignature msig = (MethodSignature) sig;
		//返回被织入增加处理目标对象
		Object target = joinPoint.getTarget();
		//为了获取注解信息
		Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
		//获取注解信息
		RateLimit annotation = currentMethod.getAnnotation(RateLimit.class);
		//获取注解每秒加入桶中的token
		double limitNum = annotation.limitNum();
		//是否启用ip限制
		boolean enableIPLimit = annotation.enableIPLimit();
		
		String ipAddr = null;
		RateLimiter ipLimiter;
		HttpServletRequest request;
		HttpServletResponse response = null;
		if (enableIPLimit) {
			ServletRequestAttributes attributes =
					(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attributes != null) {
				request = attributes.getRequest();
				response = attributes.getResponse();
				ipAddr = IPUtil.getIpAddr(request);
			} else {
				//	抛出异常
				log.warn("获取ip异常");
			}
		}
		//注解所在方法名区分不同的限流策略  -  全限定名+方法名+参数名
		String functionName = getUniqueFunctionName(target, currentMethod);
		
		//获取rateLimiter
		RateLimiter rateLimiter;
		if (map.containsKey(functionName)) {
			rateLimiter = map.get(functionName);
		} else {
			map.put(functionName, RateLimiter.create(limitNum));
			rateLimiter = map.get(functionName);
		}
		
		try {
			if (enableIPLimit) {
				ipLimiter = ipCacheService.getIPLimiter(ipAddr);
				if (rateLimiter.tryAcquire() && ipLimiter.tryAcquire()) {
					//执行方法
					obj = joinPoint.proceed();
					return obj;
				}
			} else {
				if (rateLimiter.tryAcquire()) {
					//执行方法
					obj = joinPoint.proceed();
					return obj;
				}
			}
			
			//拒绝了请求（服务降级）
			String result = "系统繁忙！" + ipAddr;
			outErrorResult(response, result);
			
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		
		return obj;
	}
	
	/**
	 * 唯一的函数名称
	 *
	 * @param target
	 * 		目标对象
	 * @param currentMethod
	 * 		当前方法
	 *
	 * @return
	 */
	private String getUniqueFunctionName(Object target, Method currentMethod) {
		StringBuilder functionName = new StringBuilder(target.getClass().getName());
		functionName.append("#");
		functionName.append(currentMethod.getName());
		functionName.append("?");
		for (Parameter parameter : currentMethod.getParameters()) {
			functionName.append(parameter.getType().getSimpleName());
			functionName.append("=");
			functionName.append(parameter.getName());
			functionName.append("&");
		}
		int index = functionName.lastIndexOf("&");
		if (index >= 0) {
			functionName.deleteCharAt(index);
		}
		return functionName.toString();
	}
	
	/**
	 * 方法降级时响应结果
	 *
	 * @param response
	 * @param result
	 */
	public String outErrorResult(HttpServletResponse response, String result) {
		log.warn("拒绝了请求：{}", result);
		if (response != null) {
			try {
				try (ServletOutputStream outputStream = response.getOutputStream()) {
					response.setContentType("text/plain;charset=UTF-8");
					response.setStatus(500);
					outputStream.write("response 的降级响应".getBytes(StandardCharsets.UTF_8));
				}
			} catch (IOException e) {
				return "response 的降级异常响应";
			}
		}
		return "方法降级";
	}
	
	public void setIpCacheService(IPCacheService ipCacheService) {
		this.ipCacheService = ipCacheService;
	}
}
