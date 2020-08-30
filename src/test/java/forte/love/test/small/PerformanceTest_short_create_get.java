/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     PerformanceTest_long_create_get.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */

package forte.love.test.small;

import com.simplerobot.modules.utils.codes.FastKQCode;
import com.simplerobot.modules.utils.codes.MapKQCode;

/**
 *
 * 测试
 * {@link FastKQCode}
 * 与
 * {@link MapKQCode}
 * 的性能差异并寻找其各自的优势
 *
 * 此类测试短CQ下两个类的实例化并获取参数的速度
 *
 * Created by lcy on 2020/8/14.
 * @author lcy
 */
public class PerformanceTest_short_create_get {
	public static void main(String[] args) {
		String longCq = "[CQ:image,url=http://forte.love:15520/img/r]";

		/* 参数获取速度 */
		int times = 10_000_000;
		String getParam = "url";
		System.out.println("start-fast....");
		long getTime_fast = createAndGetTest_fast(longCq, getParam, times);
		System.out.println("getTime_fast: " + getTime_fast);
		System.out.println("start-map.....");
		long getTime_map = createAndGetTest_map(longCq, getParam, times);
		System.out.println("getTime_map: " + getTime_map);
		System.out.printf("%s\t:\t%s\t(%,d)", getTime_fast, getTime_map, times);
		/*
			fast	:	map	(times)
			4749	:	10405	(10,000,000)
			4661	:	10380	(10,000,000)
			4779	:	11276	(10,000,000)
			4716	:	10413	(10,000,000)
			5173	:	10886	(10,000,000)

			// 相对长数值来讲，fastKQCode的优势不再过于明显

			—————————————————————————— fast中追加了get缓存后

			4651	:	10315	(10,000,000)
			4802	:	10382	(10,000,000)
			4765	:	10311	(10,000,000)
			5217	:	10755	(10,000,000)
			4779	:	10424	(10,000,000)

			// 没有明显改善

		 */

	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long createAndGetTest_fast(String code, String get, int times){
		for (int i = 0; i < 100; i++) {
			FastKQCode.fastByCode(code).get(get);
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			FastKQCode.fastByCode(code).get(get);
		}
		return time() - s;
	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long createAndGetTest_map(String code, String get, int times){
		for (int i = 0; i < 100; i++) {
			MapKQCode.byCode(code).get(get);
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			MapKQCode.byCode(code).get(get);
		}
		return time() - s;
	}






	private static long time(){
		return System.currentTimeMillis();
	}
}

