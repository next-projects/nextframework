package org.nextframework.test.summary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nextframework.summary.aggregator.Aggregator;

@SuppressWarnings("all")
public class TestMapAggregator<E extends Map> implements Aggregator<E> {

	@Override
	public E aggreagte(Map n1, Map n2) {
		if (n1 == null) {
			n1 = new HashMap();
		}
		if (n2 == null) {
			n2 = new HashMap();
		}
		E newValue = (E) new HashMap();
		newValue.putAll(n1);
		Set keySet = n2.keySet();
		for (Object key : keySet) {
			if (newValue.containsKey(key)) {
				newValue.put(key, (Double) n2.get(key) + (Double) newValue.get(key));
			} else {
				newValue.put(key, n2.get(key));
			}
		}
		return newValue;
	}

}
