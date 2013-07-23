package com.km.milonga.rhino.debug;

import java.lang.reflect.Constructor;

import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.GuiCallback;
import org.mozilla.javascript.tools.debugger.Dim.SourceInfo;
import org.mozilla.javascript.tools.debugger.Dim.StackFrame;

public class RhinoDebuggerFactory {
	
	public static Debugger create() throws Exception {
		Class<?> clazz = Class
				.forName("org.mozilla.javascript.tools.debugger.Dim$DimIProxy");
		Constructor<?> constructor = clazz.getDeclaredConstructor(Dim.class,
				int.class);
		constructor.setAccessible(true);
		
		Dim dim = new Dim();
		dim.setGuiCallback(new EmptyGuiCallback());
		
		// debug type : 0
		Debugger debugger = (Debugger) constructor.newInstance(dim, 0);
		return debugger;
	}
	
	
	static class EmptyGuiCallback implements GuiCallback {

		@Override
		public void updateSourceText(SourceInfo sourceInfo) {

		}

		@Override
		public void enterInterrupt(StackFrame lastFrame, String threadTitle,
				String alertMessage) {

		}

		@Override
		public boolean isGuiEventThread() {
			return false;
		}

		@Override
		public void dispatchNextGuiEvent() throws InterruptedException {

		}

	}

}
