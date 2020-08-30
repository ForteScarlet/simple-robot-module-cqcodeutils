/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     PerformanceTest_long_toString.java
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
public class PerformanceTest_short_toString {
	public static void main(String[] args) {
		String longCq = "[CQ:image,url=http://forte.love:15520/img/r]";

		/* 参数获取速度 */
		int times = 10_000_000;
		System.out.println("start-fast....");
		long getTime_fast = toStringTest_fast(longCq, times);
		System.out.println("getTime_fast: " + getTime_fast);
		System.out.println("start-map.....");
		long getTime_map = toStringTest_map(longCq, times);
		System.out.println("getTime_map: " + getTime_map);
		System.out.printf("%s\t:\t%s\t(%,d)", getTime_fast, getTime_map, times);
		/*
			fast	:	map	(times)
			3	:	5502	(10,000,000)
			2	:	5443	(10,000,000)
			2	:	5251	(10,000,000)
			2	:	5118	(10,000,000)
			2	:	5118	(10,000,000)

			// 这个依旧没啥悬念
		 */


	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long toStringTest_fast(String code, int times){
		KQCode kqCode = FastKQCode.byCode(code);
		for (int i = 0; i < 100; i++) {
			kqCode.toString();
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			kqCode.toString();
		}
		return time() - s;
	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long toStringTest_map(String code, int times){
		KQCode kqCode = MapKQCode.byCode(code);
		for (int i = 0; i < 100; i++) {
			kqCode.toString();
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			kqCode.toString();
		}
		return time() - s;
	}






	private static long time(){
		return System.currentTimeMillis();
	}
}

