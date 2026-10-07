import React, { useState } from 'react';
import {
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  useWindowDimensions,
  View
} from 'react-native';
import { Brand, Button, Input } from '../components/ui';
import { AuthApiError, login, registerAccount, resendVerification, type AuthSession } from '../services/auth.client';
import { colors, fontFamilies, radius, shadows, spacing, typography } from '../theme';

type Mode = 'login' | 'register' | 'pending';

export function AuthScreen({ onAuthenticated }: { onAuthenticated: (session: AuthSession) => Promise<void> }) {
  const { width } = useWindowDimensions();
  const [mode, setMode] = useState<Mode>('login');
  const [name, setName] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const compact = width < 720;

  async function submit() {
    const normalizedEmail = email.trim().toLowerCase();
    setError(null);
    setNotice(null);

    if (!normalizedEmail || !password) {
      setError('Escribe tu correo y contraseña.');
      return;
    }

    if (mode === 'register' && (!name.trim() || !companyName.trim() || password.length < 10)) {
      setError('Completa todos los campos y usa una contraseña de al menos 10 caracteres.');
      return;
    }

    setLoading(true);
    try {
      if (mode === 'register') {
        const result = await registerAccount({
          name: name.trim(),
          companyName: companyName.trim(),
          email: normalizedEmail,
          password
        });
        setNotice(result.message);
        setMode('pending');
        return;
      }

      const session = await login(normalizedEmail, password);
      await onAuthenticated(session);
    } catch (cause) {
      if (cause instanceof AuthApiError && cause.code === 'EMAIL_NOT_VERIFIED') {
        setMode('pending');
        setNotice('Tu cuenta existe, pero todavía debes confirmar el correo.');
      } else {
        setError(cause instanceof Error ? cause.message : 'No fue posible completar la solicitud.');
      }
    } finally {
      setLoading(false);
    }
  }

  async function resend() {
    if (!email.trim()) {
      setError('Escribe el correo de la cuenta.');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const result = await resendVerification(email.trim().toLowerCase());
      setNotice(result.message);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'No fue posible reenviar el correo.');
    } finally {
      setLoading(false);
    }
  }

  function changeMode(next: Mode) {
    setMode(next);
    setError(null);
    setNotice(null);
  }

  return (
    <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : undefined} style={styles.root}>
      <View style={styles.ambientTop} />
      <View style={styles.ambientBottom} />
      <ScrollView style={styles.scrollView} contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
        <View style={[styles.layout, compact && styles.layoutCompact]}>
          {!compact ? (
            <View style={styles.intro}>
              <Brand />
              <Text style={styles.kicker}>ERP empresarial conectado</Text>
              <Text style={styles.heroTitle}>Operaciones claras. Decisiones que avanzan.</Text>
              <Text style={styles.heroCopy}>Nodara reúne procesos, equipos y datos en un sistema empresarial preciso, humano y preparado para crecer.</Text>
              <View style={styles.trustRow}><Text style={styles.trustGlyph}>✓</Text><Text style={styles.trustText}>Tu Informacion Segura Siempre</Text></View>
              <View style={styles.trustRow}><Text style={styles.trustGlyph}>✓</Text><Text style={styles.trustText}>Todo en un solo lugar</Text></View>
            </View>
          ) : null}

          <View style={styles.card}>
            {compact ? <View style={styles.mobileBrand}><Brand tone="light" /></View> : null}
            {mode === 'pending' ? (
              <>
                <View style={styles.mailIcon}><Text style={styles.mailGlyph}>✉</Text></View>
                <Text style={styles.title}>Confirma tu correo</Text>
                <Text style={styles.description}>Enviamos un enlace a <Text style={styles.emailStrong}>{email.trim()}</Text>. Después de confirmarlo, vuelve aquí para iniciar sesión.</Text>
                {notice ? <Message tone="success" text={notice} /> : null}
                {error ? <Message tone="error" text={error} /> : null}
                <PrimaryButton label="Ya confirmé mi correo" loading={loading} onPress={() => changeMode('login')} />
                <TextAction disabled={loading} label="Reenviar correo de confirmación" onPress={resend} />
                <TextAction disabled={loading} label="Usar otra cuenta" onPress={() => changeMode('register')} subtle />
              </>
            ) : (
              <>
                <Text style={styles.eyebrow}>{mode === 'login' ? 'BIENVENIDO DE NUEVO' : 'NUEVA CUENTA'}</Text>
                <Text style={styles.title}>{mode === 'login' ? 'Inicia sesión' : 'Crea tu espacio de trabajo'}</Text>
                <Text style={styles.description}>{mode === 'login' ? 'Accede a tu información empresarial.' : 'Te enviaremos un correo para confirmar tu identidad.'}</Text>

                {mode === 'register' ? (
                  <>
                    <Field label="Nombre completo" value={name} onChangeText={setName} autoComplete="name" />
                    <Field label="Nombre de la empresa" value={companyName} onChangeText={setCompanyName} />
                  </>
                ) : null}
                <Field label="Correo electrónico" value={email} onChangeText={setEmail} autoComplete="email" keyboardType="email-address" />
                <Field label="Contraseña" value={password} onChangeText={setPassword} autoComplete={mode === 'login' ? 'current-password' : 'new-password'} secureTextEntry />
                {mode === 'register' ? <Text style={styles.passwordHint}>Mínimo 10 caracteres.</Text> : null}

                {notice ? <Message tone="success" text={notice} /> : null}
                {error ? <Message tone="error" text={error} /> : null}
                <PrimaryButton label={mode === 'login' ? 'Entrar a Nodara ERP' : 'Crear cuenta'} loading={loading} onPress={submit} />

                <View style={styles.switchRow}>
                  <Text style={styles.switchCopy}>{mode === 'login' ? '¿Aún no tienes cuenta?' : '¿Ya tienes una cuenta?'}</Text>
                  <TextAction disabled={loading} label={mode === 'login' ? 'Regístrate' : 'Inicia sesión'} onPress={() => changeMode(mode === 'login' ? 'register' : 'login')} />
                </View>
              </>
            )}
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

function Field(props: React.ComponentProps<typeof TextInput> & { label: string }) {
  const { label, ...inputProps } = props;
  return (
    <View style={styles.field}>
      <Input {...inputProps} autoCapitalize="none" label={label} />
    </View>
  );
}

function PrimaryButton({ label, loading, onPress }: { label: string; loading: boolean; onPress: () => void }) {
  return <View style={styles.primaryAction}><Button disabled={loading} label={label} loading={loading} onPress={onPress} /></View>;
}

function TextAction({ label, onPress, disabled = false, subtle = false }: {
  label: string;
  onPress: () => void;
  disabled?: boolean;
  subtle?: boolean;
}) {
  const [highlighted, setHighlighted] = useState(false);
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityState={{ disabled }}
      disabled={disabled}
      onBlur={() => setHighlighted(false)}
      onFocus={() => setHighlighted(true)}
      onHoverIn={() => setHighlighted(true)}
      onHoverOut={() => setHighlighted(false)}
      onPress={onPress}
      style={({ pressed }) => [styles.textButton, subtle && styles.subtleButton, highlighted && styles.textActionFocused, pressed && styles.actionPressed, disabled && styles.actionDisabled]}
    >
      <Text style={[styles.textButtonLabel, subtle && styles.subtleLabel]}>{label}</Text>
    </Pressable>
  );
}

function Message({ tone, text }: { tone: 'success' | 'error'; text: string }) {
  return (
    <View style={[styles.message, tone === 'success' ? styles.messageSuccess : styles.messageError]}>
      <Text style={[styles.messageGlyph, tone === 'success' ? styles.successGlyph : styles.errorGlyph]}>{tone === 'success' ? '✓' : '!'}</Text>
      <Text style={styles.messageText}>{text}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.background },
  scrollView: { flex: 1 },
  scroll: { flexGrow: 1, justifyContent: 'center', padding: spacing.lg },
  ambientTop: { position: 'absolute', width: 520, height: 520, borderRadius: 260, top: -330, right: 0, backgroundColor: colors.glow },
  ambientBottom: { position: 'absolute', width: 430, height: 430, borderRadius: 215, bottom: -300, left: -170, backgroundColor: 'rgba(84, 112, 100, 0.1)' },
  layout: { width: '100%', maxWidth: 1080, alignSelf: 'center', flexDirection: 'row', alignItems: 'stretch', gap: spacing.xl },
  layoutCompact: { maxWidth: 480, flexDirection: 'column', gap: 0 },
  intro: { flex: 1, maxWidth: 590, minHeight: 560, justifyContent: 'center', padding: spacing.xl, borderRadius: radius.lg, backgroundColor: colors.ink, borderWidth: 1, borderColor: colors.sidebarBorder },
  kicker: { color: colors.signature, fontSize: 11, fontWeight: '800', letterSpacing: 1.8, marginTop: 48, marginBottom: 14, fontFamily: fontFamilies.labelBold },
  heroTitle: { ...typography.display, color: colors.mist, fontSize: 42, lineHeight: 49, letterSpacing: -1.4 },
  heroCopy: { color: colors.sidebarMuted, fontSize: 16, lineHeight: 25, marginTop: 18, marginBottom: 28, fontFamily: fontFamilies.body },
  trustRow: { flexDirection: 'row', alignItems: 'center', gap: 10, marginTop: 12 },
  trustGlyph: { color: colors.signature, fontSize: 16, fontWeight: '800' },
  trustText: { color: colors.sidebarMuted, fontSize: 13, fontFamily: fontFamilies.body },
  card: { width: '100%', maxWidth: 430, minHeight: 560, justifyContent: 'center', padding: 28, borderRadius: radius.lg, borderWidth: 1, borderColor: colors.line, backgroundColor: colors.paper, ...shadows.floating },
  mobileBrand: { marginBottom: 28 },
  eyebrow: { color: colors.primaryBright, fontSize: 10, fontWeight: '800', letterSpacing: 1.7, marginBottom: 10, fontFamily: fontFamilies.labelBold },
  title: { ...typography.title, color: colors.ink, letterSpacing: -0.6 },
  description: { ...typography.body, color: colors.slate, marginTop: 9, marginBottom: 22 },
  emailStrong: { color: colors.ink, fontWeight: '700', fontFamily: fontFamilies.labelBold },
  field: { marginBottom: spacing.md },
  passwordHint: { color: colors.muted, fontSize: 11, marginTop: -8, marginBottom: spacing.md, fontFamily: fontFamilies.body },
  primaryAction: { marginTop: spacing.xs },
  textButton: { minHeight: 44, alignItems: 'center', justifyContent: 'center', marginTop: 9, paddingHorizontal: 8, borderRadius: radius.sm },
  textButtonLabel: { color: colors.primaryBright, fontSize: 12, fontWeight: '700', fontFamily: fontFamilies.label },
  subtleButton: { minHeight: 44 },
  subtleLabel: { color: colors.slate, fontSize: 11, fontFamily: fontFamilies.body },
  textActionFocused: { borderWidth: 2, borderColor: colors.primaryBright },
  actionPressed: { opacity: 0.7 },
  actionDisabled: { opacity: 0.55 },
  switchRow: { flexDirection: 'row', justifyContent: 'center', alignItems: 'center', flexWrap: 'wrap', gap: 6, marginTop: 22 },
  switchCopy: { color: colors.slate, fontSize: 12, fontFamily: fontFamilies.body },
  switchAction: { color: colors.primaryBright, fontSize: 12, fontWeight: '800', fontFamily: fontFamilies.labelBold },
  message: { flexDirection: 'row', alignItems: 'flex-start', gap: spacing.sm, borderWidth: 1, borderRadius: radius.md, padding: 11, marginBottom: 12 },
  messageSuccess: { borderColor: `${colors.success}70`, backgroundColor: `${colors.success}14` },
  messageError: { borderColor: `${colors.danger}70`, backgroundColor: `${colors.danger}12` },
  messageGlyph: { fontSize: 14, fontFamily: fontFamilies.labelBold },
  successGlyph: { color: colors.success },
  errorGlyph: { color: colors.danger },
  messageText: { flex: 1, color: colors.ink, fontSize: 12, lineHeight: 18, fontFamily: fontFamilies.body },
  mailIcon: { width: 60, height: 60, borderRadius: 20, alignItems: 'center', justifyContent: 'center', backgroundColor: colors.glow, borderWidth: 1, borderColor: colors.borderStrong, marginBottom: 20 },
  mailGlyph: { color: colors.primaryBright, fontSize: 27 }
});
