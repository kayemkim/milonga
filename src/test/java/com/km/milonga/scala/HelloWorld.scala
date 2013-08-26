package com.km.milonga.scala

import scala.collection.JavaConversions._
import scala.reflect.runtime.{universe=>ru}
import scala.collection.immutable.List
import scala.reflect.runtime.universe._
import scala.collection.immutable.Map

object HelloWorld {
  def main(args: Array[String]) {
    println("Hello, World!")
    val m = new java.util.HashMap[String, Object]()
    m.put("Foo", java.lang.Boolean.TRUE)
    m.put("Bar", Integer.valueOf(1))

    val m2: Map[String, Any] = m.toMap
    println(m2)
    
    val theType = typeOf[List[Int]]
    println(theType)
    
    val mapType = typeOf[Map[String, Map[String, Any]]]
    
  }
}