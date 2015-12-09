/*******************************************************************************
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

package org.eclipse.collections.impl.stack.immutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Iterator;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.partition.stack.PartitionImmutableStack;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.ImmutableStack;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.stack.StackIterable;
import org.eclipse.collections.api.stack.primitive.ImmutableBooleanStack;
import org.eclipse.collections.api.stack.primitive.ImmutableByteStack;
import org.eclipse.collections.api.stack.primitive.ImmutableCharStack;
import org.eclipse.collections.api.stack.primitive.ImmutableDoubleStack;
import org.eclipse.collections.api.stack.primitive.ImmutableFloatStack;
import org.eclipse.collections.api.stack.primitive.ImmutableIntStack;
import org.eclipse.collections.api.stack.primitive.ImmutableLongStack;
import org.eclipse.collections.api.stack.primitive.ImmutableShortStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.procedure.MutatingAggregationProcedure;
import org.eclipse.collections.impl.block.procedure.NonMutatingAggregationProcedure;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.stack.PartitionArrayStack;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.stack.mutable.primitive.BooleanArrayStack;
import org.eclipse.collections.impl.stack.mutable.primitive.ByteArrayStack;
import org.eclipse.collections.impl.stack.mutable.primitive.CharArrayStack;
import org.eclipse.collections.impl.stack.mutable.primitive.DoubleArrayStack;
import org.eclipse.collections.impl.stack.mutable.primitive.FloatArrayStack;
import org.eclipse.collections.impl.stack.mutable.primitive.IntArrayStack;
import org.eclipse.collections.impl.stack.mutable.primitive.LongArrayStack;
import org.eclipse.collections.impl.stack.mutable.primitive.ShortArrayStack;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;
import net.jcip.annotations.Immutable;

/**
 * The immutable equivalent of ArrayStack. Wraps a FastList.
 */
@Immutable
final class ImmutableArrayStack<T> implements ImmutableStack<T>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final FastList<T> delegate;

    private ImmutableArrayStack(T[] newElements)
    {
        this.delegate = FastList.newListWith(newElements);
    }

    private ImmutableArrayStack(FastList<T> newElements)
    {
        this.delegate = newElements;
    }

    public static <T> ImmutableStack<T> newStack()
    {
        return new ImmutableArrayStack<T>(FastList.<T>newList());
    }

    public static <E> ImmutableArrayStack<E> newStack(Iterable<? extends E> iterable)
    {
        return new ImmutableArrayStack<E>((E[]) Iterate.toArray(iterable));
    }

    public static <E> ImmutableArrayStack<E> newStackWith(E... elements)
    {
        return new ImmutableArrayStack<E>(elements.clone());
    }

    public static <T> ImmutableArrayStack<T> newStackFromTopToBottom(Iterable<? extends T> items)
    {
        return new ImmutableArrayStack<T>(FastList.newList(items).reverseThis());
    }

    public static <T> ImmutableArrayStack<T> newStackFromTopToBottom(T... items)
    {
        return new ImmutableArrayStack<T>(FastList.newListWith(items).reverseThis());
    }

    public ImmutableStack<T> push(T item)
    {
        FastList<T> newDelegate = FastList.newList(this.delegate);
        newDelegate.add(item);
        return new ImmutableArrayStack<T>(newDelegate);
    }

    public ImmutableStack<T> pop()
    {
        this.checkEmptyStack();
        FastList<T> newDelegate = FastList.newList(this.delegate);
        newDelegate.remove(this.delegate.size() - 1);
        return new ImmutableArrayStack<T>(newDelegate);
    }

    public ImmutableStack<T> pop(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return this;
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        FastList<T> newDelegate = this.delegate.clone();
        while (count > 0)
        {
            newDelegate.remove(this.delegate.size() - 1);
            count--;
        }
        return new ImmutableArrayStack<T>(newDelegate);
    }

    public T peek()
    {
        this.checkEmptyStack();
        return this.delegate.getLast();
    }

    private void checkEmptyStack()
    {
        if (this.delegate.isEmpty())
        {
            throw new EmptyStackException();
        }
    }

    public ListIterable<T> peek(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return FastList.newList();
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        return FastList.newList(this.asLazy().take(count));
    }

    private boolean checkZeroCount(int count)
    {
        return count == 0;
    }

    private void checkSizeLessThanCount(int count)
    {
        if (this.delegate.size() < count)
        {
            throw new IllegalArgumentException("Count must be less than size: Count = " + count + " Size = " + this.delegate.size());
        }
    }

    private void checkSizeLessThanOrEqualToIndex(int index)
    {
        if (this.delegate.size() <= index)
        {
            throw new IllegalArgumentException("Count must be less than size: Count = " + index + " Size = " + this.delegate.size());
        }
    }

    private void checkNegativeCount(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be positive but was " + count);
        }
    }

    public T peekAt(int index)
    {
        this.checkNegativeCount(index);
        this.checkEmptyStack();
        this.checkSizeLessThanOrEqualToIndex(index);
        return this.delegate.get(this.delegate.size() - 1 - index);
    }

    public T getFirst()
    {
        return this.peek();
    }

    public T getLast()
    {
        throw new UnsupportedOperationException("Cannot call getLast() on " + this.getClass().getSimpleName());
    }

    public boolean contains(Object object)
    {
        return this.delegate.asReversed().contains(object);
    }

    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.delegate.asReversed().containsAllIterable(source);
    }

    public boolean containsAll(Collection<?> source)
    {
        return this.delegate.asReversed().containsAll(source);
    }

    public boolean containsAllArguments(Object... elements)
    {
        return this.delegate.asReversed().containsAllArguments(elements);
    }

    public MutableStack<T> toStack()
    {
        return ArrayStack.newStackFromTopToBottom(this);
    }

    public ImmutableStack<T> select(Predicate<? super T> predicate)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().select(predicate).toList());
    }

    public <P> ImmutableStack<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return this.delegate.asReversed().select(predicate, target);
    }

    public <P, R extends Collection<T>> R selectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().selectWith(predicate, parameter, targetCollection);
    }

    public ImmutableStack<T> reject(Predicate<? super T> predicate)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().reject(predicate).toList());
    }

    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return this.delegate.asReversed().reject(predicate, target);
    }

    public <P> ImmutableStack<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    public <P, R extends Collection<T>> R rejectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().rejectWith(predicate, parameter, targetCollection);
    }

    public PartitionImmutableStack<T> partition(Predicate<? super T> predicate)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<T>();
        this.delegate.asReversed().forEach(new PartitionArrayStack.PartitionProcedure<T>(predicate, partitionMutableStack));
        return partitionMutableStack.toImmutable();
    }

    public <P> PartitionImmutableStack<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<T>();
        this.delegate.asReversed().forEach(new PartitionArrayStack.PartitionPredicate2Procedure<T, P>(predicate, parameter, partitionMutableStack));
        return partitionMutableStack.toImmutable();
    }

    public <S> RichIterable<S> selectInstancesOf(Class<S> clazz)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().selectInstancesOf(clazz).toList());
    }

    public <V> ImmutableStack<V> collect(Function<? super T, ? extends V> function)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collect(function));
    }

    public ImmutableBooleanStack collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        return BooleanArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectBoolean(booleanFunction)).toImmutable();
    }

    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        return this.delegate.asReversed().collectBoolean(booleanFunction, target);
    }

    public ImmutableByteStack collectByte(ByteFunction<? super T> byteFunction)
    {
        return ByteArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectByte(byteFunction)).toImmutable();
    }

    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        return this.delegate.asReversed().collectByte(byteFunction, target);
    }

    public ImmutableCharStack collectChar(CharFunction<? super T> charFunction)
    {
        return CharArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectChar(charFunction)).toImmutable();
    }

    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        return this.delegate.asReversed().collectChar(charFunction, target);
    }

    public ImmutableDoubleStack collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        return DoubleArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectDouble(doubleFunction)).toImmutable();
    }

    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        return this.delegate.asReversed().collectDouble(doubleFunction, target);
    }

    public ImmutableFloatStack collectFloat(FloatFunction<? super T> floatFunction)
    {
        return FloatArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectFloat(floatFunction)).toImmutable();
    }

    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        return this.delegate.asReversed().collectFloat(floatFunction, target);
    }

    public ImmutableIntStack collectInt(IntFunction<? super T> intFunction)
    {
        return IntArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectInt(intFunction)).toImmutable();
    }

    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        return this.delegate.asReversed().collectInt(intFunction, target);
    }

    public ImmutableLongStack collectLong(LongFunction<? super T> longFunction)
    {
        return LongArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectLong(longFunction)).toImmutable();
    }

    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        return this.delegate.asReversed().collectLong(longFunction, target);
    }

    public ImmutableShortStack collectShort(ShortFunction<? super T> shortFunction)
    {
        return ShortArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectShort(shortFunction)).toImmutable();
    }

    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        return this.delegate.asReversed().collectShort(shortFunction, target);
    }

    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().collect(function, target);
    }

    public <P, V> ImmutableStack<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectWith(function, parameter).toList());
    }

    public <P, V, R extends Collection<V>> R collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().collectWith(function, parameter, targetCollection);
    }

    public <V> ImmutableStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectIf(predicate, function).toList());
    }

    public <V, R extends Collection<V>> R collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().collectIf(predicate, function, target);
    }

    public <V> ImmutableStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().flatCollect(function).toList());
    }

    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.asReversed().flatCollect(function, target);
    }

    public T detect(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().detect(predicate);
    }

    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().detectWith(predicate, parameter);
    }

    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        return this.delegate.asReversed().detectIfNone(predicate, function);
    }

    public <P> T detectWithIfNone(Predicate2<? super T, ? super P> predicate, P parameter, Function0<? extends T> function)
    {
        return this.delegate.asReversed().detectWithIfNone(predicate, parameter, function);
    }

    public int count(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().count(predicate);
    }

    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().countWith(predicate, parameter);
    }

    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().anySatisfy(predicate);
    }

    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().anySatisfyWith(predicate, parameter);
    }

    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().allSatisfy(predicate);
    }

    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().allSatisfyWith(predicate, parameter);
    }

    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().noneSatisfy(predicate);
    }

    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.delegate.asReversed().noneSatisfyWith(predicate, parameter);
    }

    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return this.delegate.asReversed().injectInto(injectedValue, function);
    }

    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> intObjectToIntFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, intObjectToIntFunction);
    }

    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> longObjectToLongFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, longObjectToLongFunction);
    }

    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> doubleObjectToDoubleFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, doubleObjectToDoubleFunction);
    }

    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> floatObjectToFloatFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, floatObjectToFloatFunction);
    }

    public MutableList<T> toList()
    {
        return this.delegate.asReversed().toList();
    }

    public MutableList<T> toSortedList()
    {
        return this.delegate.asReversed().toSortedList();
    }

    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().toSortedList(comparator);
    }

    public <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().toSortedListBy(function);
    }

    public MutableSet<T> toSet()
    {
        return this.delegate.asReversed().toSet();
    }

    public MutableSortedSet<T> toSortedSet()
    {
        return this.delegate.asReversed().toSortedSet();
    }

    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().toSortedSet(comparator);
    }

    public <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().toSortedSetBy(function);
    }

    public MutableBag<T> toBag()
    {
        return this.delegate.asReversed().toBag();
    }

    public MutableSortedBag<T> toSortedBag()
    {
        return this.delegate.asReversed().toSortedBag();
    }

    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().toSortedBag(comparator);
    }

    public <V extends Comparable<? super V>> MutableSortedBag<T> toSortedBagBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().toSortedBagBy(function);
    }

    public <NK, NV> MutableMap<NK, NV> toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toMap(keyFunction, valueFunction);
    }

    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toSortedMap(keyFunction, valueFunction);
    }

    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator, Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toSortedMap(comparator, keyFunction, valueFunction);
    }

    public LazyIterable<T> asLazy()
    {
        return LazyIterate.adapt(this);
    }

    public Object[] toArray()
    {
        return this.delegate.asReversed().toArray();
    }

    public <T> T[] toArray(T[] a)
    {
        return this.delegate.asReversed().toArray(a);
    }

    public T min(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().min(comparator);
    }

    public T max(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().max(comparator);
    }

    public T min()
    {
        return this.delegate.asReversed().min();
    }

    public T max()
    {
        return this.delegate.asReversed().max();
    }

    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().toList().minBy(function);
    }

    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().maxBy(function);
    }

    public long sumOfInt(IntFunction<? super T> intFunction)
    {
        return this.delegate.asReversed().sumOfInt(intFunction);
    }

    public double sumOfFloat(FloatFunction<? super T> floatFunction)
    {
        return this.delegate.asReversed().sumOfFloat(floatFunction);
    }

    public long sumOfLong(LongFunction<? super T> longFunction)
    {
        return this.delegate.asReversed().sumOfLong(longFunction);
    }

    public double sumOfDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.delegate.asReversed().sumOfDouble(doubleFunction);
    }

    public <V> ObjectLongMap<V> sumByInt(Function<T, V> groupBy, IntFunction<? super T> function)
    {
        return this.delegate.asReversed().sumByInt(groupBy, function);
    }

    public <V> ObjectDoubleMap<V> sumByFloat(Function<T, V> groupBy, FloatFunction<? super T> function)
    {
        return this.delegate.asReversed().sumByFloat(groupBy, function);
    }

    public <V> ObjectLongMap<V> sumByLong(Function<T, V> groupBy, LongFunction<? super T> function)
    {
        return this.delegate.asReversed().sumByLong(groupBy, function);
    }

    public <V> ObjectDoubleMap<V> sumByDouble(Function<T, V> groupBy, DoubleFunction<? super T> function)
    {
        return this.delegate.asReversed().sumByDouble(groupBy, function);
    }

    public String makeString()
    {
        return this.delegate.asReversed().makeString();
    }

    public String makeString(String separator)
    {
        return this.delegate.asReversed().makeString(separator);
    }

    public String makeString(String start, String separator, String end)
    {
        return this.delegate.asReversed().makeString(start, separator, end);
    }

    public void appendString(Appendable appendable)
    {
        this.delegate.asReversed().appendString(appendable);
    }

    public void appendString(Appendable appendable, String separator)
    {
        this.delegate.asReversed().appendString(appendable, separator);
    }

    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.delegate.asReversed().appendString(appendable, start, separator, end);
    }

    public <V> ImmutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, FastListMultimap.<V, T>newMultimap()).toImmutable();
    }

    public <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().groupBy(function, target);
    }

    public <V> ImmutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, FastListMultimap.<V, T>newMultimap()).toImmutable();
    }

    public <V, R extends MutableMultimap<V, T>> R groupByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.asReversed().groupByEach(function, target);
    }

    public <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.<V, T>newMap()).toImmutable();
    }

    public <V, R extends MutableMap<V, T>> R groupByUniqueKey(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().groupByUniqueKey(function, target);
    }

    public <S> ImmutableStack<Pair<T, S>> zip(Iterable<S> that)
    {
        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().zip(that).toList());
    }

    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.delegate.asReversed().zip(that, target);
    }

    public ImmutableStack<Pair<T, Integer>> zipWithIndex()
    {
        int maxIndex = this.delegate.size() - 1;
        Interval indicies = Interval.fromTo(0, maxIndex);

        return ImmutableArrayStack.newStackFromTopToBottom(this.delegate.asReversed().zip(indicies).toList());
    }

    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.delegate.asReversed().zipWithIndex(target);
    }

    public ImmutableStack<T> toImmutable()
    {
        return this;
    }

    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return this.delegate.asReversed().chunk(size);
    }

    public <K, V> MapIterable<K, V> aggregateInPlaceBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Procedure2<? super V, ? super T> mutatingAggregator)
    {
        MutableMap<K, V> map = UnifiedMap.newMap();
        this.forEach(new MutatingAggregationProcedure<T, K, V>(map, groupBy, zeroValueFactory, mutatingAggregator));
        return map;
    }

    public <K, V> MapIterable<K, V> aggregateBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        MutableMap<K, V> map = UnifiedMap.newMap();
        this.forEach(new NonMutatingAggregationProcedure<T, K, V>(map, groupBy, zeroValueFactory, nonMutatingAggregator));
        return map;
    }

    public int size()
    {
        return this.delegate.size();
    }

    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    public boolean notEmpty()
    {
        return this.delegate.notEmpty();
    }

    public ImmutableStack<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    public void forEach(Procedure<? super T> procedure)
    {
        this.each(procedure);
    }

    public void each(Procedure<? super T> procedure)
    {
        this.delegate.reverseForEach(procedure);
    }

    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.delegate.asReversed().forEachWithIndex(objectIntProcedure);
    }

    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.delegate.asReversed().forEachWith(procedure, parameter);
    }

    public Iterator<T> iterator()
    {
        return this.delegate.asReversed().iterator();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof StackIterable<?>))
        {
            return false;
        }

        StackIterable<?> that = (StackIterable<?>) o;

        if (that instanceof ImmutableArrayStack<?>)
        {
            return this.delegate.equals(((ImmutableArrayStack<?>) that).delegate);
        }
        Iterator<T> thisIterator = this.iterator();
        Iterator<?> thatIterator = that.iterator();
        while (thisIterator.hasNext() && thatIterator.hasNext())
        {
            if (!Comparators.nullSafeEquals(thisIterator.next(), thatIterator.next()))
            {
                return false;
            }
        }
        return !thisIterator.hasNext() && !thatIterator.hasNext();
    }

    @Override
    public String toString()
    {
        return this.delegate.asReversed().makeString("[", ", ", "]");
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (T each : this)
        {
            hashCode = 31 * hashCode + (each == null ? 0 : each.hashCode());
        }
        return hashCode;
    }

    private Object writeReplace()
    {
        return new ImmutableStackSerializationProxy<T>(this);
    }

    private static class ImmutableStackSerializationProxy<T> implements Externalizable
    {
        private static final long serialVersionUID = 1L;

        private StackIterable<T> stack;

        @SuppressWarnings("UnusedDeclaration")
        public ImmutableStackSerializationProxy()
        {
            // Empty constructor for Externalizable class
        }

        protected ImmutableStackSerializationProxy(StackIterable<T> stack)
        {
            this.stack = stack;
        }

        public void writeExternal(final ObjectOutput out) throws IOException
        {
            out.writeInt(this.stack.size());
            try
            {
                this.stack.forEach(new CheckedProcedure<T>()
                {
                    public void safeValue(T object) throws IOException
                    {
                        out.writeObject(object);
                    }
                });
            }
            catch (RuntimeException e)
            {
                if (e.getCause() instanceof IOException)
                {
                    throw (IOException) e.getCause();
                }
                throw e;
            }
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
        {
            int size = in.readInt();
            FastList<T> deserializedDelegate = new FastList<T>(size);

            for (int i = 0; i < size; i++)
            {
                deserializedDelegate.add((T) in.readObject());
            }

            this.stack = ImmutableArrayStack.newStackFromTopToBottom(deserializedDelegate);
        }

        protected Object readResolve()
        {
            return this.stack;
        }
    }
}
