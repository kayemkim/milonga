package com.skp.milonga.interpret;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.MultiValueMap;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping;

public class JsSourceWatcher extends WebApplicationObjectSupport implements
		Runnable {

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private boolean trace = false;

	@SuppressWarnings("unchecked")
	public static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public JsSourceWatcher(Path dir, boolean recursive)
			throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();

		register(dir);

		// enable trace after initial registration
		this.trace = true;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				System.out.format("register: %s\n", dir);
			} else {
				if (!dir.equals(prev)) {
					System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		for (;;) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);

				reRegisterHandlerMethods();
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void reRegisterHandlerMethods() {
		AtmosRequestMappingHandlerMapping handlerMapping = getApplicationContext()
				.getBean(AtmosRequestMappingHandlerMapping.class);
		try {
			Field fieldHandlerMethods = AbstractHandlerMethodMapping.class
					.getDeclaredField("handlerMethods");
			fieldHandlerMethods.setAccessible(true);
			Map<RequestMappingInfo, HandlerMethod> map = (Map<RequestMappingInfo, HandlerMethod>) fieldHandlerMethods
					.get(handlerMapping);
			map.clear();

			Field fieldUrlMap = AbstractHandlerMethodMapping.class
					.getDeclaredField("urlMap");
			fieldUrlMap.setAccessible(true);
			MultiValueMap<String, RequestMappingInfo> urlMap = (MultiValueMap<String, RequestMappingInfo>) fieldUrlMap
					.get(handlerMapping);
			urlMap.clear();

			handlerMapping.getHandlerMappingInfoStorage()
					.getHandlerMappingInfos().clear();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handlerMapping.reInitHandlerMethods();
	}

}
