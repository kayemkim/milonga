package com.km.milonga.rhino;

import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.springframework.web.servlet.ModelAndView;

public abstract class ArgumentCheckPolicy {

	protected int argumentsCount;

	protected Object[] args;

	protected Context context = Context.enter();

	protected Scriptable scope = context.initStandardObjects();

	public ArgumentCheckPolicy(int argumentsCount, Object[] args) {
		this.argumentsCount = argumentsCount; 
		this.args = args;
	}

	public abstract boolean isValidated(NativeFunction atmosHandler);

	public abstract ModelAndView apply(NativeFunction atmosHandler, HttpServletResponse response);

}
