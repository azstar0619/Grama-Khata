package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary palette ──────────────────────────────────────────────────────────
val GreenDark       = Color(0xFF1B5E20)
val GreenMid        = Color(0xFF2E7D32)
val GreenLight      = Color(0xFF4CAF50)
val GreenContainer  = Color(0xFFC8E6C9)
val GreenOnContainer= Color(0xFF1B5E20)

// ── Secondary / accent ───────────────────────────────────────────────────────
val SaffronDark     = Color(0xFFE65100)
val SaffronMid      = Color(0xFFFF6D00)
val SaffronLight    = Color(0xFFFFAB40)
val SaffronContainer= Color(0xFFFFE0B2)

// ── Background / surface ─────────────────────────────────────────────────────
val BgLight         = Color(0xFFF4FAF4)
val BgDark          = Color(0xFF101810)
val SurfaceLight    = Color(0xFFFFFFFF)
val SurfaceDark     = Color(0xFF1A251A)
val SurfaceVarLight = Color(0xFFECF5EC)
val SurfaceVarDark  = Color(0xFF253025)

// ── Semantic ─────────────────────────────────────────────────────────────────
val AmountDue       = Color(0xFFC62828)
val AmountAdvance   = Color(0xFF1B5E20)
val AmountNeutral   = Color(0xFF546E7A)

// ── Avatar palette ───────────────────────────────────────────────────────────
val AvatarColors = listOf(
    Color(0xFF1A237E), Color(0xFF4A148C), Color(0xFF880E4F),
    Color(0xFFB71C1C), Color(0xFF1B5E20), Color(0xFF006064),
    Color(0xFF01579B), Color(0xFFE65100), Color(0xFF4E342E),
    Color(0xFF33691E), Color(0xFF37474F)
)

fun avatarColorFor(name: String): Color =
    AvatarColors[kotlin.math.abs(name.hashCode()) % AvatarColors.size]
