package fr.multimc.api.spigot.sample.spigot.gui.linked;

import fr.multimc.api.spigot.sample.spigot.gui.basic.ExitComponent;
import fr.multimc.api.spigot.common.gui.GuiView;
import fr.multimc.api.spigot.common.gui.components.LinkedComponent;
import fr.multimc.api.spigot.common.gui.enums.GuiSize;
import fr.multimc.api.spigot.common.gui.gui.LinkedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SampleLinkedGui extends LinkedGui {

    public SampleLinkedGui(@NotNull final Plugin plugin, @NotNull final GuiView view) {
        super(plugin, Component.text(""), GuiSize.DOUBLE_CHEST, view);
        this.addView("view1", new GuiView(GuiSize.DOUBLE_CHEST));
        this.fillDefaultView();
        this.fillView1();
        this.render();
    }

    public void fillDefaultView(){
        final GuiView defaultView = this.getView("default");
        if(Objects.isNull(defaultView)) return;
        defaultView.fill(Material.BLACK_STAINED_GLASS_PANE);
        defaultView.setComponent(this.getLastSlot(), new ExitComponent());
        defaultView.setComponent(this.getFirstSlot(), new LinkedComponent(new ItemStack(Material.DIAMOND), this, "view1"));
    }

    public void fillView1(){
        final GuiView view1 = this.getView("view1");
        if(Objects.isNull(view1)) return;
        view1.fill(Material.RED_STAINED_GLASS_PANE);
        view1.setComponent(this.getLastSlot(), new ExitComponent());
        view1.setComponent(this.getFirstSlot(), new LinkedComponent(new ItemStack(Material.DIAMOND), this, "default"));
    }

}
