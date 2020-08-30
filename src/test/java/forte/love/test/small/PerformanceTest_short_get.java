/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     PerformanceTest_long_get.java
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
import com.simplerobot.modules.utils.KQCode;
import com.simplerobot.modules.utils.codes.MapKQCode;

/**
 *
 * 测试
 * {@link FastKQCode}
 * 与
 * {@link MapKQCode}
 * 的性能差异并寻找其各自的优势
 *
 * 此类测试短CQ下两个类的参数获取速度
 *
 * Created by lcy on 2020/8/14.
 * @author lcy
 */
public class PerformanceTest_short_get {
	public static void main(String[] args) {
		String longCq = "[CQ:image,url=http://forte.love:15520/img/r]";

		/* 参数获取速度 */
		int times = 100_0000;
		String getParam = "url";
		System.out.println("start-fast....");
		long getTime_fast = getTest_fast(longCq, getParam, times);
		System.out.println("getTime_fast: " + getTime_fast);
		System.out.println("start-map.....");
		long getTime_map = getTest_map(longCq, getParam, times);
		System.out.println("getTime_map: " + getTime_map);
		System.out.printf("%s\t:\t%s\t(%,d)", getTime_fast, getTime_map, times);
		/*
			fast	:	map	(times)
			4378	:	21	(10,000,000)
			4341	:	23	(10,000,000)
			4247	:	23	(10,000,000)
			4512	:	23	(10,000,000)
			4281	:	23	(10,000,000)
			// 这倒是有点令我意外, 可能是因为subString还要创建String实例的原因？

			—————————————————————————— fast中追加了get缓存后

			4117	:	22	(10,000,000)
			3833	:	23	(10,000,000)
			3664	:	24	(10,000,000)
			3617	:	23	(10,000,000)
			3847	:	22	(10,000,000)
			// 依然很拉跨，没有明显改善
		 */


	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long getTest_fast(String code, String get, int times){
		KQCode kqCode = new FastKQCode(code);
		for (int i = 0; i < 100; i++) {
			kqCode.get(get);
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			kqCode.get(get);
		}
		return time() - s;
	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long getTest_map(String code, String get, int times){
		KQCode kqCode = MapKQCode.mapByCode(code);
		for (int i = 0; i < 100; i++) {
			kqCode.get(get);
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			kqCode.get(get);
		}
		return time() - s;
	}






	private static long time(){
		return System.currentTimeMillis();
	}
}

