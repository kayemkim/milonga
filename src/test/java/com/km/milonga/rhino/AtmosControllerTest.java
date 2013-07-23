package com.km.milonga.rhino;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.shell.Global;

import com.km.milonga.rhino.debug.RhinoDebuggerFactory;

public class AtmosControllerTest {
	
	static Context cx;
	static Scriptable scope;
	static Global global;
	
	@BeforeClass
	public static void setup() throws Exception {
		
		cx = Context.enter();
		scope = cx.initStandardObjects();
		global = new Global(cx);
		
		setDebugger();
		
		String userCodeFilePath = AtmosControllerTest.class
				.getResource("userCode.js").getPath();
		
		FileReader jsReader = new FileReader(new File(userCodeFilePath));
		cx.evaluateReader(scope, jsReader, "userCode.js", 1, null);
	}
	
	public static void setDebugger() throws Exception {
		// optimization level -1 means interpret mode
		cx.setOptimizationLevel(-1);
		Debugger debugger = RhinoDebuggerFactory.create();
		cx.setDebugger(debugger, new Dim.ContextData());
	}
	
	@Test
	public void testFunction() {
		Object function = ScriptableObject.getProperty(scope, "define");
		boolean isNativeFunction = function instanceof NativeFunction;
		assertTrue(isNativeFunction);
		
		Object testFunction = ScriptableObject.getProperty(scope, "testFunc");
		isNativeFunction = testFunction instanceof NativeFunction;
		assertTrue(isNativeFunction);
		
		Object x = scope.get("param", scope);
		assertEquals(Scriptable.NOT_FOUND, x);
		
		NativeFunction nativeFunction = (NativeFunction) testFunction;
		assertNotNull(nativeFunction.getEncodedSource());
	}
	
	
	@Test
	public void testInterpreter() throws Exception {
		NativeFunction function = (NativeFunction) ScriptableObject.getProperty(scope, "define");
		DebuggableScript dScript = function.getDebuggableView();
		
		assertNotNull(dScript);
		assertEquals(1, dScript.getParamCount());
		assertEquals("param", dScript.getParamOrVarName(0));
	}
	
	@Test
	public void regexTest() {
		String source = "ab/ W.request$hilsresponse$#%%";
		assertTrue(source.matches(".+"));
	}
}
