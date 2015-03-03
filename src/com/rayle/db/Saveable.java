package com.rayle.db;

public interface Saveable<T> {

	public T load();
	public void save();
	
}
