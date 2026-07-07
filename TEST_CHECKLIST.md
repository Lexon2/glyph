# Manual Test Checklist

## Phase 0 — Key Spike (`:prototype:key-spike`)

- [ ] Overlay permission granted
- [ ] Key Spike accessibility service enabled
- [ ] BT keyboard paired; Samsung Keyboard default IME
- [ ] Ctrl+Space logged in overlay / logcat (`KeySpike`)
- [ ] Alt+Shift logged
- [ ] Shift+Space logged
- [ ] Events visible with Chrome focused, home screen, text field
- [ ] Overlay draggable

## Production App (`:app`)

### Permissions & onboarding

- [ ] Onboarding shows restricted-settings hint on API 33+
- [ ] Accessibility enable step works
- [ ] Overlay permission step works
- [ ] Battery optimization step opens settings
- [ ] Settings status card reflects permission state after refresh

### Overlay

- [ ] Overlay appears after enabling accessibility + overlay permission
- [ ] Shows EN / RU / UA label
- [ ] Draggable; position persists after drag
- [ ] Tap toggles between configured language pair
- [ ] Opacity slider updates overlay (min 0.4)
- [ ] Font size slider updates overlay
- [ ] System / Light / Dark appearance modes
- [ ] Reset position returns to top-right area

### Shortcuts

- [ ] Alt+Shift toggles layout (when Samsung shortcut = Alt+Shift)
- [ ] Ctrl+Space toggles layout (when Samsung shortcut = Ctrl+Space)
- [ ] Typing in text fields unaffected (events not consumed)

### Lifecycle

- [ ] Layout persists across service restart
- [ ] Reboot with `startAtBoot` on: overlay returns when a11y enabled
- [ ] Boot health notification if permissions missing after 60s
- [ ] Screen rotation keeps overlay in correct relative position

### Samsung-specific

- [ ] App excluded from Sleeping apps
- [ ] Service survives 30+ min idle
- [ ] DeX: overlay on tablet display (default display only — document if missing on external)
