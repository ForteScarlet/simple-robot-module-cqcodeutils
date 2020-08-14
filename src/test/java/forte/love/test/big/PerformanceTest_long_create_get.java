package forte.love.test.big;

import com.simplerobot.modules.utils.FastKQCode;
import com.simplerobot.modules.utils.KQCode;
import com.simplerobot.modules.utils.KQCodeUtils;
import com.simplerobot.modules.utils.MapKQCode;

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
 * 此类测试长CQ下两个类的实例化并获取参数的速度
 * 测试用CQ码约有1000个键值对
 *
 * Created by lcy on 2020/8/14.
 * @author lcy
 */
public class PerformanceTest_long_create_get {
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
		String getParam = "param600";
		System.out.println("start-fast....");
		long getTime_fast = createAndGetTest_fast(longCq, getParam, times);
		System.out.println("getTime_fast: " + getTime_fast);
		System.out.println("start-map.....");
		long getTime_map = createAndGetTest_map(longCq, getParam, times);
		System.out.println("getTime_map: " + getTime_map);
		System.out.println(getTime_fast + "\t:\t" + getTime_map + "\t("+ times +")");
		/*
			fast	:	map	(times)
			752	:	17595	(10000)
			615	:	17806	(10000)
			562	:	16024	(10000)
			800	:	16850	(10000)
			666	:	16281	(10000)
			// 看来第一组组合赛是fast胜出
		 */

	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long createAndGetTest_fast(String code, String get, int times){
		for (int i = 0; i < 100; i++) {
			new FastKQCode(code).get(get);
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			new FastKQCode(code).get(get);
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

