# Manual Test Checklist — Glyph

## Phase 0 — Key Spike (`:prototype:key-spike`)

- [ ] Overlay permission granted
- [ ] Key Spike accessibility service enabled
- [ ] BT keyboard paired; Samsung Keyboard default IME
- [ ] Ctrl+Space logged in overlay / logcat (`KeySpike`)
- [ ] Alt+Shift logged
- [ ] Shift+Space logged
- [ ] Events visible with Chrome focused, home screen, text field
- [ ] Overlay draggable
- [ ] Results recorded in `docs/phase0-results.md`

## Production App (`:app`)

### Permissions & onboarding

- [ ] Onboarding shows restricted-settings hint on API 33+
- [ ] Accessibility enable step works
- [ ] Overlay permission step works
- [ ] Battery optimization step opens settings
- [ ] Settings status card reflects permission state after refresh
- [ ] User-facing name shows **Glyph** in launcher and onboarding

### Languages (N-language cycle)

- [ ] Default languages EN + RU shown in ordered list
- [ ] Add language from catalog (e.g. DE, FR, UA)
- [ ] Remove language (blocked when only two remain)
- [ ] Drag-and-drop reorder persists after app restart
- [ ] Rename display label updates overlay text
- [ ] Language order documented to match Samsung Keyboard
- [ ] Shortcut cycles EN → RU → UA → … → wrap to first
- [ ] Tap on overlay cycles same order as shortcut
- [ ] Removing active language snaps to first remaining

### Overlay visibility (input session)

- [ ] **Auto mode:** overlay hidden on home screen / launcher
- [ ] **Auto mode:** overlay appears when focusing text field (Notes, Chrome, etc.)
- [ ] **Auto mode:** overlay hides ~400ms after leaving text field
- [ ] **Auto mode:** overlay stays visible while soft keyboard (IME) shown
- [ ] **Always show:** overlay visible outside text fields
- [ ] Rotation preserves overlay when session active
- [ ] Draggable; position persists after drag
- [ ] Opacity slider updates overlay (min 0.4)
- [ ] Font size slider updates overlay
- [ ] System / Light / Dark appearance modes
- [ ] Reset position returns to top-right area

### Shortcuts

- [ ] Alt+Shift cycles layout (when Samsung shortcut = Alt+Shift)
- [ ] Ctrl+Space cycles layout (when Samsung shortcut = Ctrl+Space)
- [ ] Typing in text fields unaffected (events not consumed)

### Lifecycle

- [ ] Current language persists across service restart
- [ ] Reboot with `startAtBoot` on: overlay returns when a11y enabled and session active
- [ ] Boot health notification if permissions missing after 60s
- [ ] Screen rotation keeps overlay in correct relative position

### Battery benchmarks

- [ ] Run `scripts/benchmark-battery.ps1` scenario S1 (a11y only, overlay hidden)
- [ ] Run scenario S2 (always show overlay visible)
- [ ] Run scenario S3 (auto mode, no focused field)
- [ ] Run scenario S4 (active typing 30 min)
- [ ] Record results in `docs/benchmarks/YYYY-MM-DD.md`
- [ ] S3 shows lower CPU than S2 (target ≥40% reduction)

### Samsung-specific

- [ ] App excluded from Sleeping apps
- [ ] Service survives 30+ min idle
- [ ] 3+ language cycle matches Samsung Keyboard order
- [ ] DeX: overlay on tablet display (default display only — document if missing on external)
