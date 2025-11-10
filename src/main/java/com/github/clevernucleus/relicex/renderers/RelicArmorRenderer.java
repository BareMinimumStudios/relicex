package com.github.clevernucleus.relicex.renderers;

import com.github.clevernucleus.relicex.item.ArmorRelicItem;
import mod.azure.azurelibarmor.model.GeoModel;
import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;

public class RelicArmorRenderer extends GeoArmorRenderer<ArmorRelicItem> {
    public RelicArmorRenderer(GeoModel<ArmorRelicItem> model) {
        super(model);
    }
}
