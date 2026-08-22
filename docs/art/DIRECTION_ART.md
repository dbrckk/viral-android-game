# Direction artistique — Empire Tycoon: Idle Conquest

> Source de vérité artistique du projet. Toute nouvelle production doit rester compatible avec ces règles et ne doit pas redéfinir le style.

## Identité visuelle

- Nom de travail : **Empire Tycoon — Idle Conquest**.
- Genre : idle / tycoon mobile à progression exponentielle.
- Rendu principal : **isométrique 3/4**, lisible sur petit écran.
- Univers : progression du commerce terrestre vers des mégastructures futuristes et cosmiques.
- Fond/UI : sombre, spatial, très contrasté.
- Accents : or + néons vert / bleu / orange / violet selon les familles d'assets.
- Matériaux : métal sombre, verre, panneaux lumineux, emissive/glow contrôlé.
- Style : illustration mobile premium, nette, détaillée, silhouettes fortes, lecture immédiate avant micro-détails.
- Interdit : photoréalisme, flat design générique, pastel dominant, contours mous, changement arbitraire de perspective.

## Business — groupe 01

### Street Stand
- Palette dominante : vert / terre / orange chaud.
- Atmosphère : petit commerce chaleureux, vivant, lanternes, végétation, bois, auvent.
- Progression : BASE → LV25 → LV100 → LV250 → LV500 → MASTER.

### Corner Shop
- Palette dominante : bleu urbain / cyan.
- Atmosphère : commerce de ville, enseigne lumineuse, vitrines, trottoir, ambiance nocturne.
- Progression : BASE → LV25 → LV100 → LV250 → LV500 → MASTER.

### Workshop
- Palette dominante : orange industriel.
- Atmosphère : atelier mécanique/industriel, chaleur, métal, énergie et production.

### Factory
- Palette dominante : violet / magenta industriel premium.
- Atmosphère : usine évoluée, plus massive, automatisée et futuriste.

## Progression du monde

La montée en puissance doit évoluer sans rupture de style :

1. Street Stand
2. Corner Shop
3. Workshop
4. Factory
5. Tech Company
6. Megacity
7. Moon Colony
8. Mars Empire
9. Dyson Network
10. Galactic Exchange
11. Intergalactic Gateway
12. Cosmic Foundry
13. Reality Engine
14. Transcendent Nexus

La progression visuelle augmente : échelle, verticalité, densité lumineuse, complexité technologique et présence cosmique.

## UI / HUD

- Cadres sombres métalliques, séparateurs nets.
- Contraste élevé, chiffres et revenus prioritaires.
- Lueurs utilisées comme hiérarchie, jamais comme bruit.
- Couleurs fonctionnelles cohérentes :
  - cash : vert,
  - managers : bleu,
  - upgrades : or,
  - missions : violet,
  - prestige / legacy : violet premium,
  - gems : cyan/bleu,
  - rewarded ads : violet/bleu.
- Boutons importants : surface sombre + bord lumineux + état pressé explicite.
- Cibles tactiles adaptées mobile.

## Spécifications d'assets

- Master art : 2048 × 2048 px lorsque l'asset le nécessite.
- Format de livraison principal : PNG 32-bit RGBA, fond transparent.
- Perspective : isométrique 3/4 constante.
- Layers recommandés :
  1. base_structure
  2. details
  3. lights_emissive
  4. effects_particles
- Animations de référence : 24/30 FPS selon le type.
- Les variations MASTER doivent conserver la silhouette-source tout en augmentant prestige, lumière et complexité.

## Performance / accessibilité

Tous les assets animés ou VFX doivent prévoir :

- `full` : rendu complet,
- `reduced_motion` : mouvements simplifiés,
- `power_save` : particules, trails et effets emissive réduits.

Le gameplay et les informations importantes ne doivent jamais dépendre uniquement d'une animation ou d'une couleur.

## Références générées déjà validées

Les images de production déjà générées constituent les références visuelles et non de simples inspirations :

- `Infographie néon Empire Tycoon complète.png`
- `image-gen-1.png`
- `Présentation AAA des business de jeu idle.png`
- `Infographie futuriste d’un empire tycoon мобильe.png`
- `image-gen-1(1).png`

Ces références définissent notamment le rendu isométrique, le traitement néon, les tiers des business, l'UI, les icônes, le système de layers et les états Reduced Motion / Power Save.

## Règle de production

Tout nouvel asset doit être vérifié contre ce document avant intégration. Une production qui ne respecte pas l'identité ci-dessus doit être rejetée plutôt que d'introduire une seconde direction artistique.
