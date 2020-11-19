package xyz.biandeshen.simpllifylimiting;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * @FileName: Test
 * @Author: fjp
 * @Date: 2020/11/19 11:42
 * @Description: History:
 * <author>          <time>          <version>
 * fjp           2020/11/19           版本号
 */
public class Test {
	
	@org.junit.Test
	public void test() throws NoSuchMethodException {
		String methodName = Thread.currentThread().getStackTrace()[0].getMethodName();
		Method method = this.getClass().getMethod(methodName, String.class);
		StringBuilder functionName =
				new StringBuilder(this.getClass().getName() + method.getName() + Arrays.toString(method.getParameterTypes()));
		for (Parameter parameter : method.getParameters()) {
			functionName.append(parameter);
		}
		System.out.println("functionName = " + functionName);
	}
	
}