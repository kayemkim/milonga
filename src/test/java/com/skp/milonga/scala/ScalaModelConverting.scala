package com.skp.milonga.scala

import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.Iterator
import java.util.Map.Entry
import java.util.ArrayList
import java.util.LinkedList

object ScalaModelConverting {
  def main(args: Array[String]) {
    val map: HashMap[String, Object] = new HashMap[String, Object]
    map.put("key_1", "value_1")
    
    val list: ArrayList[Object] = new ArrayList
    list.add("element_1")
    list.add("element_2")
    
    val mapInList: HashMap[String, Object] = new HashMap[String, Object]
    mapInList.put("key_map", "value_2")
    list.add(mapInList)
    map.put("key_2", list)
    
    val childMap: HashMap[String, Object] = new HashMap[String, Object]
    childMap.put("child_1", "value_child_1")
    map.put("key_3", childMap)
    
    var result: Map[String, Object] = convertHashMapToScalaMap(map)
    
    System.out.println(result.get("key_1"))
    System.out.println(result.get("key_2").getClass() + " : " + result.get("key_2"))
    System.out.println(result.get("key_3").getClass() + " : " + result.get("key_3"))
  }
  
  def convertHashMapToScalaMap(hashMap: HashMap[String, Object]): Map[String, Object] = {
    var sm: Map[String, Object] = Map()
    val iterator: Iterator[Entry[String, Object]] = hashMap.entrySet().iterator()
    while (iterator.hasNext()) {
      val key: String = iterator.next().getKey()
      val element: Object = hashMap.get(key);
      if (element.isInstanceOf[HashMap[String, Object]]) {
        sm += (key -> convertHashMapToScalaMap(element.asInstanceOf[HashMap[String, Object]]))
      } else if (element.isInstanceOf[ArrayList[Object]]) {
        sm += (key -> convertArrayListToScalaList(element.asInstanceOf[ArrayList[Object]]))
      } else {
        sm += (key -> hashMap.get(key))
      }
    }
    return sm
  }
  
  def convertArrayListToScalaList(arrayList: ArrayList[Object]): List[Object] = {
    var sl: List[Object] = List()
    val arrayListSize: Int = arrayList.size();
    
    for (inx <- 0 until arrayListSize) {
      val element: Object = arrayList.get(inx)
      if(element.isInstanceOf[HashMap[String, Object]]) {
        sl :+ convertHashMapToScalaMap(element.asInstanceOf[HashMap[String, Object]])
      } else if (element.isInstanceOf[ArrayList[Object]]) {
        sl :+ convertArrayListToScalaList(element.asInstanceOf[ArrayList[Object]])
      } else {
        sl :+ arrayList.get(inx)
      }
    }
    return sl
  }
  
  

}