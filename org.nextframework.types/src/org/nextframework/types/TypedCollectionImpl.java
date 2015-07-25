package org.nextframework.types;

import java.util.Collection;
import java.util.Iterator;

public class TypedCollectionImpl<E> implements TypedValue<E>{

	protected Collection<E> original;
	protected Class<E> type;

	public TypedCollectionImpl(Collection<E> original, Class<E> type) {
		this.original = original;
		this.type = type;
	}

	public int size() {
		return original.size();
	}

	public boolean isEmpty() {
		return original.isEmpty();
	}

	public boolean contains(Object o) {
		return original.contains(o);
	}

	public Iterator<E> iterator() {
		return original.iterator();
	}

	public Object[] toArray() {
		return original.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return original.toArray(a);
	}

	public boolean add(E e) {
		return original.add(e);
	}

	public boolean remove(Object o) {
		return original.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return original.containsAll(c);
	}

	public boolean addAll(Collection<? extends E> c) {
		return original.addAll(c);
	}

	public boolean removeAll(Collection<?> c) {
		return original.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return original.retainAll(c);
	}

	public void clear() {
		original.clear();
	}

	public boolean equals(Object o) {
		return original.equals(o);
	}

	public int hashCode() {
		return original.hashCode();
	}

	@Override
	public Class<E> getCollectionItemType() {
		return type;
	}

	@Override
	public Collection<E> getOriginalCollection() {
		return original;
	}
	
	
}
