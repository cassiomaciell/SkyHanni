package at.hannibal2.skyhanni.features.fishing

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.ActionBarData
import at.hannibal2.skyhanni.data.mob.Mob
import at.hannibal2.skyhanni.events.MobEvent
import at.hannibal2.skyhanni.events.PlaySoundEvent
import at.hannibal2.skyhanni.events.fishing.SeaCreatureFishEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.SimpleTimeMark
import at.hannibal2.skyhanni.utils.collection.TimeLimitedSet
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@SkyHanniModule
object MuteFishingSounds {

    private val agarimoos = TimeLimitedSet<Mob>(6.minutes)
    private var lastCatch = SimpleTimeMark.farPast()
    private var lastCatchName = ""
    private val config get() = SkyHanniMod.feature.fishing

    @HandleEvent(onlyOnSkyblock = true)
    fun onMobFirstSeen(event: MobEvent.FirstSeen.SkyblockMob) {
        val mob = event.mob
        if (mob.name != "Agarimoo" || mob.name != lastCatchName || !FishingApi.isFishing(checkRodInHand = true)) return
        if (lastCatch.passedSince() > 150.milliseconds) return

        agarimoos.add(mob)
    }

    @HandleEvent
    fun onMobDespawn(event: MobEvent.DeSpawn.SkyblockMob) {
        agarimoos.remove(event.mob)
    }

    @HandleEvent
    fun onWorldChange() {
        agarimoos.clear()
    }

    @HandleEvent(onlyOnSkyblock = true)
    fun onSeaCreatureFish(event: SeaCreatureFishEvent) {
        lastCatch = SimpleTimeMark.now()
        lastCatchName = event.seaCreature.name
    }

    private fun isWearingThunderGear(): Boolean {
        return ActionBarData.getActionBar().contains("⚡")
    }

    @HandleEvent(onlyOnSkyblock = true)
    fun onSound(event: PlaySoundEvent) {
        val sound = event.soundName
        val isFishing = FishingApi.isFishing(checkRodInHand = true)

        val muteThunderGear = sound == "entity.firework_rocket.twinkle_far" && config.muteThunderGear && isWearingThunderGear()
        val muteAgarimooDamage = sound == "entity.player.hurt" && config.muteAgarimooDamage && agarimoos.isNotEmpty() && isFishing

        if (muteThunderGear || (muteAgarimooDamage && event.distanceToPlayer <= 1)) event.cancel()
    }
}
