export const colors = {
  signature: '#E08934',
  ink: '#18201F',
  slate: '#35413F',
  moss: '#547064',
  mist: '#F4F5F1',
  paper: '#FFFFFF',
  line: '#E2E7E1',
  success: '#2E7D5B',
  danger: '#B44736',
  warning: '#8F5D23',
  muted: '#547064',
  sidebarBackground: '#18201F',
  sidebarText: '#F4F5F1',
  sidebarMuted: '#C4CEC7',
  sidebarDim: '#A5B2A9',
  sidebarBorder: 'rgba(244, 245, 241, 0.16)',
  background: '#18201F',
  backgroundSoft: '#EDF0EA',
  surface: '#FFFFFF',
  card: '#FFFFFF',
  cardElevated: '#FFFFFF',
  primary: '#E08934',
  primaryBright: '#A35719',
  primaryDark: '#547064',
  text: '#18201F',
  textMuted: '#35413F',
  textDim: '#547064',
  border: 'rgba(53, 65, 63, 0.12)',
  borderStrong: 'rgba(53, 65, 63, 0.24)',
  glow: 'rgba(224, 137, 52, 0.12)',
  overlay: 'rgba(24, 32, 31, 0.92)'
} as const;

export const spacing = {
  xs: 6,
  sm: 10,
  md: 16,
  lg: 24,
  xl: 32,
  xxl: 48
} as const;

export const radius = {
  sm: 8,
  md: 12,
  lg: 18,
  pill: 999
} as const;

export const fontFamilies = {
  display: 'Manrope_700Bold',
  heading: 'Manrope_700Bold',
  body: 'Inter_400Regular',
  bodyMedium: 'Inter_600SemiBold',
  label: 'Inter_600SemiBold',
  labelBold: 'Inter_700Bold',
  mono: 'IBMPlexMono_400Regular'
} as const;

export const typography = {
  display: { fontSize: 32, lineHeight: 38, fontFamily: fontFamilies.display },
  title: { fontSize: 22, lineHeight: 28, fontFamily: fontFamilies.heading },
  section: { fontSize: 17, lineHeight: 24, fontFamily: fontFamilies.heading },
  cardTitle: { fontSize: 16, lineHeight: 22, fontFamily: fontFamilies.heading },
  metric: { fontSize: 25, lineHeight: 32, fontFamily: fontFamilies.heading },
  body: { fontSize: 14, lineHeight: 20, fontFamily: fontFamilies.body },
  label: { fontSize: 12, lineHeight: 16, fontFamily: fontFamilies.label },
  caption: { fontSize: 10, lineHeight: 14, fontFamily: fontFamilies.label },
  table: { fontSize: 12, lineHeight: 18, fontFamily: fontFamilies.body },
  mono: { fontSize: 12, lineHeight: 18, fontFamily: fontFamilies.mono }
} as const;

export const shadows = {
  card: {
    shadowColor: colors.ink,
    shadowOpacity: 0.12,
    shadowRadius: 16,
    shadowOffset: { width: 0, height: 8 }
  },
  floating: {
    shadowColor: colors.ink,
    shadowOpacity: 0.2,
    shadowRadius: 20,
    shadowOffset: { width: 0, height: 10 }
  }
} as const;
