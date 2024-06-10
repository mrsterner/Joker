package dev.sterner.joker.game
/*
import dev.sterner.joker.JokerMod
import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.core.Card
import dev.sterner.joker.core.GameUtils
import dev.sterner.joker.core.Special
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import org.joml.Vector3i

@Deprecated("")
class CardEntity(entity: EntityType<*>, level: Level) : Entity(JokerMod.CARD_ENTITY, level) {

    constructor(level: Level): this(JokerMod.CARD_ENTITY, level)

    val width: Int = 31
    val height: Int = 45
    var card: Card? = null
    var screenPos = Vector3i(0,0,0)
    var targetScreenPos = Vector3i(0,0,0)

    fun getCardName(card: Card): String {
        return card.rank.name.lowercase() + "_of_" + card.suit.name.lowercase()
    }

    fun getBaseName(card: Card): String {
        if (card.special == Special.NONE || card.special == Special.WILD) {
            return "base"
        }
        return card.special.name.lowercase() + "_base"
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {

    }

    fun tick(partialTick: Float){
        if (screenPos != targetScreenPos) {
            screenPos = lerp(screenPos, targetScreenPos, partialTick)
        }
    }

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {
        card = GameUtils.readCardFromTag(compoundTag)
        //screenPos = Vector3i(compoundTag.getInt("X"), compoundTag.getInt("Y"), compoundTag.getInt("Z"))
        //targetScreenPos = Vector3i(compoundTag.getInt("TX"), compoundTag.getInt("TY"), compoundTag.getInt("TZ"))
    }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {
        if (card != null) {
            GameUtils.writeCardToTag(card!!, compoundTag)
        }
/*
        compoundTag.putInt("X", screenPos.x)
        compoundTag.putInt("Y", screenPos.y)
        compoundTag.putInt("Z", screenPos.z)

        compoundTag.putInt("TX", targetScreenPos.x)
        compoundTag.putInt("TY", targetScreenPos.y)
        compoundTag.putInt("TZ", targetScreenPos.z)

 */
    }

    fun easeOut(t: Float): Float {
        return t * (2 - t)
    }

    fun lerp(a: Vector3i, b: Vector3i, t: Float): Vector3i {
        val easedT = easeOut(t)
        val x = a.x + (easedT * (b.x - a.x)).toInt()
        val y = a.y + (easedT * (b.y - a.y)).toInt()
        val z = a.z + (easedT * (b.z - a.z)).toInt()
        return Vector3i(x, y, z)
    }
}

 */