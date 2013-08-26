package com.km.milonga.scala

import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.Iterator
import java.util.Map.Entry
import java.util.Map
import java.util.List
import java.util.ArrayList
import java.util.LinkedList

object ModelConverting {
  def main(args: Array[String]) {
    val map: HashMap[String, Object] = new HashMap[String, Object]
    map.put("key_1", "value_1")
    
    val list: List[Object] = new ArrayList
    list.add("element_1")
    list.add("element_2")
    
    val mapInList: HashMap[String, Object] = new HashMap[String, Object]
    mapInList.put("key_map", "value_2")
    list.add(mapInList)
    map.put("key_2", list)
    
    val childMap: HashMap[String, Object] = new HashMap[String, Object]
    childMap.put("child_1", "value_child_1")
    map.put("key_3", childMap)
    
    var result: ConcurrentHashMap[String, Object] = convert(map)
    
    System.out.println(result.get("key_1"))
    System.out.println(result.get("key_2").getClass() + " : " + result.get("key_2"))
    System.out.println(result.get("key_3").getClass() + " : " + result.get("key_3"))
  }
  
  def convert(input: HashMap[String, Object]): ConcurrentHashMap[String, Object] = {
    var output: ConcurrentHashMap[String, Object] = new ConcurrentHashMap
    val iterator: Iterator[Entry[String, Object]] = input.entrySet().iterator()
    while (iterator.hasNext()) {
      val key: String = iterator.next().getKey()
      val element: Object = input.get(key)
      if (element.isInstanceOf[Map[String, Object]]) {
        output.put(key, convertHashMapToConcurrentHashMap(element.asInstanceOf[HashMap[String, Object]]))
        convert(element.asInstanceOf[HashMap[String, Object]])
      } else if (element.isInstanceOf[List[Object]]) {
        output.put(key, convertArrayListToLinkedList(element.asInstanceOf[ArrayList[Object]]))
      } else {
        output.put(key, input.get(key))
      }
    }
    return output
  }
  
  
  def convertHashMapToConcurrentHashMap(hashMap: HashMap[String, Object]): ConcurrentHashMap[String, Object] = {
    var chm: ConcurrentHashMap[String, Object] = new ConcurrentHashMap
    val iterator: Iterator[Entry[String, Object]] = hashMap.entrySet().iterator()
    while (iterator.hasNext()) {
      val key: String = iterator.next().getKey()
      chm.put(key, hashMap.get(key))
    }
    return chm
  }
  
  def convertArrayListToLinkedList(arrayList: ArrayList[Object]): LinkedList[Object] = {
    var linkedList: LinkedList[Object] = new LinkedList
    
    val arrayListSize: Int = arrayList.size();
    for (inx <- 0 until arrayListSize) {
      linkedList.add(arrayList.get(inx))
    }
    return linkedList
  }
  
  

}