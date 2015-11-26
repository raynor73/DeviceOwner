package com.example.deviceowner.common;

public interface Observable<T> {
	void registerObserver(T observer);
	void unregisterObserver(T observer);
}
