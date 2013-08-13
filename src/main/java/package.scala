import org.mozilla.javascript.NativeObject
import org.mozilla.javascript.IdScriptableObject

package object Atmos {
  
  type Json = java.util.Map[String, NativeObject]
  //type Json = java.util.Map[String, Any]
  
  //type Smap[Smap] = Map[String, Map[String, Smap]]
  type Smap = Map[String, Map[String, Map[String, Map[String, Map[String, Map[String, _]]]]]]
  
  
  
  class L {
    type L = Map[String, this.type]
  }
}

/*class Smap extends Map[String, Smap] {
  
}*/




