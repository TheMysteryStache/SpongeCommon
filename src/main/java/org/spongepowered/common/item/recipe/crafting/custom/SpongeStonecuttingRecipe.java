package org.spongepowered.common.item.recipe.crafting.custom;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.common.item.util.ItemStackUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class SpongeStonecuttingRecipe extends SingleItemRecipe {

    private static IRecipeSerializer<SpongeStonecuttingRecipe> SPONGE_STONECUTTER = register("stonecutting", new SpongeStoneCutterSerializer());

    static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String key, S recipeSerializer) {
        ResourceLocation resourceLocation = (ResourceLocation) (Object) CatalogKey.sponge("stonecutting");
        return (S)(Registry.<IRecipeSerializer<?>>register(Registry.RECIPE_SERIALIZER, resourceLocation, recipeSerializer));
    }

    private Predicate<ItemStackSnapshot> ingredientPredicate;

    // Constructor for custom plugin recipes
    public SpongeStonecuttingRecipe(ResourceLocation key, String group, Predicate<ItemStackSnapshot> ingredientPredicate, Ingredient ingredient, ItemStack result) {
        super(IRecipeType.STONECUTTING, SPONGE_STONECUTTER, (ResourceLocation)(Object)key, group, ingredient, result);
        this.ingredientPredicate = ingredientPredicate;
        SpongeStoneCutterSerializer.PREDICATE_MAP.put(key, ingredientPredicate);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        ItemStack itemStack = inv.getStackInSlot(0);
        if (this.ingredientPredicate != null) {
            if (!this.ingredientPredicate.test(ItemStackUtil.snapshotOf(itemStack))) {
                return false;
            }
        }
        return this.ingredient.test(itemStack);
    }

    /**
     * Based of {@link SingleItemRecipe.Serializer}
     */
    public static class SpongeStoneCutterSerializer implements IRecipeSerializer<SpongeStonecuttingRecipe> {

        private static Map<ResourceLocation, Predicate<ItemStackSnapshot>> PREDICATE_MAP = new HashMap<>();

        @Override
        public SpongeStonecuttingRecipe read(ResourceLocation recipeId, JsonObject json) {
            String s = JSONUtils.getString(json, "group", "");
            Ingredient ingredient;
            if (JSONUtils.isJsonArray(json, "ingredient")) {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
            }

            String s1 = JSONUtils.getString(json, "result");
            int i = JSONUtils.getInt(json, "count");
            ItemStack itemstack = new ItemStack(Registry.ITEM.getOrDefault(new ResourceLocation(s1)), i);
            return this.newRecipe(recipeId, s, ingredient, itemstack);
        }

        @Override
        public SpongeStonecuttingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            String s = buffer.readString(32767);
            Ingredient ingredient = Ingredient.read(buffer);
            ItemStack itemstack = buffer.readItemStack();
            return this.newRecipe(recipeId, s, ingredient, itemstack);
        }

        private SpongeStonecuttingRecipe newRecipe(ResourceLocation recipeId, String group, Ingredient ingredient, ItemStack itemstack) {
            Predicate<ItemStackSnapshot> predicate = PREDICATE_MAP.get(recipeId);
            return new SpongeStonecuttingRecipe(recipeId, group, predicate, ingredient, itemstack);
        }

        @Override
        public void write(PacketBuffer buffer, SpongeStonecuttingRecipe recipe) {
            buffer.writeString(recipe.group);
            recipe.ingredient.write(buffer);
            buffer.writeItemStack(recipe.result);
        }
    }
}
