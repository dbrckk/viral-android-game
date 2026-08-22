# Business Group 01 — Production Contract

This document operationalizes the locked Empire Tycoon / Idle Conquest art direction for the first four businesses. It does not replace `docs/art/DIRECTION_ART.md`.

## Businesses

| ID | Visual identity | Progression |
| --- | --- | --- |
| `street_stand` | warm green / earthy, cozy first-income stand | base → lv25 → lv100 → lv250 → lv500 → master |
| `corner_shop` | blue/cyan urban neon corner boutique | base → lv25 → lv100 → lv250 → lv500 → master |
| `workshop` | orange industrial workshop | base → lv25 → lv100 → lv250 → lv500 → master |
| `factory` | violet/magenta advanced industrial factory | base → lv25 → lv100 → lv250 → lv500 → master |

## Required deliverables per business

Each final raster asset must preserve the 3/4 isometric camera and transparent RGBA delivery. The canonical naming contract is:

`<business>__<tier>__<state>__<layer>__<profile>.png`

Allowed tiers: `base`, `lv25`, `lv100`, `lv250`, `lv500`, `master`.

Allowed states: `default`, `unlock`, `milestone`.

Layer order is fixed: `base_structure`, `details`, `lights_emissive`, `effects_particles`.

Runtime profiles are `full`, `reduced_motion`, and `power_save`. Static structural layers may be shared when pixel-identical; motion and particle-heavy layers require profile-aware variants.

## Source references

The generated visual references recorded in `assets/art/manifest.json` remain authoritative. In particular, Group 01 must follow the existing Street Stand, Corner Shop, Workshop, and Factory evolution language rather than being redesigned from scratch.

## Android integration

Production masters live under `assets/art/business/group_01/`. Runtime-ready exports are mirrored or generated under `app/src/main/assets/art/business/group_01/` once the Android application module exists.

The renderer must resolve a requested business/tier/state/layer/profile deterministically and may fall back from `reduced_motion` or `power_save` to `full` only when the requested layer is static and visually equivalent.

## Quality gates

- No perspective drift between tiers.
- Silhouette remains readable on small mobile screens.
- Upgrade tiers increase richness without changing business identity.
- Emissive layers remain separable from structural color.
- Particle-heavy effects cannot be required for semantic comprehension.
- Unlock and milestone states remain recognizable with reduced motion enabled.
- No generated reference may be silently replaced by a new art direction.
