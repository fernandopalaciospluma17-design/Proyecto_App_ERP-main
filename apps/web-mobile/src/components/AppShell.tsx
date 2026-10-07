import React, { type PropsWithChildren, useState } from 'react';
import { Platform, Pressable, SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, useWindowDimensions, View } from 'react-native';
import { Brand, IconButton, SearchField, StatusPill } from './ui';
import { colors, fontFamilies, radius, shadows, spacing, typography } from '../theme';
import type { AuthUser } from '../services/auth.client';

export type ScreenKey = 'dashboard' | 'sales' | 'inventory' | 'contacts' | 'purchases' | 'finance' | 'projects' | 'team' | 'activity' | 'reports' | 'profile';

const navigation: Array<{ key: ScreenKey; label: string; glyph: string; roles?: string[] }> = [
  { key: 'dashboard', label: 'Dashboard', glyph: '⌂' },
  { key: 'sales', label: 'Ventas', glyph: '↗', roles: ['sales', 'accounting'] },
  { key: 'purchases', label: 'Compras', glyph: '▣', roles: ['purchasing', 'accounting'] },
  { key: 'inventory', label: 'Inventario', glyph: '◫', roles: ['sales', 'purchasing'] },
  { key: 'contacts', label: 'Contactos', glyph: '◎', roles: ['sales', 'accounting', 'purchasing', 'projects'] },
  { key: 'finance', label: 'Finanzas', glyph: '$', roles: ['accounting'] },
  { key: 'projects', label: 'Proyectos', glyph: '◇', roles: ['projects', 'sales'] },
  { key: 'team', label: 'Equipo', glyph: '♙', roles: ['admin'] },
  { key: 'activity', label: 'Actividad', glyph: '◷', roles: ['admin'] },
  { key: 'reports', label: 'Reportes', glyph: '▤', roles: ['accounting', 'sales'] },
  { key: 'profile', label: 'Perfil', glyph: '○' }
];

export function AppShell({
  active,
  onNavigate,
  onOpenCommands,
  apiOnline,
  user,
  children
}: PropsWithChildren<{
  active: ScreenKey;
  onNavigate: (screen: ScreenKey) => void;
  onOpenCommands: () => void;
  apiOnline: boolean | null;
  user: AuthUser;
}>) {
  const { width } = useWindowDimensions();
  const desktop = width >= 900;
  const [moreOpen, setMoreOpen] = useState(false);
  const [focusedMobileItem, setFocusedMobileItem] = useState<ScreenKey | null>(null);
  const [focusedMoreItem, setFocusedMoreItem] = useState<ScreenKey | null>(null);
  const [focusedMoreButton, setFocusedMoreButton] = useState(false);
  const [focusedCommandButton, setFocusedCommandButton] = useState(false);
  const allowedNavigation = navigation.filter((item) => user.roles.includes('admin') || !item.roles || item.roles.some((role) => user.roles.includes(role)));
  const activeItem = allowedNavigation.find((item) => item.key === active);
  const activeLabel = activeItem?.label ?? 'Dashboard';
  const mobileNavigation = allowedNavigation.filter((item) => ['dashboard', 'sales', 'inventory', 'contacts'].includes(item.key)).slice(0, 4);
  const mobileMoreNavigation = allowedNavigation.filter((item) => !mobileNavigation.some((main) => main.key === item.key));
  const moreActive = mobileMoreNavigation.some((item) => item.key === active);

  function navigate(screen: ScreenKey) {
    setMoreOpen(false);
    onNavigate(screen);
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.shell}>
        {desktop ? (
          <View style={styles.sidebar}>
            <Brand />
            <ScrollView style={styles.sideNav} contentContainerStyle={styles.sideNavContent} showsVerticalScrollIndicator={false}>
              {allowedNavigation.map((item) => (
                <NavButton key={item.key} active={item.key === active} item={item} onPress={() => navigate(item.key)} />
              ))}
            </ScrollView>
            <View style={styles.sideFooter}>
              <View style={styles.workspaceIcon}><Text style={styles.workspaceInitial}>{user.name.slice(0, 1).toUpperCase()}</Text></View>
              <View style={styles.workspaceCopy}><Text numberOfLines={1} style={styles.workspaceName}>{user.name}</Text><Text style={styles.workspaceRole}>{user.roles.includes('admin') ? 'Administrador' : 'Usuario'}</Text></View>
              <Text style={styles.chevron}>›</Text>
            </View>
          </View>
        ) : null}

        <View style={styles.main}>
          <View style={[styles.topbar, !desktop && styles.topbarMobile]}>
            <View style={styles.topbarRow}>
              {!desktop ? <Brand compact tone="light" /> : null}  

              <View style={styles.topActions}>
                {desktop ? (
                  <>
                    <StatusPill label={apiOnline ? 'API en línea' : apiOnline === false ? 'Modo demo' : 'Conectando'} tone={apiOnline ? 'success' : apiOnline === false ? 'warning' : 'info'} />
                    <View style={styles.topUser}>
                      <View style={styles.topUserIcon}><Text style={styles.topUserInitial}>{user.name.slice(0, 1).toUpperCase()}</Text></View>
                      <Text numberOfLines={1} style={styles.topUserName}>{user.name}</Text>
                    </View>
                    <IconButton label="Abrir búsqueda rápida" glyph="⌕" />
                  </>
                ) : null}
                <Pressable accessibilityLabel="Abrir comandos" accessibilityRole="button" onBlur={() => setFocusedCommandButton(false)} onFocus={() => setFocusedCommandButton(true)} onHoverIn={() => setFocusedCommandButton(true)} onHoverOut={() => setFocusedCommandButton(false)} onPress={onOpenCommands} style={[styles.commandButton, focusedCommandButton && styles.commandFocused]}>
                  <Text style={styles.commandGlyph}>⌘</Text>
                </Pressable>
              </View>
            </View>
            {!desktop ? <Text numberOfLines={1} style={styles.mobileSectionTitle}>{activeLabel}</Text> : null}
          </View>
          <View style={styles.content}>{children}</View>
          {!desktop ? (
            <>
              {moreOpen ? <View style={styles.moreMenu}>
                <Text style={styles.moreTitle}>MÁS OPCIONES</Text>
                <ScrollView style={styles.moreScroll}>{mobileMoreNavigation.map((item) => <Pressable key={item.key} accessibilityRole="button" accessibilityState={{ selected: item.key === active }} onBlur={() => setFocusedMoreItem(null)} onFocus={() => setFocusedMoreItem(item.key)} onHoverIn={() => setFocusedMoreItem(item.key)} onHoverOut={() => setFocusedMoreItem((current) => current === item.key ? null : current)} onPress={() => navigate(item.key)} style={({ pressed }) => [styles.moreItem, item.key === active && styles.moreItemActive, focusedMoreItem === item.key && styles.moreFocusVisible, pressed && styles.pressed]}><Text style={[styles.moreGlyph, item.key === active && styles.bottomGlyphActive]}>{item.glyph}</Text><Text style={[styles.moreLabel, item.key === active && styles.bottomLabelActive]}>{item.label}</Text></Pressable>)}</ScrollView>
              </View> : null}
              <View style={styles.bottomNav}>
              {mobileNavigation.map((item) => (
                <Pressable
                  accessibilityLabel={item.label}
                  accessibilityRole="button"
                  accessibilityState={{ selected: item.key === active }}
                  key={item.key}
                  onBlur={() => setFocusedMobileItem(null)}
                  onFocus={() => setFocusedMobileItem(item.key)}
                  onHoverIn={() => setFocusedMobileItem(item.key)}
                  onHoverOut={() => setFocusedMobileItem((current) => current === item.key ? null : current)}
                  onPress={() => navigate(item.key)}
                  style={({ pressed }) => [styles.bottomItem, item.key === active && styles.bottomItemActive, focusedMobileItem === item.key && styles.focusVisible, pressed && styles.pressed]}
                >
                  <Text style={[styles.bottomGlyph, item.key === active && styles.bottomGlyphActive]}>{item.glyph}</Text>
                  <Text numberOfLines={1} style={[styles.bottomLabel, item.key === active && styles.bottomLabelActive]}>{item.label}</Text>
                </Pressable>
              ))}
              <Pressable accessibilityLabel="Más opciones" accessibilityRole="button" accessibilityState={{ expanded: moreOpen, selected: moreActive }} onBlur={() => setFocusedMoreButton(false)} onFocus={() => setFocusedMoreButton(true)} onHoverIn={() => setFocusedMoreButton(true)} onHoverOut={() => setFocusedMoreButton(false)} onPress={() => setMoreOpen((value) => !value)} style={({ pressed }) => [styles.bottomItem, moreActive && styles.bottomItemActive, focusedMoreButton && styles.focusVisible, pressed && styles.pressed]}>
                <Text style={[styles.bottomGlyph, moreActive && styles.bottomGlyphActive]}>•••</Text>
                <Text numberOfLines={1} style={[styles.bottomLabel, moreActive && styles.bottomLabelActive]}>Más</Text>
              </Pressable>
              </View>
            </>
          ) : null}
        </View>
      </View>
    </SafeAreaView>
  );
}

function NavButton({
  active,
  item,
  onPress
}: {
  active: boolean;
  item: { key: ScreenKey; label: string; glyph: string };
  onPress: () => void;
}) {
  const [highlighted, setHighlighted] = useState(false);
  return (
    <Pressable accessibilityRole="button" accessibilityState={{ selected: active }} onBlur={() => setHighlighted(false)} onFocus={() => setHighlighted(true)} onHoverIn={() => setHighlighted(true)} onHoverOut={() => setHighlighted(false)} onPress={onPress} style={({ pressed }) => [styles.navButton, active && styles.navButtonActive, highlighted && styles.focusVisible, pressed && styles.pressed]}>
      <View style={[styles.navGlyphWrap, active && styles.navGlyphWrapActive]}><Text style={[styles.navGlyph, active && styles.navGlyphActive]}>{item.glyph}</Text></View>
      <Text style={[styles.navLabel, active && styles.navLabelActive]}>{item.label}</Text>
      {active ? <View style={styles.activeRail} /> : null}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: colors.background,
    paddingTop: Platform.OS === 'android' ? StatusBar.currentHeight ?? 0 : 0
  },
  ambientTop: { position: 'absolute', width: 500, height: 500, borderRadius: 250, backgroundColor: colors.glow, top: -360, right: -120 },
  ambientSide: { position: 'absolute', width: 340, height: 340, borderRadius: 170, backgroundColor: colors.glow, bottom: -220, left: -180 },
  shell: { flex: 1, flexDirection: 'row' },
  sidebar: { width: 252, padding: spacing.lg, borderRightWidth: 1, borderRightColor: colors.sidebarBorder, backgroundColor: colors.sidebarBackground },
  sideNav: { flex: 1, marginTop: spacing.lg }, sideNavContent: { gap: 5, paddingBottom: spacing.md },
  navButton: { minHeight: 50, flexDirection: 'row', alignItems: 'center', gap: 12, borderRadius: radius.md, paddingHorizontal: 10, position: 'relative' },
  navButtonActive: { backgroundColor: colors.glow, borderWidth: 1, borderColor: colors.sidebarBorder },
  navGlyphWrap: { width: 32, height: 32, borderRadius: 11, alignItems: 'center', justifyContent: 'center' },
  navGlyphWrapActive: { backgroundColor: colors.primaryDark },
  navGlyph: { color: colors.sidebarMuted, fontSize: 17, fontFamily: fontFamilies.body },
  navGlyphActive: { color: colors.signature },
  navLabel: { color: colors.sidebarMuted, fontSize: 13, fontWeight: '600', fontFamily: fontFamilies.label },
  navLabelActive: { color: colors.sidebarText },
  activeRail: { position: 'absolute', width: 3, height: 22, right: -1, borderRadius: 3, backgroundColor: colors.signature, shadowColor: colors.signature, shadowOpacity: 0.6, shadowRadius: 9 },
  sideFooter: { flexDirection: 'row', alignItems: 'center', gap: 10, borderTopWidth: 1, borderTopColor: colors.sidebarBorder, paddingTop: spacing.md },
  workspaceIcon: { width: 38, height: 38, borderRadius: 12, backgroundColor: colors.primaryDark, alignItems: 'center', justifyContent: 'center' },
  workspaceInitial: { color: colors.sidebarText, fontWeight: '800', fontFamily: fontFamilies.heading },
  workspaceCopy: { flex: 1 },
  workspaceName: { color: colors.sidebarText, fontSize: 11, fontWeight: '700', fontFamily: fontFamilies.labelBold },
  workspaceRole: { color: colors.sidebarDim, fontSize: 10, marginTop: 3, fontFamily: fontFamilies.body },
  chevron: { color: colors.sidebarDim, fontSize: 20 },
  main: { flex: 1, minWidth: 0 },
  topbar: { minHeight: 76, justifyContent: 'center', paddingHorizontal: spacing.lg, paddingVertical: spacing.sm, borderBottomWidth: 1, borderBottomColor: colors.line, backgroundColor: colors.paper },
  topbarMobile: { minHeight: 88, paddingHorizontal: spacing.md, paddingVertical: spacing.xs },
  topbarRow: { flexDirection: 'row', alignItems: 'center', gap: spacing.md },
  sectionContext: { minWidth: 112, maxWidth: 170 },
  sectionEyebrow: { color: colors.muted, fontSize: 10, fontWeight: '700', letterSpacing: 1.1, fontFamily: fontFamilies.label },
  sectionTitle: { ...typography.section, color: colors.text, marginTop: 2 },
  mobileSectionTitle: { color: colors.textMuted, fontSize: 12, fontFamily: fontFamilies.label, marginTop: spacing.xs, marginLeft: 2 },
  topActions: { flexDirection: 'row', alignItems: 'center', gap: 9 },
  topUser: { flexDirection: 'row', alignItems: 'center', gap: 8, maxWidth: 150 },
  topUserIcon: { width: 34, height: 34, borderRadius: 12, backgroundColor: colors.moss, alignItems: 'center', justifyContent: 'center' },
  topUserInitial: { color: colors.mist, fontFamily: fontFamilies.labelBold },
  topUserName: { color: colors.text, fontSize: 11, fontFamily: fontFamilies.label, maxWidth: 104 },
  commandButton: { width: 44, height: 44, borderRadius: 14, alignItems: 'center', justifyContent: 'center', backgroundColor: colors.signature, borderWidth: 1, borderColor: colors.signature },
  commandGlyph: { color: colors.ink, fontSize: 17, fontWeight: '700', fontFamily: fontFamilies.heading },
  content: { flex: 1, minWidth: 0 },
  bottomNav: { position: 'absolute', left: 8, right: 8, bottom: Platform.OS === 'ios' ? 8 : 6, minHeight: 66, borderRadius: 22, borderWidth: 1, borderColor: colors.sidebarBorder, backgroundColor: colors.sidebarBackground, flexDirection: 'row', alignItems: 'center', paddingHorizontal: 5, paddingVertical: 5, ...shadows.floating },
  bottomItem: { flex: 1, minWidth: 0, height: 54, borderRadius: 17, alignItems: 'center', justifyContent: 'center', gap: 2 },
  bottomItemActive: { backgroundColor: colors.glow },
  bottomGlyph: { color: colors.sidebarDim, fontSize: 18, height: 22, fontFamily: fontFamilies.body },
  bottomGlyphActive: { color: colors.signature },
  bottomLabel: { color: colors.sidebarDim, fontSize: 10, fontWeight: '600', fontFamily: fontFamilies.label },
  bottomLabelActive: { color: colors.sidebarText },
  moreMenu: { position: 'absolute', right: 12, bottom: Platform.OS === 'ios' ? 82 : 78, width: 190, borderRadius: 18, borderWidth: 1, borderColor: colors.borderStrong, backgroundColor: colors.cardElevated, padding: 8, ...shadows.floating },
  moreScroll: { maxHeight: 390 },
  moreTitle: { color: colors.textDim, fontSize: 10, fontWeight: '900', letterSpacing: 1.2, paddingHorizontal: 10, paddingVertical: 7, fontFamily: fontFamilies.labelBold },
  moreItem: { minHeight: 48, borderRadius: 13, flexDirection: 'row', alignItems: 'center', gap: 11, paddingHorizontal: 12 },
  moreItemActive: { backgroundColor: colors.glow }, moreGlyph: { color: colors.textMuted, fontSize: 17, fontFamily: fontFamilies.body }, moreLabel: { color: colors.textMuted, fontSize: 12, fontWeight: '700', fontFamily: fontFamilies.label },
  pressed: { opacity: 0.7 },
  focusVisible: { borderWidth: 2, borderColor: colors.signature, backgroundColor: colors.glow },
  moreFocusVisible: { borderWidth: 2, borderColor: colors.primaryBright, backgroundColor: colors.glow },
  commandFocused: { borderWidth: 2, borderColor: colors.ink }
});
