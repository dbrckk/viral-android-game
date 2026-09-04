package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import com.empiretycoon.idleconquest.game.MissionDefinition
import com.empiretycoon.idleconquest.game.MissionRewardType

class MissionBadgeRenderer(context: Context) {
    private val statusBadges = StatusBadgeRenderer(context)
    private val actionIcons = MissionActionIconRenderer(context)
    private val atlas = RasterAtlas(
        context,
        "art/missions/raster/missions_atlas_runtime64.webp.b64",
        4,
        6,
    )

    private val iconRows = mapOf(
        "stack_up" to 0,
        "street_badge" to 1,
        "income_wave" to 2,
        "team" to 3,
        "chip_star" to 4,
        "factory_crown" to 5,
    )

    private val stateColumns = mapOf(
        "locked" to 0,
        "active" to 1,
        "complete" to 2,
        "claimed" to 3,
    )

    fun draw(c: Canvas, r: RectF, m: MissionDefinition, state: String) {
        val row = iconRows[m.icon] ?: return
        val col = stateColumns[state] ?: return
        if (!atlas.drawCell(c, r, col, row)) return

        val s = r.width() * .34f
        val actionRect = RectF(r.right - s, r.top, r.right, r.top + s)
        when (state) {
            "locked" -> actionIcons.draw(c, actionRect, "locked")
            "complete" -> actionIcons.draw(c, actionRect, "claim")
            else -> actionIcons.draw(c, actionRect, if (m.reward.type == MissionRewardType.CASH) "cash" else "gem")
        }

        if (state == "claimed") {
            val cs = r.width() * .42f
            statusBadges.draw(c, RectF(r.right - cs, r.bottom - cs, r.right, r.bottom), "completed")
        }
    }
}
