package at.hannibal2.skyhanni.config.features.garden

import at.hannibal2.skyhanni.config.FeatureToggle
import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class AutoSettingsConfig {
    @Expose
    @ConfigEditorBoolean
    @FeatureToggle
    @ConfigOption(name = "Disable View Bobbing", desc = "Disables view bobbing while holding a farming tool.")
    var disableViewBobbing: Boolean = false
}
