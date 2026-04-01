package com.EvgenWarGold.GregTechNightmare.Utils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;

public class GTN_RecipeUtils {

    public static Object getCircuit(Materials circuitTier) {
        return OrePrefixes.circuit.get(circuitTier);
    }

    public static Object[] getCircuits(Materials circuitTier, int amount) {
        return new Object[] { OrePrefixes.circuit.get(circuitTier), amount };
    }
}
