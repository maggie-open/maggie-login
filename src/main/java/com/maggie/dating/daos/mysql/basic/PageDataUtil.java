package com.maggie.dating.daos.mysql.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 
 * PageData中Result转换的工具类
 *
 * @author Eli_Wu
 * @date 2017年2月17日 下午4:14:15 
 * 
 */
public class PageDataUtil {

	/**
	 * findPageBySql默认查询结果为List<Object[]>,
	 * 为了明确定义每个字段的Key，需要转换为List<Map<String, Object>>
	 * key[]为对应结果集每个字段key的数组
	 * 
	 * @author Eli_Wu   
	 * @date 2017年2月17日 下午4:40:05 
	 * @version V1.0   
	 * @param list findPageBySql查询的结果集（格式List<Object[]>）
	 * @param keys 结果集对应每列Key的字符串数组
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map<String, Object>> convertPageDateResult(List list, String[] keys) {
		if(list == null) return null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		int i = 0;
		
		for(Object[] obj : ((List<Object[]>)list)){
			map = new HashMap<String, Object>();
			
			for (i = 0; i < keys.length; i++) {
				map.put(keys[i], obj[i]);
			}
			
			result.add(map);
		}
		return result;
	}
}
