import React from 'react';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { Card, SectionTitle, StatusPill } from '../components/ui';
import type { AuthUser } from '../services/auth.client';
import { colors, fontFamilies, radius, spacing, typography } from '../theme';
import { ScreenHeading, screenStyles } from './shared';

export function ProfileScreen({ apiOnline, apiUrl, user, onLogout }: { apiOnline: boolean | null; apiUrl: string; user: AuthUser; onLogout: () => void }) {
  const initials = user.name.split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]?.toUpperCase()).join('') || 'U';
  return (
    <ScrollView contentContainerStyle={screenStyles.content} showsVerticalScrollIndicator={false}>
      <ScreenHeading eyebrow="CUENTA Y CONFIGURACIÓN" title="Perfil" subtitle="Tu cuenta y el estado de los servicios de Nodara ERP." />
      <Card style={styles.profileCard}>
        <View style={styles.avatar}><Text style={styles.initials}>{initials}</Text></View>
        <View style={styles.copy}><Text style={styles.name}>{user.name}</Text><Text style={styles.detail}>{user.email}</Text></View>
        <StatusPill label={user.roles.includes('admin') ? 'Administrador' : 'Usuario'} tone="info" />
      </Card>
      <Card>
        <SectionTitle title="Estado del sistema" />
        <View style={styles.setting}><View style={styles.copy}><Text style={styles.settingTitle}>API de Nodara ERP</Text><Text numberOfLines={1} style={styles.detail}>{apiUrl}</Text></View><StatusPill label={apiOnline === null ? 'Comprobando' : apiOnline ? 'En línea' : 'Sin conexión'} tone={apiOnline ? 'success' : apiOnline === false ? 'danger' : 'info'} /></View>
        <View style={[styles.setting, styles.last]}><View style={styles.copy}><Text style={styles.settingTitle}>Sincronización</Text><Text style={styles.detail}>La aplicación Android y la web utilizan la misma cuenta y datos.</Text></View><StatusPill label="Activa" tone="success" /></View>
      </Card>
      <Pressable accessibilityRole="button" onPress={onLogout} style={({ pressed }) => [styles.logout, pressed && styles.pressed]}><Text style={styles.logoutText}>Cerrar sesión</Text></Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  profileCard: { flexDirection: 'row', alignItems: 'center', gap: spacing.md },
  avatar: { width: 58, height: 58, borderRadius: 20, backgroundColor: colors.primaryDark, borderWidth: 1, borderColor: colors.primaryBright, alignItems: 'center', justifyContent: 'center' },
  initials: { color: colors.paper, fontSize: 19, fontWeight: '900', fontFamily: fontFamilies.heading }, copy: { flex: 1, minWidth: 100 }, name: { ...typography.section, color: colors.text, fontWeight: '800' },
  detail: { ...typography.label, color: colors.textMuted, marginTop: 4, fontFamily: fontFamilies.body },
  setting: { flexDirection: 'row', alignItems: 'center', gap: 12, paddingVertical: 14, borderBottomWidth: 1, borderBottomColor: colors.border }, last: { borderBottomWidth: 0 }, settingTitle: { ...typography.body, color: colors.text, fontWeight: '700', fontFamily: fontFamilies.label },
  logout: { minHeight: 46, alignItems: 'center', justifyContent: 'center', borderRadius: radius.md, borderWidth: 1, borderColor: `${colors.danger}70`, backgroundColor: `${colors.danger}12` }, logoutText: { ...typography.label, color: colors.danger, fontWeight: '800' }, pressed: { opacity: 0.7 }
});
