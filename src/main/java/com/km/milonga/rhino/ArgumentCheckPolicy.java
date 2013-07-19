package com.km.milonga.rhino;

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

	public abstract boolean isOk(NativeFunction atmosHandler);

	public abstract ModelAndView result(NativeFunction atmosHandler);

}
