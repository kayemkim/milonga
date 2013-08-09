package com.km.milonga.scala

import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.Iterator
import java.util.Map.Entry
import java.util.ArrayList
import java.util.LinkedList
import org.mozilla.javascript.IdScriptableObject

object NativeObjectToScalaMap {
  def main(args: Array[String]) {
    var map: HashMap[String, Object] = new HashMap[String, Object]
    map.put("key_1", "value_1")
    
    var list: ArrayList[Object] = new ArrayList
    list.add("element_1")
    list.add("element_2")
    
    var mapInList: HashMap[String, Object] = new HashMap[String, Object]
    mapInList.put("key_map", "value_2")
    list.add(mapInList)
    map.put("key_2", list)
    
    var childMap: HashMap[String, Object] = new HashMap[String, Object]
    childMap.put("child_1", "value_child_1")
    map.put("key_3", childMap)
    
    var result: Map[String, Object] = convertHashMapToScalaMap(map)
    
    System.out.println(result.get("key_1"))
    System.out.println(result.get("key_2").getClass() + " : " + result.get("key_2"))
    System.out.println(result.get("key_3").getClass() + " : " + result.get("key_3"))
    
    for (inx <- 0 until 4) {
      println(inx)
    }
  }
  
/*  def convertHashMapToScalaMap(nativeObject: java.util.Map[String, Object]): Map[String, Object] = {
    var sm: Map[String, Object] = Map()
    val iterator: Iterator[Entry[String, Object]] = nativeObject.entrySet().iterator()
    while (iterator.hasNext()) {
      val key: String = iterator.next().getKey()
      val element: Object = nativeObject.get(key);
      if (element.isInstanceOf[java.util.Map[String, Object]]) {
        sm += (key -> convertHashMapToScalaMap(element.asInstanceOf[java.util.Map[String, Object]]))
      } else if (element.isInstanceOf[java.util.List[Object]]) {
        sm += (key -> convertArrayListToScalaList(element.asInstanceOf[java.util.List[Object]]))
      } else {
        sm += (key -> nativeObject.get(key))
      }
    }
    return sm
  }
  
  def convertArrayListToScalaList(nativeArray: java.util.List[Object]): List[Object] = {
    var sl: List[Object] = List()
    val nativeArraySize: Int = nativeArray.size();
    
    for (inx <- 0 until nativeArraySize) {
      val element: Object = nativeArray.get(inx)
      if(element.isInstanceOf[HashMap[String, Object]]) {
        sl :+ convertHashMapToScalaMap(element.asInstanceOf[java.util.Map[String, Object]])
      } else if (element.isInstanceOf[ArrayList[Object]]) {
        sl :+ convertArrayListToScalaList(element.asInstanceOf[java.util.List[Object]])
      } else {
        sl :+ nativeArray.get(inx)
      }
    }
    return sl
  }*/
  

  def convertHashMapToScalaMap(nativeObject: java.util.Map[String, Object]): Map[String, Object] = {
    var sm: Map[String, Object] = Map()
    var iterator: Iterator[Entry[String, Object]] = nativeObject.entrySet().iterator()
    while (iterator.hasNext()) {
      var key: String = iterator.next().getKey()
      var element: Object = nativeObject.get(key);
      if (element.isInstanceOf[java.util.Map[String, Object]]) {
        sm += (key -> convertHashMapToScalaMap(element.asInstanceOf[java.util.Map[String, Object]]))
      } else if (element.isInstanceOf[java.util.List[Object]]) {
        sm += (key -> convertArrayListToScalaList(element.asInstanceOf[java.util.List[Object]]))
      } else {
        sm += (key -> nativeObject.get(key))
      }
    }
    return sm
  }
  
  def convertArrayListToScalaList(nativeArray: java.util.List[Object]): List[Object] = {
    var sl: List[Object] = List()
    var nativeArraySize: Int = nativeArray.size();
    
    for (inx <- 0 until nativeArraySize) {
      var element: Object = nativeArray.get(inx)
      if(element.isInstanceOf[HashMap[String, Object]]) {
        sl :+ convertHashMapToScalaMap(element.asInstanceOf[java.util.Map[String, Object]])
      } else if (element.isInstanceOf[ArrayList[Object]]) {
        sl :+ convertArrayListToScalaList(element.asInstanceOf[java.util.List[Object]])
      } else {
        sl :+ nativeArray.get(inx)
      }
    }
    return sl
  }
}