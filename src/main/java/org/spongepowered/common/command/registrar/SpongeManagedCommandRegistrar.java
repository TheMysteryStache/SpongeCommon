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
package org.spongepowered.common.command.registrar;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.registrar.tree.CommandTreeBuilder;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.command.manager.SpongeCommandCause;

/**
 * For use with {@link org.spongepowered.api.command.Command.Parameterized}
 */
public class SpongeManagedCommandRegistrar extends SpongeCommandRegistrar<Command.Parameterized> {

    public static final CatalogKey CATALOG_KEY = CatalogKey.builder().namespace(SpongeImpl.getSpongePlugin()).value("managed").build();
    public static final SpongeManagedCommandRegistrar INSTANCE = new SpongeManagedCommandRegistrar(CATALOG_KEY);

    private SpongeManagedCommandRegistrar(CatalogKey catalogKey) {
        super(catalogKey);
    }

    @Override
    LiteralArgumentBuilder<CommandCause> createNode(String primaryAlias, Command.Parameterized command) {
        return null;
    }

    @Override
    public void completeCommandTree(CommandCause commandCause, CommandTreeBuilder.Basic builder) {
        // We're going to cheat at bit. We will let Minecraft serialise the nodes, then we'll use the
        // Json that is provided to create the command trees.
        // We don't really care for the helper methods, so all nodes will be "Basic". We don't allow
        // exposing the tree to others.

    }
}
