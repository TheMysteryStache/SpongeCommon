/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
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
package org.spongepowered.common.mixin.core.command.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import org.spongepowered.api.command.registrar.tree.ClientCompletionKey;
import org.spongepowered.api.command.registrar.tree.CommandTreeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.common.bridge.command.argument.ArgumentTypes_EntryBridge;

import java.util.function.Function;

@Mixin(targets = "net/minecraft/command/arguments/ArgumentTypes$Entry")
public abstract class ArgumentTypes_EntryMixin<T extends CommandTreeBuilder<T>, S extends ArgumentType<?>>
        implements ArgumentTypes_EntryBridge<S, T> {

    private Function<ClientCompletionKey<T>, T> impl$supplier;

    @Override
    public T bridge$provideCommandTreeBuilder() {
        return this.impl$supplier.apply((ClientCompletionKey<T>) this);
    }

    @Override
    public void bridge$setCommandTreeBuilderProvider(Function<ClientCompletionKey<T>, T> supplier) {
        this.impl$supplier = supplier;
    }

}
