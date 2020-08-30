/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     PerformanceTest_long_create.java
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
 * 此类测试短CQ下两个类的实例化速度
 *
 * Created by lcy on 2020/8/14.
 * @author lcy
 */
public class PerformanceTest_short_create {
	public static void main(String[] args) {
		String longCq = "[CQ:image,url=http://forte.love:15520/img/r]";

		/* 实例化速度 */
		int times = 10_000_000;
		System.out.println("start-fast....");
		long createTime_fast = createTest_fast(longCq, times);
		System.out.println("createTime_fast: " + createTime_fast);
		System.out.println("start-map.....");
		long createTime_map = createTest_map(longCq, times);
		System.out.println("createTime_map: " + createTime_map);
		System.out.printf("%s\t:\t%s\t(%,d)", createTime_fast, createTime_map, times);
		/*
			fast	:	map	(times)
			541	:	11189	(10,000,000)
			539	:	10485	(10,000,000)
			567	:	10774	(10,000,000)
			553	:	10654	(10,000,000)
			557	:	11369	(10,000,000)
			// 这个依旧没啥悬念
		 */


	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long createTest_fast(String code, int times){
		for (int i = 0; i < 100; i++) {
			FastKQCode.fastByCode(code);
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			FastKQCode.fastByCode(code);
		}
		return time() - s;
	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long createTest_map(String code, int times){
		for (int i = 0; i < 100; i++) {
			MapKQCode.byCode(code);
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			MapKQCode.byCode(code);
		}
		return time() - s;
	}






	private static long time(){
		return System.currentTimeMillis();
	}
}

