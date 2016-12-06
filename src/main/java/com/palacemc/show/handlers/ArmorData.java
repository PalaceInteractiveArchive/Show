package com.palacemc.show.handlers;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 2/29/16
 */
public class ArmorData {
    private ItemStack head;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public ArmorData(ItemStack head, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.head = head;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public ItemStack getHead() {
        return head;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    @Override
    public String toString() {
        return head.toString() + " " + chestplate.toString() + " " + leggings.toString() + " " + boots.toString();
    }
}