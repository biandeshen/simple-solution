package xyz.biandeshen.gatewaylimiting.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @FileName: TestController
 * @Author: fjp
 * @Date: 2020/11/24 11:46
 * @Description: 测试服务
 * History:
 * <author>          <time>          <version>
 * fjp           2020/11/24           版本号
 */
@RestController
public class TestController {
	
	@RequestMapping("/test")
	public String test() {
		return "hello test";
	}
}