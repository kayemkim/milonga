package scala.map

import org.fusesource.scalate.RenderContext.capture

object Cheese {
  def foo(productId: Int)(body : => Unit) =
    <a href={"products/" + productId} title="Product link">capture(body)</a>
}