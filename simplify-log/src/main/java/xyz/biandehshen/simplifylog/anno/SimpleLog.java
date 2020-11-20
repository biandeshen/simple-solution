package xyz.biandehshen.simplifylog.anno;

import java.lang.annotation.*;

/**
 * @FileName: SimpleLog
 * @Author: fjp
 * @Date: 2020/11/18 17:30
 * @Description: 自定义切面日志注解
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/18           0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@Inherited 此元注释仅使注释从超类继承；已实现的接口上的注释无效
@Inherited
@Documented
public @interface SimpleLog {
	
	/**
	 * 自定义日志描述信息文案
	 */
	String description() default "";
}