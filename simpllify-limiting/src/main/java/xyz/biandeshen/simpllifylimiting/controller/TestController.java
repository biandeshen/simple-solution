package xyz.biandeshen.simpllifylimiting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.biandeshen.simpllifylimiting.anno.RateLimit;

import java.util.concurrent.TimeUnit;

/**
 * @FileName: TestController
 * @Author: fjp
 * @Date: 2020/11/19 13:37
 * @Description: 测试限流
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/19           版本号
 */
@RestController
public class TestController {
	
	@RateLimit(limitNum = 100, enableIPLimit = true)
	@GetMapping("/hello")
	public String hello() {
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "hello ratelimiter!";
	}
}

