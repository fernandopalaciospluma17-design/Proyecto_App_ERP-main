import React, { useEffect, useMemo, useState } from 'react';
import { Modal, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import type { ScreenKey } from './AppShell';
import { ModalSurface } from './ui';
import { colors, fontFamilies, radius, spacing, typography } from '../theme';

const commands: Array<{ label: string; hint: string; screen: ScreenKey; glyph: string; roles?: string[] }> = [
  { label: 'Abrir dashboard', hint: 'Resumen de la operación', screen: 'dashboard', glyph: '⌂' },
  { label: 'Consultar ventas', hint: 'Facturas y cobros', screen: 'sales', glyph: '↗' },
  { label: 'Revisar inventario', hint: 'Existencias y alertas', screen: 'inventory', glyph: '◫' },
  { label: 'Buscar contactos', hint: 'Clientes y proveedores', screen: 'contacts', glyph: '◎' },
  { label: 'Gestionar compras', hint: 'Órdenes y recepción de mercancía', screen: 'purchases', glyph: '▣', roles: ['admin', 'purchasing', 'accounting'] },
  { label: 'Abrir finanzas', hint: 'Ingresos, gastos y saldo', screen: 'finance', glyph: '$', roles: ['admin', 'accounting'] },
  { label: 'Revisar proyectos', hint: 'Avance y presupuestos', screen: 'projects', glyph: '◇', roles: ['admin', 'projects', 'sales'] },
  { label: 'Administrar equipo', hint: 'Usuarios, roles y acceso', screen: 'team', glyph: '♙', roles: ['admin'] },
  { label: 'Consultar actividad', hint: 'Auditoría y trazabilidad', screen: 'activity', glyph: '◷', roles: ['admin'] },
  { label: 'Consultar reportes', hint: 'Indicadores, PDF y Excel', screen: 'reports', glyph: '▤' },
  { label: 'Estado del sistema', hint: 'Perfil y conexión', screen: 'profile', glyph: '○' }
];

export function CommandPalette({
  visible,
  onClose,
  onSelect,
  roles
}: {
  visible: boolean;
  onClose: () => void;
  onSelect: (screen: ScreenKey) => void;
  roles: string[];
}) {
  const [query, setQuery] = useState('');
  const [focusedCommand, setFocusedCommand] = useState<ScreenKey | null>(null);
  useEffect(() => {
    if (!visible) {
      setQuery('');
      setFocusedCommand(null);
    }
  }, [visible]);
  const filtered = useMemo(() => commands.filter((command) => (!command.roles || command.roles.some((role) => roles.includes(role))) && `${command.label} ${command.hint}`.toLowerCase().includes(query.toLowerCase())), [query, roles]);

  return (
    <Modal visible={visible} transparent animationType="none" onRequestClose={onClose}>
      <Pressable accessibilityRole="button" accessibilityLabel="Cerrar comandos" style={styles.backdrop} onPress={onClose}>
        <ModalSurface visible={visible} style={styles.panelMotion}>
        <Pressable style={styles.panel} onPress={() => undefined}>
          <View style={styles.inputWrap}>
            <Text style={styles.searchGlyph}>⌕</Text>
            <TextInput
              accessibilityLabel="Buscar comandos"
              autoFocus
              value={query}
              onChangeText={setQuery}
              placeholder="Buscar acciones o módulos"
              placeholderTextColor={colors.textDim}
              style={styles.input}
            />
            <View style={styles.escape}><Text style={styles.escapeText}>ESC</Text></View>
          </View>
          <Text style={styles.groupLabel}>NAVEGACIÓN RÁPIDA</Text>
          {filtered.map((command) => (
            <Pressable key={command.screen} accessibilityRole="button" onFocus={() => setFocusedCommand(command.screen)} onBlur={() => setFocusedCommand(null)} onHoverIn={() => setFocusedCommand(command.screen)} onHoverOut={() => setFocusedCommand((current) => current === command.screen ? null : current)} onPress={() => onSelect(command.screen)} style={({ pressed }) => [styles.command, pressed && styles.pressed, focusedCommand === command.screen && styles.focused]}>
              <View style={styles.commandIcon}><Text style={styles.commandGlyph}>{command.glyph}</Text></View>
              <View style={styles.commandCopy}><Text style={styles.commandLabel}>{command.label}</Text><Text style={styles.commandHint}>{command.hint}</Text></View>
              <Text style={styles.arrow}>›</Text>
            </Pressable>
          ))}
        </Pressable>
        </ModalSurface>
      </Pressable>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: { flex: 1, backgroundColor: colors.overlay, alignItems: 'center', paddingTop: 86, paddingHorizontal: 16 },
  panelMotion: { width: '100%', maxWidth: 580 },
  panel: { width: '100%', maxWidth: 580, backgroundColor: colors.card, padding: spacing.md, borderRadius: radius.lg, borderWidth: 1, borderColor: colors.borderStrong, shadowColor: colors.primary, shadowOpacity: 0.18, shadowRadius: 34 },
  inputWrap: { height: 52, flexDirection: 'row', alignItems: 'center', gap: 10, borderWidth: 1, borderColor: colors.borderStrong, backgroundColor: colors.backgroundSoft, borderRadius: radius.md, paddingHorizontal: 13 },
  searchGlyph: { color: colors.primaryBright, fontSize: 20, fontFamily: fontFamilies.heading },
  input: { ...typography.body, flex: 1, color: colors.text, outlineStyle: 'none' } as never,
  escape: { paddingHorizontal: 7, paddingVertical: 4, borderWidth: 1, borderColor: colors.border, borderRadius: 6 },
  escapeText: { ...typography.caption, color: colors.textDim, fontWeight: '700', fontFamily: fontFamilies.mono },
  groupLabel: { ...typography.caption, color: colors.textDim, fontWeight: '800', letterSpacing: 1.2, marginTop: spacing.md, marginBottom: 6 },
  command: { minHeight: 62, flexDirection: 'row', alignItems: 'center', gap: 12, paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: colors.border },
  commandIcon: { width: 38, height: 38, alignItems: 'center', justifyContent: 'center', borderRadius: 12, backgroundColor: colors.glow },
  commandGlyph: { color: colors.primaryBright, fontSize: 17, fontFamily: fontFamilies.heading },
  commandCopy: { flex: 1 },
  commandLabel: { ...typography.label, color: colors.text, fontWeight: '700' },
  commandHint: { ...typography.caption, color: colors.textMuted, marginTop: 3, fontFamily: fontFamilies.body },
  arrow: { color: colors.textDim, fontSize: 21, fontFamily: fontFamilies.heading },
  focused: { borderColor: colors.primaryBright, borderWidth: 2, backgroundColor: colors.glow },
  pressed: { opacity: 0.65 }
});
