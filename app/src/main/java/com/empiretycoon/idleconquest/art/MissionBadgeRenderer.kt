package com.empiretycoon.idleconquest.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64
import com.empiretycoon.idleconquest.game.MissionDefinition
import com.empiretycoon.idleconquest.game.MissionRewardType

class MissionBadgeRenderer(private val context: Context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val statusBadges = StatusBadgeRenderer(context)
    private val actionIcons = MissionActionIconRenderer(context)
    private val atlas: Bitmap? by lazy {
        runCatching {
            val encoded = context.assets.open("art/missions/raster/missions_atlas_runtime64.webp.b64")
                .bufferedReader()
                .use { it.readText().trim() }
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    private val missionRows = mapOf(
        "first_25_levels" to 0,
        "street_lv100" to 1,
        "income_10k" to 2,
        "hire_two" to 3,
        "buy_four_upgrades" to 4,
        "factory_lv500" to 5
    )

    private val stateColumns = mapOf(
        "locked" to 0,
        "active" to 1,
        "complete" to 2,
        "claimed" to 3
    )

    fun draw(c: Canvas, r: RectF, m: MissionDefinition, state: String) {
        val bmp = atlas ?: return
        val row = missionRows[m.id] ?: return
        val col = stateColumns[state] ?: return
        val cellW = bmp.width / 4
        val cellH = bmp.height / 6
        val src = Rect(col * cellW, row * cellH, (col + 1) * cellW, (row + 1) * cellH)
        c.drawBitmap(bmp, src, r, paint)

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
