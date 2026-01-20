package at.hannibal2.skyhanni.features.fishing

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.api.event.HandleEvent
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
object MuteAgarimooDamage {

    private val agarimoos = TimeLimitedSet<Mob>(6.minutes)
    private var lastCatch = SimpleTimeMark.farPast()
    private var lastCatchName = ""

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

    @HandleEvent(onlyOnSkyblock = true)
    fun onSound(event: PlaySoundEvent) {
        if (!FishingApi.isFishing(checkRodInHand = true) || !SkyHanniMod.feature.fishing.muteAgarimooDamage) return
        if (agarimoos.isEmpty() || event.soundName != "entity.player.hurt" || event.distanceToPlayer > 1) return

        event.cancel()
    }
}
