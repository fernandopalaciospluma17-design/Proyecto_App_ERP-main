import React from 'react';
import { FlatList, ScrollView, StyleSheet, Text, View } from 'react-native';
import { colors, fontFamilies, radius, spacing, typography } from '../../theme';

export type DataGridColumn<T> = {
  key: keyof T;
  title: string;
  width?: number;
  render?: (value: T[keyof T], row: T) => React.ReactNode;
};

export function DataGrid<T extends { id: string }>({
  columns,
  rows,
  rowHeight = 44
}: {
  columns: DataGridColumn<T>[];
  rows: T[];
  rowHeight?: number;
}) {
  return (
    <ScrollView horizontal accessibilityLabel="Tabla de datos" showsHorizontalScrollIndicator>
      <View style={styles.table}>
        <View style={styles.header}>
          {columns.map((column) => (
            <Text
              accessibilityRole="header"
              key={String(column.key)}
              style={[styles.cell, styles.headerCell, { width: column.width ?? 160 }]}
            >
              {column.title}
            </Text>
          ))}
        </View>
        <FlatList
          data={rows}
          getItemLayout={(_, index) => ({ length: rowHeight, offset: rowHeight * index, index })}
          keyExtractor={(row) => row.id}
          renderItem={({ item }) => (
            <View style={[styles.row, { height: rowHeight }]}>
              {columns.map((column) => (
                <Text key={String(column.key)} style={[styles.cell, { width: column.width ?? 160 }]}>
                  {column.render ? column.render(item[column.key], item) : String(item[column.key] ?? '')}
                </Text>
              ))}
            </View>
          )}
        />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  table: {
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: colors.borderStrong,
    borderRadius: radius.md,
    backgroundColor: colors.card
  },
  header: { flexDirection: 'row', backgroundColor: colors.slate },
  row: { flexDirection: 'row', borderBottomWidth: 1, borderBottomColor: colors.border },
  cell: {
    ...typography.table,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    color: colors.text
  },
  headerCell: {
    color: colors.mist,
    fontFamily: fontFamilies.labelBold,
    fontSize: typography.table.fontSize,
    lineHeight: typography.table.lineHeight
  }
});
