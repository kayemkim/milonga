package com.km.milonga.rhino;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.shell.Global;

import com.km.milonga.rhino.debug.RhinoDebuggerFactory;

/**
 * AtmosController TestCase
 * 
 * @author kminkim
 * 
 */
public class RhinoUsingTest {

	static Context cx;
	static Global global;

	static String USER_StringJS_FILE_NAME = "userCode.js";
	static String PRELOAD_JS_FILE_NAME = "preload.js";

	/**
	 * Ready for test
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setup() throws Exception {

		cx = Context.enter();
		global = new Global(cx);
		
		List<String> modulePath = new ArrayList<String>();
		modulePath.add(RhinoUsingTest.class.getResource(".").getPath());
		global.installRequire(cx, modulePath, false);
		
		setDebugger();

		String userCodeFilePath = RhinoUsingTest.class.getResource(
				USER_StringJS_FILE_NAME).getPath();

		String preCodeFilePath = RhinoUsingTest.class.getResource(
				PRELOAD_JS_FILE_NAME).getPath();
		FileReader preReader = new FileReader(new File(preCodeFilePath));
		FileReader jsReader = new FileReader(new File(userCodeFilePath));
		cx.evaluateReader(global, preReader, PRELOAD_JS_FILE_NAME, 1, null);
		cx.evaluateReader(global, jsReader, USER_StringJS_FILE_NAME, 1, null);
	}

	/**
	 * Rhino debugger setting
	 */
	static void setDebugger() throws Exception {
		// optimization level -1 means interpret mode
		cx.setOptimizationLevel(-1);
		Debugger debugger = RhinoDebuggerFactory.create();
		cx.setDebugger(debugger, new Dim.ContextData());
	}

	@Test
	public void testFunction() {
		Object function = ScriptableObject.getProperty(global, "define");
		boolean isNativeFunction = function instanceof NativeFunction;
		assertTrue(isNativeFunction);

		Object testFunction = ScriptableObject.getProperty(global, "testFunc");
		isNativeFunction = testFunction instanceof NativeFunction;
		assertTrue(isNativeFunction);

		Object x = global.get("param", global);
		assertEquals(Scriptable.NOT_FOUND, x);

		NativeFunction nativeFunction = (NativeFunction) testFunction;
		assertNotNull(nativeFunction.getEncodedSource());
		
	}

	@Test
	public void checkJavaCodeInHandler() throws Exception {
		String FILE_NAME = "filetest.txt";

		NativeFunction function = (NativeFunction) ScriptableObject
				.getProperty(global, "define");
		DebuggableScript dScript = function.getDebuggableView();

		assertNotNull(dScript);
		assertEquals(1, dScript.getParamCount());
		assertEquals("param", dScript.getParamOrVarName(0));

		NativeFunction function2 = (NativeFunction) ScriptableObject
				.getProperty(global, "testFunc");
		function2.call(cx, global, global, new Object[] { FILE_NAME });

		File file = new File(FILE_NAME);
		assertTrue(file.exists());
		file.delete();
	}
	
	
	@Test
	public void checkUsingPrototype() throws Exception {
		NativeFunction function = (NativeFunction) ScriptableObject
				.getProperty(global, "returnResponse");
		NativeObject response = (NativeObject) function.call(cx, global,
				global, new Object[] {});
		//assertEquals("Hello, Response!", response.get("content"));

		Scriptable prototype = response.getPrototype();
		NativeObject cookie = (NativeObject) prototype.get("cookie", prototype);

		assertEquals("kmkim", cookie.get("user"));
	}
}
