package com.km.milonga.template

import _root_.java.util.Locale
import _root_.javax.servlet.ServletConfig
import _root_.javax.servlet.http.HttpServletRequest
import _root_.javax.servlet.http.HttpServletResponse
import _root_.org.fusesource.scalate.RenderContext
import _root_.org.fusesource.scalate.servlet.ServletRenderContext
import _root_.org.fusesource.scalate.servlet.ServletTemplateEngine
import _root_.org.springframework.web.context.ServletConfigAware
import _root_.scala.collection.JavaConversions._
import _root_.org.fusesource.scalate.TemplateException
import _root_.org.springframework.web.servlet.view.{ AbstractView, AbstractTemplateView }
import _root_.org.slf4j.LoggerFactory
import org.fusesource.scalate.util.ResourceNotFoundException
import com.km.milonga.scala.NativeObjectToScalaMap

trait ScalateRenderStrategy {
  protected val log = LoggerFactory.getLogger(getClass)
  def render(context: ServletRenderContext, model: Map[String, Any]);
}

trait LayoutScalateRenderStrategy extends AbstractTemplateView with ScalateRenderStrategy {
  def templateEngine: ServletTemplateEngine
  def render(context: ServletRenderContext, model: Map[String, Any]) {
    log.debug("Rendering view with name '" + getUrl + "' with model " + model)
    for ((key, value) <- model) {
      context.attributes(key) = value
    }
    templateEngine.layout(getUrl, context)
  }
}

trait DefaultScalateRenderStrategy extends AbstractTemplateView with ScalateRenderStrategy {
  override def render(context: ServletRenderContext, model: Map[String, Any]) {
    log.debug("Rendering view with name '" + getUrl + "' with model " + model)
    context.render(getUrl, model)
  }
}

trait ViewScalateRenderStrategy extends ScalateRenderStrategy {
  override def render(context: ServletRenderContext, model: Map[String, Any]) {
    log.debug("Rendering with model " + model)
    val it = model.get("it")
    if (it.isEmpty)
      throw new TemplateException("No 'it' model object specified.  Cannot render request")
    context.view(it.get.asInstanceOf[AnyRef])
  }
}

trait AbstractScalateView extends AbstractView {
  var templateEngine: ServletTemplateEngine = _;
  def checkResource(locale: Locale): Boolean;
}

class ScalateUrlView extends AbstractTemplateView with AbstractScalateView
  with LayoutScalateRenderStrategy {

  override def renderMergedTemplateModel(model: java.util.Map[String, Object],
    request: HttpServletRequest,
    response: HttpServletResponse): Unit = {

    val context = new ServletRenderContext(templateEngine, request, response, getServletContext)
    RenderContext.using(context) {
      // working now ...
      /*var dataMap: Map[String, Any] = Map()
      dataMap = NativeObjectToScalaMap.convertHashMapToScalaMap(model.asInstanceOf[java.util.Map[String, Object]])
      render(context, dataMap)*/
      render(context, model.asInstanceOf[java.util.Map[String, Any]].toMap)
    }
  }

  override def checkResource(locale: Locale): Boolean = try {
    log.debug("Checking for resource " + getUrl)
    templateEngine.load(getUrl)
    true
  } catch {
    case e: ResourceNotFoundException => {
      log.info("Could not find resource " + getUrl);
      false
    }
  }

}

class AtmosScalateView extends AbstractScalateView with ViewScalateRenderStrategy {

  override def checkResource(locale: Locale) = true;

  override def renderMergedOutputModel(model: java.util.Map[String, Object], 
    request: HttpServletRequest, 
    response: HttpServletResponse): Unit = {

    val context = new ServletRenderContext(templateEngine, request, response, getServletContext)
    RenderContext.using(context) {
      render(context, model.asInstanceOf[java.util.Map[String, Any]].toMap)
    }
  }

}