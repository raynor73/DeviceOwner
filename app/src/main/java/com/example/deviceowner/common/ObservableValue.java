package com.example.deviceowner.common;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

public class ObservableValue<T> implements Observable<ValueObserver<T>> {
	private final Set<ValueObserver<T>> mObservers = Sets.newHashSet();
	private final boolean mNotifyAfterAdd;
	private T mValue;

	public ObservableValue(final T value, final boolean notifyAfterAdd) {
		mNotifyAfterAdd = notifyAfterAdd;
		mValue = value;
	}

	@Override
	public void registerObserver(final ValueObserver<T> observer) {
		if (mObservers.contains(observer)) {
			throw new IllegalArgumentException("Observer " + observer + " already registered");
		}

		mObservers.add(observer);
		if (mNotifyAfterAdd) {
			observer.onChanged(mValue);
		}
	}

	@Override
	public void unregisterObserver(final ValueObserver<T> observer) {

	}

	public T getValue() {
		return mValue;
	}

	public void setValue(final T value) {
		if (mValue == value) {
			return;
		}

		mValue = value;
		notifyChanged();
	}

	private void notifyChanged() {
		final Set<ValueObserver<T>> observersCopy = ImmutableSet.copyOf(mObservers);
		for (final ValueObserver<T> observer : observersCopy) {
			if (mObservers.contains(observer)) {
				observer.onChanged(mValue);
			}
		}
	}
}
