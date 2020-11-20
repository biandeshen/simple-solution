package xyz.biandeshen.simpllifylimiting.anno;   /**
 * @Title: RateLimit
 * @ProjectName simple-solution
 * @Description: TODO
 * @author fjp
 * @date 2020/11/1910:44
 */

import java.lang.annotation.*;

/**
 * @FileName: RateLimit
 * @Author: fjp
 * @Date: 2020/11/19 10:44
 * @Description: 自定义限流注解
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/19           版本号
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@Inherited 此元注释仅使注释从超类继承；已实现的接口上的注释无效
@Inherited
@Documented
public @interface RateLimit {
	double limitNum() default 200;  //默认每秒放入桶中的token
	
	/**
	 * 默认取消ip限制
	 *
	 * @return
	 *
	 * @see xyz.biandeshen.simpllifylimiting.service.DefaultIPCacheService#DefaultIPCacheService(boolean)
	 * 此处修改ip限制的大小
	 */
	boolean enableIPLimit() default false;
}