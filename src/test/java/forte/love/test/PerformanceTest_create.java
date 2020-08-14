package forte.love.test;

import com.simplerobot.modules.utils.FastKQCode;
import com.simplerobot.modules.utils.KQCodeUtils;
import com.simplerobot.modules.utils.MapKQCode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 测试
 * {@link com.simplerobot.modules.utils.FastKQCode}
 * 与
 * {@link com.simplerobot.modules.utils.MapKQCode}
 * 的性能差异并寻找其各自的优势
 *
 * 此类测试长CQ下两个类的实例化速度
 * 测试用CQ码约有1000个键值对
 *
 * Created by lcy on 2020/8/14.
 * @author lcy
 */
public class PerformanceTest_create {
	public static void main(String[] args) {
		KQCodeUtils instance = KQCodeUtils.getInstance();
		List<String> strList = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			// 1000个paramx=valuex
			strList.add("param" + i + "=value" + i);
		}
		String longCq = instance.toCq("test", strList.toArray(new String[0]));

		/* 实例化速度 */
		int times = 1_0000;
		System.out.println("start-fast....");
		long createTime_fast = createTest_fast(longCq, times);
		System.out.println("createTime_fast: " + createTime_fast);
		System.out.println("start-map.....");
		long createTime_map = createTest_map(longCq, times);
		System.out.println("createTime_map: " + createTime_map);
		System.out.println(createTime_fast + "\t:\t" + createTime_map + "\t("+ times +")");
		/*
			// 1_0000 times
			fast	:	map
			194	:	21289	(10000)
			231	:	24901	(10000)
			134	:	17434	(10000)
			340	:	17591	(10000)
			491	:	18927	(10000)
		 */


	}

	/**
	 * 值获取测试
	 * 开始之前都会有100次的预热
	 */
	public static long createTest_fast(String code, int times){
		for (int i = 0; i < 100; i++) {
			new FastKQCode(code);
		}
		long s = time();
		for (int i = 0; i < times; i++) {
			new FastKQCode(code);
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

