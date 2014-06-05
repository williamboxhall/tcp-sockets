package org.example.infrastructure;

public interface Consumer<T> {
	public void accept(T t);
}
