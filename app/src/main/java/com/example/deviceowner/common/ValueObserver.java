package com.example.deviceowner.common;

public interface ValueObserver<T> {
	void onChanged(T value);
}
