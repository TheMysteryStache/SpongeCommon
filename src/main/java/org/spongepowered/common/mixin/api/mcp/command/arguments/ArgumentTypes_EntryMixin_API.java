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
package org.spongepowered.common.mixin.api.mcp.command.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.command.registrar.tree.ClientCompletionKey;
import org.spongepowered.api.command.registrar.tree.CommandTreeBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.bridge.command.argument.ArgumentTypes_EntryBridge;

@Mixin(targets = "net/minecraft/command/arguments/ArgumentTypes$Entry")
public abstract class ArgumentTypes_EntryMixin_API<T extends CommandTreeBuilder<T>, S extends ArgumentType<?>>
        implements ClientCompletionKey<T>, ArgumentTypes_EntryBridge<S, T> {

    @Shadow @Final public ResourceLocation shadow$id;

    @Override
    public T createCommandTreeBuilder() {
        return this.bridge$provideCommandTreeBuilder();
    }

    @Override
    public CatalogKey getKey() {
        return (CatalogKey) (Object) this.shadow$id;
    }

}
