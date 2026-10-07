import React, { useState, type PropsWithChildren } from 'react';
import {
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
  type TextInputProps
} from 'react-native';
import { Button, Input, ModalSurface } from './ui';
import { colors, fontFamilies, radius, shadows, spacing, typography } from '../theme';

export function FormModal({
  visible,
  title,
  description,
  submitLabel = 'Guardar',
  busy = false,
  error,
  onClose,
  onSubmit,
  children
}: PropsWithChildren<{
  visible: boolean;
  title: string;
  description?: string;
  submitLabel?: string;
  busy?: boolean;
  error?: string;
  onClose: () => void;
  onSubmit: () => void;
}>) {
  const [closeHighlighted, setCloseHighlighted] = useState(false);
  return (
    <Modal animationType="none" transparent visible={visible} onRequestClose={onClose}>
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : undefined} style={styles.overlay}>
        <Pressable accessibilityLabel="Cerrar formulario" accessibilityRole="button" style={StyleSheet.absoluteFill} onPress={onClose} />
        <ModalSurface visible={visible} style={styles.modalCard}>
          <View style={styles.header}>
            <View style={styles.headerCopy}>
              <Text style={styles.title}>{title}</Text>
              {description ? <Text style={styles.description}>{description}</Text> : null}
            </View>
            <Pressable accessibilityLabel="Cerrar" accessibilityRole="button" onBlur={() => setCloseHighlighted(false)} onFocus={() => setCloseHighlighted(true)} onHoverIn={() => setCloseHighlighted(true)} onHoverOut={() => setCloseHighlighted(false)} onPress={onClose} style={[styles.close, closeHighlighted && styles.closeFocused]}><Text style={styles.closeText}>×</Text></Pressable>
          </View>
          <ScrollView contentContainerStyle={styles.body} keyboardShouldPersistTaps="handled">
            {children}
            {error ? <Text accessibilityRole="alert" style={styles.error}><Text style={styles.errorGlyph}>! </Text>{error}</Text> : null}
          </ScrollView>
          <View style={styles.actions}>
            <Button disabled={busy} label="Cancelar" onPress={onClose} variant="secondary" />
            <Button disabled={busy} label={submitLabel} loading={busy} onPress={onSubmit} />
          </View>
        </ModalSurface>
      </KeyboardAvoidingView>
    </Modal>
  );
}

export function FormField({ label, style, ...props }: TextInputProps & { label: string; error?: string }) {
  const { error, multiline } = props;
  return (
    <Input
      {...props}
      error={error}
      label={label}
      style={[multiline && styles.multiline, style]}
    />
  );
}

export function ChoiceRow<T extends string>({ label, value, options, onChange }: {
  label: string;
  value: T;
  options: readonly T[];
  onChange: (value: T) => void;
}) {
  const [focusedOption, setFocusedOption] = useState<T | null>(null);
  return (
    <View style={styles.fieldWrap}>
      <Text style={styles.label}>{label}</Text>
      <View style={styles.choices}>
        {options.map((option) => (
          <Pressable
            key={option}
            accessibilityRole="button"
            accessibilityState={{ selected: option === value }}
            onBlur={() => setFocusedOption(null)}
            onFocus={() => setFocusedOption(option)}
            onHoverIn={() => setFocusedOption(option)}
            onHoverOut={() => setFocusedOption((current) => current === option ? null : current)}
            onPress={() => onChange(option)}
            style={[styles.choice, option === value && styles.choiceActive, focusedOption === option && styles.choiceFocused, focusedOption === option && option === value && styles.choiceFocusedActive]}
          >
            <Text style={[styles.choiceText, option === value && styles.choiceTextActive]}>{option}</Text>
          </Pressable>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: colors.overlay, alignItems: 'center', justifyContent: 'center', padding: spacing.md },
  modalCard: { width: '100%', maxWidth: 620, maxHeight: '92%', backgroundColor: colors.card, borderRadius: radius.lg, borderWidth: 1, borderColor: colors.borderStrong, overflow: 'hidden', ...shadows.floating },
  header: { flexDirection: 'row', alignItems: 'flex-start', padding: spacing.lg, borderBottomWidth: 1, borderBottomColor: colors.border },
  headerCopy: { flex: 1 },
  title: { ...typography.title, color: colors.text, fontWeight: '800' },
  description: { ...typography.label, color: colors.textMuted, marginTop: 5, fontFamily: fontFamilies.body },
  close: { width: 44, height: 44, borderRadius: 12, alignItems: 'center', justifyContent: 'center', backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  closeFocused: { borderWidth: 2, borderColor: colors.primaryBright },
  closeText: { color: colors.textMuted, fontSize: 24, lineHeight: 26 },
  body: { padding: spacing.lg, gap: spacing.md },
  fieldWrap: { gap: spacing.xs },
  label: { ...typography.label, color: colors.textMuted },
  multiline: { minHeight: 86, paddingTop: 12, textAlignVertical: 'top' },
  choices: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  choice: { minHeight: 44, paddingVertical: 9, paddingHorizontal: 14, borderRadius: radius.pill, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  choiceActive: { borderColor: colors.primaryBright, backgroundColor: colors.primaryDark },
  choiceFocused: { borderWidth: 2, borderColor: colors.primaryBright },
  choiceFocusedActive: { borderColor: colors.paper },
  choiceText: { color: colors.textMuted, fontSize: 11, fontWeight: '700' },
  choiceTextActive: { color: colors.text },
  error: { color: colors.text, borderWidth: 1, borderColor: `${colors.danger}70`, backgroundColor: `${colors.danger}18`, borderRadius: radius.md, padding: 12, fontSize: 11, lineHeight: 17, fontFamily: fontFamilies.body },
  errorGlyph: { color: colors.danger, fontFamily: fontFamilies.labelBold },
  actions: { flexDirection: 'row', justifyContent: 'flex-end', gap: 10, padding: spacing.lg, borderTopWidth: 1, borderTopColor: colors.border },
});
