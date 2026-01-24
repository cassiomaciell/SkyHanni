package at.hannibal2.skyhanni.features.garden

import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.events.garden.GardenToolChangeEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule

@SkyHanniModule
object GardenAutoSettings {
    val config get() = GardenApi.config
    var isHoldingFarmingTool = false

    @JvmStatic
    fun shouldDisableViewBobbing() = GardenApi.inGarden() && config.disableViewBobbing && isHoldingFarmingTool

    @HandleEvent
    fun onGardenToolChange(event: GardenToolChangeEvent) {
        (event.crop != null).also { isHoldingFarmingTool = it }
    }

}
