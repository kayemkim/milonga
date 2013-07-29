package com.km.milonga.rhino;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.NativeFunction;
import org.springframework.web.servlet.ModelAndView;

public class ArgumentChecker {

	private List<Class<? extends ArgumentCheckPolicy>> policyList;

	public void setPolicyList(
			List<Class<? extends ArgumentCheckPolicy>> policyList) {
		this.policyList = policyList;
	}

	public ModelAndView checkAndProcess(NativeFunction atmosHandler,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int argumentsCount = atmosHandler.getLength();

		for (Class<? extends ArgumentCheckPolicy> policy : policyList) {
			Constructor<? extends ArgumentCheckPolicy> policyConst = policy
					.getConstructor(int.class, Object[].class);
			ArgumentCheckPolicy policyObj = policyConst.newInstance(
					argumentsCount, new Object[] { request, response });
			if (policyObj.isValidated(atmosHandler)) {
				return policyObj.apply(atmosHandler, response);
			}
		}

		return null;
	}

}
