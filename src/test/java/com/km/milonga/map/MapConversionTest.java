package com.km.milonga.map;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

public class MapConversionTest {
	
	public static int count = 0;
	
	@SuppressWarnings("unchecked")
	@Test
	public void convertMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("key_1", "value_1");
		
		List<Object> list = new ArrayList<Object>();
		list.add("element_1");
		list.add("element_2");
		HashMap<String, Object> mapInList = new HashMap<String, Object>();
		mapInList.put("key_map", "value_2");
		list.add(mapInList);
		map.put("key_2", list);
		
		HashMap<String, Object> childMap = new HashMap<String, Object>();
		childMap.put("child_1", "value_child_1");
		List<Object> list2 = new ArrayList<Object>();
		list2.add("abc");
		list2.add("def");
		childMap.put("child_2", list2);
		map.put("key_3", childMap);
		
		ConcurrentHashMap<String, Object> result = convertHashMapToConcurrentHashMap(map);
		assertEquals("value_1", result.get("key_1"));
		assertEquals(LinkedList.class, result.get("key_2").getClass());
		assertEquals(ConcurrentHashMap.class, result.get("key_3").getClass());
		assertEquals(LinkedList.class, ((Map<String, Object>) result.get("key_3")).get("child_2").getClass());
		assertEquals(11, count);
	}
	
	
	public boolean isMap(Object element) {
		return element instanceof Map;
	}
	
	
	public boolean isList(Object element) {
		return element instanceof List;
	}
	
	
	@SuppressWarnings("unchecked")
	public ConcurrentHashMap<String, Object> convertHashMapToConcurrentHashMap(HashMap<String, Object> hashMap) {
		ConcurrentHashMap<String, Object> chm = new ConcurrentHashMap<String, Object>();
		Iterator<Entry<String, Object>> iterator = hashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().getKey();
			Object element = hashMap.get(key);
			if (isMap(element)) {
				chm.put(key, convertHashMapToConcurrentHashMap((HashMap<String, Object>) element));
			} else if(isList(element)) {
				chm.put(key, convertArrayListToLinkedList((ArrayList<Object>)element));
			} else {
				chm.put(key, hashMap.get(key));
			}
			count++;
		}
		return chm;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LinkedList<Object> convertArrayListToLinkedList(ArrayList<Object> arrayList) {
		LinkedList<Object> ll = new LinkedList<Object>();
		
		for (Object element : arrayList) {
			if (isMap(element)) {
				ll.add(convertHashMapToConcurrentHashMap((HashMap<String, Object>)element));
			} else if (isList(element)) {
				ll.add(convertArrayListToLinkedList((ArrayList) element));
			} else {
				ll.add(element);
			}
			count++;
		}
		
		return ll;
	}

}
