package esn.classes;

import java.lang.ref.SoftReference;
import java.util.HashMap;

public class MemoryCache<T> {
	private HashMap<String, SoftReference<T>> cache = new HashMap<String, SoftReference<T>>();

	public T get(String id) {
		if (!cache.containsKey(id))
			return null;
		SoftReference<T> ref = cache.get(id);
		return ref.get();
	}

	public void put(String id, T data) {
		cache.put(id, new SoftReference<T>(data));
	}

	public void clear() {
		cache.clear();
	}
}