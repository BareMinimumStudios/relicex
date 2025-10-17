package com.github.clevernucleus.relicex.renderers;

import com.github.clevernucleus.relicex.item.ArmorRelicItem;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoArmorRenderer;

public class RelicArmorRenderer extends GeoArmorRenderer<ArmorRelicItem> {
    public RelicArmorRenderer(GeoModel<ArmorRelicItem> model) {
        super(model);
    }
}
