package com.skp.milonga.template

import java.util.Enumeration

import scala.collection.JavaConverters.asJavaEnumerationConverter

import org.fusesource.scalate.servlet.Config.servletConfig2Config
import org.fusesource.scalate.servlet.ServletTemplateEngine
import org.fusesource.scalate.spring.view.ScalateView
import org.springframework.web.context.ServletConfigAware
import org.springframework.web.servlet.view.AbstractTemplateViewResolver
import org.springframework.web.servlet.view.AbstractUrlBasedView

import javax.servlet.ServletConfig
import javax.servlet.ServletContext

class AtmosScalateViewResolver() extends AbstractTemplateViewResolver with ServletConfigAware {

  var templateEngine: ServletTemplateEngine = _

  override def setServletConfig(config: ServletConfig) {
    val ste = new ServletTemplateEngine(config)
    ServletTemplateEngine(config.getServletContext()) = ste
    templateEngine = ste;
  }

  override def initServletContext(servletContext: ServletContext) {
    super.initServletContext(servletContext);

    setServletConfig(new ServletConfig() {
      def getServletName(): String = "unknown"
      def getServletContext(): ServletContext = servletContext
      def getInitParameterNames(): Enumeration[String] = List[String]().iterator.asJavaEnumeration
      def getInitParameter(s: String) = null;
    });
  }

  setViewClass(requiredViewClass())

  override def requiredViewClass(): java.lang.Class[_] = classOf[org.fusesource.scalate.spring.view.ScalateView]

  override def buildView(viewName: String): AbstractUrlBasedView = {
    var view: AbstractScalateView = null

    if (viewName == "view") {
      view = new AtmosScalateView
    } else if (viewName.startsWith("layout:")) {
      val urlView = new ScalateUrlView with LayoutScalateRenderStrategy
      urlView.setUrl(getPrefix() + viewName.substring("layout:".length()) + getSuffix())
      view = urlView
    } else {
      val urlView = new ScalateUrlView with DefaultScalateRenderStrategy
      urlView.setUrl(getPrefix() + viewName + getSuffix())
      view = urlView
    }

    view.templateEngine = templateEngine

    val contentType = getContentType
    if (contentType != null) {
      view.setContentType(contentType)
    }

    view.asInstanceOf[AbstractUrlBasedView]
  }

}