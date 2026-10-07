import React, { useEffect, useRef, useState, type PropsWithChildren } from 'react';
import {
  AccessibilityInfo,
  Animated,
  Easing,
  Image,
  Pressable,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  View,
  type ImageStyle,
  type StyleProp,
  type TextInputProps,
  type ViewStyle
} from 'react-native';
import { colors, fontFamilies, radius, shadows, spacing, typography } from '../theme';
import type { StatusTone } from '../data/demo';

export function useReducedMotion() {
  const [reducedMotion, setReducedMotion] = useState(true);

  useEffect(() => {
    if (Platform.OS === 'web' && typeof window !== 'undefined' && typeof window.matchMedia === 'function') {
      const media = window.matchMedia('(prefers-reduced-motion: reduce)');
      const updatePreference = (event: MediaQueryListEvent) => setReducedMotion(event.matches);
      setReducedMotion(media.matches);
      media.addEventListener('change', updatePreference);
      return () => media.removeEventListener('change', updatePreference);
    }

    let mounted = true;
    AccessibilityInfo.isReduceMotionEnabled().then((enabled) => {
      if (mounted) setReducedMotion(enabled);
    });
    const subscription = AccessibilityInfo.addEventListener('reduceMotionChanged', setReducedMotion);
    return () => {
      mounted = false;
      subscription.remove();
    };
  }, []);

  return reducedMotion;
}

export function ScreenTransition({ screenKey, children }: PropsWithChildren<{ screenKey: string }>) {
  const reducedMotion = useReducedMotion();
  const opacity = useRef(new Animated.Value(0)).current;
  const translateY = useRef(new Animated.Value(8)).current;

  useEffect(() => {
    if (reducedMotion) {
      opacity.setValue(1);
      translateY.setValue(0);
      return;
    }

    opacity.setValue(0);
    translateY.setValue(8);
    const animation = Animated.parallel([
      Animated.timing(opacity, { toValue: 1, duration: 200, easing: Easing.out(Easing.cubic), useNativeDriver: true }),
      Animated.timing(translateY, { toValue: 0, duration: 200, easing: Easing.out(Easing.cubic), useNativeDriver: true })
    ]);
    animation.start();
    return () => animation.stop();
  }, [opacity, reducedMotion, screenKey, translateY]);

  return <Animated.View style={[styles.screenTransition, { opacity, transform: [{ translateY }] }]}>{children}</Animated.View>;
}

export function ModalSurface({ visible, children, style }: PropsWithChildren<{ visible: boolean; style?: StyleProp<ViewStyle> }>) {
  const reducedMotion = useReducedMotion();
  const progress = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    progress.stopAnimation();
    if (!visible) {
      progress.setValue(0);
      return;
    }
    if (reducedMotion) {
      progress.setValue(1);
      return;
    }

    progress.setValue(0);
    const animation = Animated.timing(progress, { toValue: 1, duration: 200, easing: Easing.out(Easing.cubic), useNativeDriver: true });
    animation.start();
    return () => animation.stop();
  }, [progress, reducedMotion, visible]);

  const scale = progress.interpolate({ inputRange: [0, 1], outputRange: [0.98, 1] });
  return <Animated.View style={[style, { opacity: progress, transform: [{ scale }] }]}>{children}</Animated.View>;
}

export function SkeletonBlock({ width = '100%', height = 12 }: { width?: number | `${number}%`; height?: number }) {
  return <View accessibilityElementsHidden importantForAccessibility="no" style={[styles.skeletonBlock, { width, height }]} />;
}

export function Brand({ compact = false, tone = 'dark' }: { compact?: boolean; tone?: 'dark' | 'light' }) {
  return (
    <View style={styles.brand}>
      <Image
        accessibilityLabel="Logotipo de Nodara ERP"
        resizeMode="contain"
        source={tone === 'dark' ? require('../../assets/logo-nodara-dark.png') : require('../../assets/logo-nodara-light.png')}
        style={[styles.logo, compact && styles.logoCompact] as StyleProp<ImageStyle>}
      />
      {!compact ? <Text style={[styles.brandTag, tone === 'dark' && styles.brandTagDark]}>Gestión empresarial conectada</Text> : null}
    </View>
  );
}

export function Card({ children, style }: PropsWithChildren<{ style?: StyleProp<ViewStyle> }>) {
  return <View style={[styles.card, style]}>{children}</View>;
}

export function Button({
  label,
  onPress,
  variant = 'primary',
  disabled = false,
  loading = false
}: {
  label: string;
  onPress: () => void;
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  disabled?: boolean;
  loading?: boolean;
}) {
  const [focused, setFocused] = useState(false);
  const [hovered, setHovered] = useState(false);
  const unavailable = disabled || loading;
  const variantStyle = {
    primary: styles.button_primary,
    secondary: styles.button_secondary,
    danger: styles.button_danger,
    ghost: styles.button_ghost
  }[variant];
  const variantLabelStyle = {
    primary: styles.buttonLabel_primary,
    secondary: styles.buttonLabel_secondary,
    danger: styles.buttonLabel_danger,
    ghost: styles.buttonLabel_ghost
  }[variant];

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityState={{ disabled: unavailable, busy: loading }}
      disabled={unavailable}
      onBlur={() => setFocused(false)}
      onFocus={() => setFocused(true)}
      onHoverIn={() => setHovered(true)}
      onHoverOut={() => setHovered(false)}
      onPress={onPress}
      style={({ pressed }) => [
        styles.button,
        variantStyle,
        focused && styles.focused,
        hovered && styles.hovered,
        unavailable && styles.disabled,
        pressed && !unavailable && styles.pressed
      ]}
    >
      <Text style={[styles.buttonLabel, variantLabelStyle]}>
        {loading ? 'Procesando…' : label}
      </Text>
    </Pressable>
  );
}

export function Input({
  label,
  error,
  disabled = false,
  editable = true,
  style,
  onFocus,
  onBlur,
  ...inputProps
}: TextInputProps & { label?: string; error?: string; disabled?: boolean }) {
  const [focused, setFocused] = useState(false);
  const unavailable = disabled || !editable;

  function handleFocus(event: Parameters<NonNullable<TextInputProps['onFocus']>>[0]) {
    setFocused(true);
    onFocus?.(event);
  }

  function handleBlur(event: Parameters<NonNullable<TextInputProps['onBlur']>>[0]) {
    setFocused(false);
    onBlur?.(event);
  }

  return (
    <View style={styles.inputField}>
      {label ? <Text style={styles.inputLabel}>{label}</Text> : null}
      <TextInput
        {...inputProps}
        accessibilityHint={error ?? inputProps.accessibilityHint}
        accessibilityState={{ disabled: unavailable }}
        editable={!unavailable}
        onBlur={handleBlur}
        onFocus={handleFocus}
        placeholderTextColor={colors.textDim}
        selectionColor={colors.signature}
        style={[
          styles.input,
          focused && styles.inputFocused,
          error && styles.inputInvalid,
          unavailable && styles.inputDisabled,
          style
        ]}
      />
      {error ? <Text accessibilityRole="alert" style={styles.inputError}><Text style={styles.inputErrorGlyph}>! </Text>{error}</Text> : null}
    </View>
  );
}

export function SectionTitle({ title, action }: { title: string; action?: string }) {
  return (
    <View style={styles.sectionTitleRow}>
      <Text numberOfLines={1} style={styles.sectionTitle}>{title}</Text>
      {action ? <Text numberOfLines={1} style={styles.sectionAction}>{action}</Text> : null}
    </View>
  );
}

const toneColor: Record<StatusTone, string> = {
  success: colors.success,
  warning: colors.warning,
  danger: colors.danger,
  info: colors.moss
};

const toneGlyph: Record<StatusTone, string> = {
  success: '✓',
  warning: '!',
  danger: '×',
  info: 'i'
};

export function StatusPill({ label, tone = 'info' }: { label: string; tone?: StatusTone }) {
  const color = toneColor[tone];
  return (
    <View accessibilityRole="text" style={[styles.pill, { borderColor: `${color}70`, backgroundColor: `${color}20` }]}>
      <Text style={styles.pillGlyph}>{toneGlyph[tone]}</Text>
      <Text style={styles.pillText}>{label}</Text>
    </View>
  );
}

export function MetricCard({
  label,
  value,
  delta,
  icon,
  tone = 'info'
}: {
  label: string;
  value: string;
  delta: string;
  icon: string;
  tone?: StatusTone;
}) {
  const color = toneColor[tone];
  return (
    <Card style={styles.metricCard}>
      <View style={styles.metricTop}>
        <View style={[styles.metricIcon, { borderColor: `${color}45`, backgroundColor: `${color}16` }]}>
          <Text style={[styles.metricIconText, { color }]}>{icon}</Text>
        </View>
        <Text style={[styles.metricDelta, { color }]}>{delta}</Text>
      </View>
      <Text style={styles.metricValue}>{value}</Text>
      <Text style={styles.metricLabel}>{label}</Text>
    </Card>
  );
}

export function SearchField({ label = 'Buscar en el ERP' }: { label?: string }) {
  return (
    <View accessibilityRole="search" style={styles.search}>
      <Text style={styles.searchIcon}>⌕</Text>
      <Text style={styles.searchPlaceholder}>{label}</Text>
      <View style={styles.shortcut}><Text style={styles.shortcutText}>⌘ K</Text></View>
    </View>
  );
}

export function IconButton({ label, glyph }: { label: string; glyph: string }) {
  const [focused, setFocused] = useState(false);
  const [hovered, setHovered] = useState(false);
  return (
    <Pressable accessibilityLabel={label} accessibilityRole="button" onBlur={() => setFocused(false)} onFocus={() => setFocused(true)} onHoverIn={() => setHovered(true)} onHoverOut={() => setHovered(false)} style={({ pressed }) => [styles.iconButton, focused && styles.focused, hovered && styles.hovered, pressed && styles.pressed]}>
      <Text style={styles.iconButtonText}>{glyph}</Text>
    </Pressable>
  );
}

export function ProgressBar({ value, color = colors.primary }: { value: number; color?: string }) {
  return (
    <View accessibilityLabel={`${value}%`} accessibilityRole="progressbar" style={styles.progressTrack}>
      <View style={[styles.progressFill, { width: `${Math.min(100, Math.max(0, value))}%`, backgroundColor: color }]} />
    </View>
  );
}

export function EmptyState({ title, description }: { title: string; description: string }) {
  return (
    <Card style={styles.emptyState}>
      <View style={styles.emptyIcon}><Text style={styles.emptyIconText}>◇</Text></View>
      <Text style={styles.emptyTitle}>{title}</Text>
      <Text style={styles.emptyDescription}>{description}</Text>
    </Card>
  );
}

const styles = StyleSheet.create({
  screenTransition: { flex: 1 },
  skeletonBlock: { borderRadius: radius.sm, backgroundColor: colors.backgroundSoft },
  brand: { alignItems: 'flex-start', gap: 3 },
  logo: { width: 168, height: 50 },
  logoCompact: { width: 134, height: 40 },
  brandTag: { color: colors.textMuted, fontSize: 11, marginTop: 2, fontFamily: fontFamilies.body },
  brandTagDark: { color: colors.sidebarMuted },
  card: {
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radius.lg,
    padding: spacing.md,
    ...shadows.card
  },
  button: { minHeight: 44, borderWidth: 1, borderRadius: radius.md, alignItems: 'center', justifyContent: 'center', paddingHorizontal: spacing.md },
  button_primary: { backgroundColor: colors.signature, borderColor: colors.signature },
  button_secondary: { backgroundColor: colors.surface, borderColor: colors.borderStrong },
  button_danger: { backgroundColor: colors.danger, borderColor: colors.danger },
  button_ghost: { backgroundColor: 'transparent', borderColor: 'transparent' },
  focused: { borderWidth: 2, borderColor: colors.ink },
  hovered: { borderColor: colors.signature },
  disabled: { opacity: 0.55 },
  buttonLabel: { ...typography.label },
  buttonLabel_primary: { color: colors.ink },
  buttonLabel_secondary: { color: colors.text },
  buttonLabel_danger: { color: colors.paper },
  buttonLabel_ghost: { color: colors.textMuted },
  inputField: { gap: spacing.xs },
  inputLabel: { ...typography.label, color: colors.textMuted },
  input: { ...typography.body, minHeight: 46, borderRadius: radius.md, borderWidth: 1, borderColor: colors.borderStrong, backgroundColor: colors.surface, color: colors.text, paddingHorizontal: spacing.md },
  inputFocused: { borderWidth: 2, borderColor: colors.primaryBright },
  inputInvalid: { borderColor: colors.danger },
  inputDisabled: { opacity: 0.55 },
  inputError: { ...typography.label, color: colors.text },
  inputErrorGlyph: { color: colors.danger, fontFamily: fontFamilies.labelBold },
  pillGlyph: { ...typography.caption, minWidth: 12, textAlign: 'center', color: colors.text, fontFamily: fontFamilies.labelBold },
  pillText: { ...typography.caption, fontWeight: '700', flexShrink: 1, color: colors.text, fontFamily: fontFamilies.labelBold },
  sectionTitleRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: spacing.md },
  sectionTitle: { color: colors.text, fontSize: 17, fontWeight: '700', flexShrink: 1, fontFamily: fontFamilies.heading },
  sectionAction: { color: colors.signature, fontSize: 12, fontWeight: '600', flexShrink: 1, textAlign: 'right', marginLeft: 10, fontFamily: fontFamilies.bodyMedium },
  pill: { alignSelf: 'flex-start', maxWidth: '100%', flexDirection: 'row', alignItems: 'center', gap: 6, borderWidth: 1, borderRadius: radius.pill, paddingHorizontal: 9, paddingVertical: 5 },
  metricCard: { flex: 1, minWidth: 150, minHeight: 142, justifyContent: 'space-between' },
  metricTop: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', gap: 8 },
  metricIcon: { width: 36, height: 36, borderRadius: 12, borderWidth: 1, alignItems: 'center', justifyContent: 'center' },
  metricIconText: { fontSize: 18, fontWeight: '700', fontFamily: fontFamilies.heading },
  metricDelta: { flex: 1, textAlign: 'right', fontSize: 10, fontWeight: '600', fontFamily: fontFamilies.label },
  metricValue: { ...typography.metric, color: colors.text, fontWeight: '700', marginTop: 14 },
  metricLabel: { ...typography.label, color: colors.textMuted, marginTop: 4, fontFamily: fontFamilies.body },
  search: { height: 44, flexDirection: 'row', alignItems: 'center', gap: 10, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, borderRadius: radius.md, paddingHorizontal: 13, flex: 1, maxWidth: 440 },
  searchIcon: { color: colors.textMuted, fontSize: 20, fontFamily: fontFamilies.body },
  searchPlaceholder: { ...typography.body, color: colors.textMuted, flex: 1 },
  shortcut: { borderWidth: 1, borderColor: colors.borderStrong, borderRadius: 7, paddingHorizontal: 7, paddingVertical: 3 },
  shortcutText: { ...typography.mono, color: colors.textDim, fontSize: 9, lineHeight: 12, fontWeight: '700' },
  iconButton: { width: 44, height: 44, borderRadius: 14, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface, alignItems: 'center', justifyContent: 'center' },
  iconButtonText: { color: colors.textMuted, fontSize: 18, fontFamily: fontFamilies.body },
  pressed: { opacity: 0.72 },
  progressTrack: { height: 8, borderRadius: 8, backgroundColor: colors.backgroundSoft, overflow: 'hidden' },
  progressFill: { height: '100%', borderRadius: 8 },
  emptyState: { alignItems: 'center', paddingVertical: spacing.xl },
  emptyIcon: { width: 48, height: 48, borderRadius: 16, borderWidth: 1, borderColor: colors.borderStrong, backgroundColor: colors.glow, alignItems: 'center', justifyContent: 'center' },
  emptyIconText: { color: colors.moss, fontSize: 24, fontFamily: fontFamilies.heading },
  emptyTitle: { ...typography.cardTitle, color: colors.text, fontWeight: '700', marginTop: 14 },
  emptyDescription: { ...typography.label, color: colors.textMuted, textAlign: 'center', marginTop: 6, maxWidth: 300, lineHeight: 18, fontFamily: fontFamilies.body }
});
