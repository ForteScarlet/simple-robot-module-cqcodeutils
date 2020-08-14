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
 * 此类测试长CQ下两个类的参数获取速度
 * 测试用CQ码约有1000个键值对
 *
 * Created by lcy on 2020/8/14.
 * @author lcy
 */
public class PerformanceTest_long_get {
	public static void main(String[] args) {
		KQCodeUtils instance = KQCodeUtils.getInstance();
		List<String> strList = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			// 1000个paramx=valuex
			strList.add("param" + i + "=value" + i);
		}
		String longCq = instance.toCq("test", strList.toArray(new String[0]));

		/* 参数获取速度 */
		int times = 100_0000;
		String getParam = "param600";
		System.out.println("start-fast....");
		long getTime_fast = getTest_fast(longCq, getParam, times);
		System.out.println("getTime_fast: " + getTime_fast);
		System.out.println("start-map.....");
		long getTime_map = getTest_map(longCq, getParam, times);
		System.out.println("getTime_map: " + getTime_map);
		System.out.println(getTime_fast + "\t:\t" + getTime_map + "\t("+ times +")");
		/*
			fast	:	map	(times)
			17438	:	32	(1000000)
			17425	:	32	(1000000)
			17434	:	35	(1000000)
			17726	:	28	(1000000)
			17316	:	33	(1000000)
			—————————————————————————— (预热的时候不再创建对象实例)
			18644	:	54	(1000000)
			18348	:	51	(1000000)
			17672	:	53	(1000000)
			21374	:	86	(1000000)
			17514	:	79	(1000000)
			// 不愧是map，在获取上有绝对优势
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
		KQCode kqCode = MapKQCode.byCode(code);
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

