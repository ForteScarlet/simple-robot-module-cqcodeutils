package forte.love.test.big;

import com.simplerobot.modules.utils.codes.FastKQCode;
import com.simplerobot.modules.utils.KQCode;
import com.simplerobot.modules.utils.KQCodeUtils;
import com.simplerobot.modules.utils.codes.MapKQCode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 测试
 * {@link FastKQCode}
 * 与
 * {@link MapKQCode}
 * 的性能差异并寻找其各自的优势
 *
 * 此类测试长CQ下两个类的参数获取速度
 * 测试用CQ码约有1000个键值对
 *
 * Created by lcy on 2020/8/14.
 * @author lcy
 */
public class PerformanceTest_long_toString {
	public static void main(String[] args) {
		KQCodeUtils instance = KQCodeUtils.getInstance();
		List<String> strList = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			// 1000个paramx=valuex
			strList.add("param" + i + "=value" + i);
		}
		String longCq = instance.toCq("test", strList.toArray(new String[0]));

		/* 参数获取速度 */
		int times = 1_0000;
		System.out.println("start-fast....");
		long getTime_fast = toStringTest_fast(longCq, times);
		System.out.println("getTime_fast: " + getTime_fast);
		System.out.println("start-map.....");
		long getTime_map = toStringTest_map(longCq, times);
		System.out.println("getTime_map: " + getTime_map);
		System.out.println(getTime_fast + "\t:\t" + getTime_map + "\t("+ times +")");
		/*
			fast	:	map	(times)
			1	:	9110	(10000)
			0	:	8923	(10000)
			0	:	9329	(10000)
			1	:	9126	(10000)
			1	:	10365	(10000)
			// 这没啥悬念了，fast不存在toString转化，没有消耗
			// map则接近了1毫秒才toString出来一个

			———————————————————————— MapKQCode优化了ToString后 (家里的台式机)
			0	:	0	(10000)
			0	:	1	(10000)
			1	:	0	(10000)
			0	:	0	(10000)

			// 因为对toString做了记录，现在toString的效率基本一致
		 */


	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long toStringTest_fast(String code, int times){
		KQCode kqCode = new FastKQCode(code);
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

