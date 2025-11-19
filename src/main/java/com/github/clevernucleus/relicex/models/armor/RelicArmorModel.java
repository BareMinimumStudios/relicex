package com.github.clevernucleus.relicex.models.armor;

import com.github.clevernucleus.relicex.impl.Rareness;
import com.github.clevernucleus.relicex.item.ArmorRelicItem;
import mod.azure.azurelibarmor.model.GeoModel;
import net.minecraft.util.Identifier;

public class RelicArmorModel extends GeoModel<ArmorRelicItem> {

    private final Identifier model;
    private final Identifier texture;

    public RelicArmorModel(Rareness rareness) {
        this.model = Identifier.of("relicex", "geo/armors/" + rareness.key() + ".geo.json");
        this.texture = Identifier.of("relicex", "textures/models/armor/v2/" + rareness.key() + ".png");
    }

    @Override
    public Identifier getModelResource(ArmorRelicItem armorRelicItem) {
        return model;
    }

    @Override
    public Identifier getTextureResource(ArmorRelicItem armorRelicItem) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(ArmorRelicItem armorRelicItem) {
        return null;
    }
}
