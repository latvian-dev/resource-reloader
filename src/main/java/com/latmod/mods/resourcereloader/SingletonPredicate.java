package com.latmod.mods.resourcereloader;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class SingletonPredicate<T> implements Predicate<T>
{
	private final T object;

	public SingletonPredicate(T o)
	{
		object = o;
	}

	@Override
	public boolean test(T t)
	{
		return object == t;
	}
}