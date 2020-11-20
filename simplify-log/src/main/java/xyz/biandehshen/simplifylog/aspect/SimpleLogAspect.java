package xyz.biandehshen.simplifylog.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.biandehshen.simplifylog.anno.SimpleLog;
import xyz.biandehshen.simplifylog.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @FileName: SimpleLogAspect
 * @Author: fjp
 * @Date: 2020/11/19 9:12
 * @Description: 日志切面  TODO 与 javac -parameter 和 全局 traceid 结合使用
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/19           版本号
 */

@Slf4j
@Aspect
@Scope
@Component
public class SimpleLogAspect {
	private static final String LINE_SEPARATOR = System.lineSeparator();
	
	/**
	 * 以自定义 @PrintlnLog 注解作为切面入口
	 */
	@Pointcut("@annotation(xyz.biandehshen.simplifylog.anno.SimpleLog)")
	public void simpleLog() {
	}
	
	/**
	 * @param joinPoint
	 *
	 * @author fu
	 * @description 切面方法入参日志打印
	 * @date 2020/7/15 10:30
	 */
	@Before("simpleLog()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		assert attributes != null;
		HttpServletRequest request = attributes.getRequest();
		
		String methodDetailDescription = this.getAspectMethodLogDescJP(joinPoint);
		
		log.info("------------------------------- start --------------------------");
		/**
		 * 打印自定义方法描述
		 */
		log.info("Method detail Description: {}", methodDetailDescription);
		/**
		 * 打印请求入参
		 */
		log.info("Request Args: {}", JsonUtils.objectToJsonJackSonImpl(joinPoint.getArgs()));
		/**
		 * 打印请求方式
		 */
		log.info("Request method: {}", request.getMethod());
		/**
		 * 打印请求 url
		 */
		log.info("Request URL: {}", request.getRequestURL().toString());
		
		/**
		 * 打印调用方法全路径以及执行方法
		 */
		log.info("Request Class and Method: {}.{}", joinPoint.getSignature().getDeclaringTypeName(),
		         joinPoint.getSignature().getName());
	}
	
	/**
	 * @param proceedingJoinPoint
	 *
	 * @author xiaofu
	 * @description 切面方法返回结果日志打印
	 * @date 2020/7/15 10:32
	 */
	@Around("simpleLog()")
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		String aspectMethodLogDescPJ = getAspectMethodLogDescJP(proceedingJoinPoint);
		
		long startTime = System.currentTimeMillis();
		
		Object result = proceedingJoinPoint.proceed();
		/**
		 * 输出结果
		 */
		log.info("{}，Response result  : {}", aspectMethodLogDescPJ, JsonUtils.objectToJsonJackSonImpl(result));
		
		/**
		 * 方法执行耗时
		 */
		log.info("Time Consuming: {} ms", System.currentTimeMillis() - startTime);
		
		return result;
	}
	
	/**
	 * @author xiaofu
	 * @description 切面方法执行后执行
	 * @date 2020/7/15 10:31
	 */
	@After("simpleLog()")
	public void doAfter(JoinPoint joinPoint) {
		log.info("------------------------------- End --------------------------" + LINE_SEPARATOR);
	}
	
	/**
	 * @param joinPoint
	 *
	 * @author xiaofu
	 * @description @PrintlnLog 注解作用的切面方法详细细信息
	 * @date 2020/7/15 10:34
	 */
	private String getAspectMethodLogDescJP(JoinPoint joinPoint) throws Exception {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		return getAspectMethodLogDesc(targetName, methodName, arguments);
	}
	
	/**
	 * @param proceedingJoinPoint
	 *
	 * @author xiaofu
	 * @description @PrintlnLog 注解作用的切面方法详细细信息
	 * @date 2020/7/15 10:34
	 */
	private String getAspectMethodLogDescPJ(ProceedingJoinPoint proceedingJoinPoint) throws Exception {
		return getAspectMethodLogDescJP(proceedingJoinPoint);
	}
	
	/**
	 * @param targetName
	 * @param methodName
	 * @param arguments
	 *
	 * @author xiaofu
	 * @description 自定义注解参数
	 * @date 2020/7/15 11:51
	 */
	public String getAspectMethodLogDesc(String targetName, String methodName, Object[] arguments) throws Exception {
		Class<?> targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		StringBuilder description = new StringBuilder();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					description.append(method.getAnnotation(SimpleLog.class).description());
					break;
				}
			}
		}
		return description.toString();
	}
}