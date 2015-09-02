/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.spongepowered.common.entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.spongepowered.api.data.DataQuery.of;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.common.data.DataProcessor;
import org.spongepowered.common.data.SpongeDataRegistry;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

public class SpongeEntitySnapshot implements EntitySnapshot {

    @Nullable private final UUID entityUuid;
    @Nullable private final Transform<World> world;
    private final EntityType entityType;
    private final ImmutableList<ImmutableDataManipulator<?, ?>> manipulators;
    private final ImmutableSet<Key<?>> keys;
    private final ImmutableSet<ImmutableValue<?>> values;

    public SpongeEntitySnapshot(@Nullable UUID entityUuid, @Nullable Transform<World> world, EntityType entityType,
                                ImmutableList<ImmutableDataManipulator<?, ?>> manipulators) {
        this.entityUuid = entityUuid;
        this.world = world;
        this.entityType = checkNotNull(entityType);
        this.manipulators = checkNotNull(manipulators);
        ImmutableSet.Builder<Key<?>> keyBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<ImmutableValue<?>> valueBuilder = ImmutableSet.builder();
        for (ImmutableDataManipulator<?, ?> manipulator : manipulators) {
            keyBuilder.addAll(manipulator.getKeys());
            valueBuilder.addAll(manipulator.getValues());
        }
        this.keys = keyBuilder.build();
        this.values = valueBuilder.build();
    }

    @Override
    public Optional<UUID> getUniqueId() {
        return Optional.fromNullable(this.entityUuid);
    }

    @Override
    public Optional<Transform<World>> getTransform() {
        return Optional.fromNullable(this.world);
    }

    @Override
    public EntityType getType() {
        return this.entityType;
    }

    @Override
    public Optional<Location<World>> getLocation() {
        return Optional.fromNullable(this.world == null ? null : this.world.getLocation());
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getManipulators() {
        return this.manipulators;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
            .set(of("EntityType"), this.entityType.getId())
            .set(of("Location"), this.world == null ? "null" : this.world.getLocation())
            .set(of("Data"), this.manipulators);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        for (ImmutableDataManipulator<?, ?> manipulator : this.manipulators) {
            if (containerClass.isInstance(manipulator)) {
                return Optional.of((T) manipulator);
            }
        }
        return Optional.absent();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        final Optional<T> optional = get(containerClass);
        if (optional.isPresent()) {
            return optional;
        } else { // try harder
            final Optional<DataProcessor> processorOptional = SpongeDataRegistry.getInstance().getWildImmutableProcessor(containerClass);
            if (processorOptional.isPresent()) {
                // TODO if (processorOptional.get().supports(this.entityType)) {
                    return Optional.of((T) (Object) SpongeDataRegistry.getInstance().getWildBuilderForImmutable(containerClass).get().create());
                //}
            }
        }
        return Optional.absent();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        for (ImmutableDataManipulator<?, ?> manipulator : this.manipulators) {
            if (containerClass.isInstance(manipulator)) {
                return true;
            }
        }
        final Optional<DataProcessor> processorOptional = SpongeDataRegistry.getInstance().getWildImmutableProcessor(containerClass);
        return processorOptional.isPresent(); // TODO && processorOptional.get().supports(this.entityType);
    }

    @Override
    public <E> Optional<EntitySnapshot> transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        checkNotNull(key);
        checkNotNull(function);
        final ImmutableList.Builder<ImmutableDataManipulator<?, ?>> builder = ImmutableList.builder();
        boolean createNew = false;
        for (ImmutableDataManipulator<?, ?> manipulator : this.manipulators) {
            if (manipulator.supports(key)) {
                createNew = true;
                manipulator.with(key, checkNotNull(function.apply(manipulator.get(key).orNull())));
            }
        }
        if (createNew) {
            return Optional.of((EntitySnapshot) new SpongeEntitySnapshot(this.entityUuid, this.world, this.entityType, builder.build()));
        }
        return null;
    }

    @Override
    public <E> Optional<EntitySnapshot> with(Key<? extends BaseValue<E>> key, E value) {
        return null;
    }

    @Override
    public Optional<EntitySnapshot> with(BaseValue<?> value) {
        return null;
    }

    @Override
    public Optional<EntitySnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        return null;
    }

    @Override
    public Optional<EntitySnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        return null;
    }

    @Override
    public Optional<EntitySnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return null;
    }

    @Override
    public EntitySnapshot merge(EntitySnapshot that) {
        return null;
    }

    @Override
    public EntitySnapshot merge(EntitySnapshot that, MergeFunction function) {
        return null;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getContainers() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        checkNotNull(key);
        for (ImmutableValue<?> value : this.values) {
            if (value.getKey().equals(key)) {
                return Optional.of((E) value.get());
            }
        }
        return Optional.absent();
    }

    @Nullable
    @Override
    public <E> E getOrNull(Key<? extends BaseValue<E>> key) {
        return get(key).orNull();
    }

    @Override
    public <E> E getOrElse(Key<? extends BaseValue<E>> key, E defaultValue) {
        return get(key).or(defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        checkNotNull(key);
        for (ImmutableValue<?> value : this.values) {
            if (value.getKey().equals(key)) {
                return Optional.of((V) value.asMutable());
            }
        }
        return Optional.absent();
    }

    @Override
    public boolean supports(Key<?> key) {

        return false;
    }

    @Override
    public boolean supports(BaseValue<?> baseValue) {
        return false;
    }

    @Override
    public EntitySnapshot copy() {
        return this;
    }

    @Override
    public Set<Key<?>> getKeys() {
        return this.keys;
    }

    @Override
    public Set<ImmutableValue<?>> getValues() {
        return this.values;
    }
}
