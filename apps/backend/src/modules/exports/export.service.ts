import type { Response } from 'express';
import ExcelJS from 'exceljs';
import PDFDocument from 'pdfkit';
import { existsSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { TenantModel } from '../auth/tenant.model.js';
import { ContactModel } from '../contacts/contact.model.js';
import { ProductModel } from '../inventory/product.model.js';
import { StockBalanceModel } from '../inventory/stock-balance.model.js';
import { InvoiceModel } from '../sales/invoice.model.js';

const currency = new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' });
const nodaraLogoPath = (() => {
  const path = [
    new URL('../../../../../logo-nodara-oscuro.png', import.meta.url),
    new URL('../../../../../../logo-nodara-oscuro.png', import.meta.url)
  ].map((url) => fileURLToPath(url)).find(existsSync);
  if (!path) throw new Error('No se encontró el logo de Nodara para generar el reporte PDF.');
  return path;
})();

async function getExportData(tenantId: string) {
  const [tenant, products, balances, contacts, invoices] = await Promise.all([
    TenantModel.findById(tenantId).lean(),
    ProductModel.find({ tenantId }).sort({ name: 1 }).lean(),
    StockBalanceModel.find({ tenantId }).lean(),
    ContactModel.find({ tenantId }).sort({ name: 1 }).lean(),
    InvoiceModel.find({ tenantId }).sort({ issuedAt: -1 }).lean()
  ]);
  const stock = new Map(balances.map((balance) => [String(balance.productId), balance.currentStock]));
  return {
    company: tenant?.name ?? 'Empresa',
    products: products.map((product) => ({ ...product, currentStock: stock.get(String(product._id)) ?? 0 })),
    contacts,
    invoices
  };
}

function styleSheet(sheet: ExcelJS.Worksheet) {
  sheet.views = [{ state: 'frozen', ySplit: 1 }];
  sheet.autoFilter = { from: { row: 1, column: 1 }, to: { row: 1, column: sheet.columnCount } };
  sheet.getRow(1).font = { bold: true, color: { argb: 'FFFFFFFF' } };
  sheet.getRow(1).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1557C8' } };
  sheet.getRow(1).alignment = { vertical: 'middle' };
  sheet.columns.forEach((column) => { column.width = Math.min(34, Math.max(12, Number(column.header?.toString().length ?? 10) + 4)); });
}

export async function streamTenantWorkbook(tenantId: string, res: Response) {
  const data = await getExportData(tenantId);
  const workbook = new ExcelJS.Workbook();
  workbook.creator = 'Nodara ERP';
  workbook.created = new Date();
  const summary = workbook.addWorksheet('Resumen');
  summary.addRows([
    ['Nodara ERP - Exportación de datos', data.company],
    ['Fecha de exportación', new Date()],
    ['Productos', data.products.length],
    ['Contactos', data.contacts.length],
    ['Facturas', data.invoices.length],
    ['Ventas no canceladas', data.invoices.filter((item) => item.status !== 'cancelled').reduce((sum, item) => sum + item.total, 0)]
  ]);
  summary.getColumn(1).font = { bold: true };
  summary.getColumn(1).width = 30; summary.getColumn(2).width = 34;
  summary.getCell('B2').numFmt = 'dd/mm/yyyy hh:mm'; summary.getCell('B6').numFmt = '$#,##0.00';

  const productSheet = workbook.addWorksheet('Productos');
  productSheet.columns = [
    { header: 'SKU', key: 'sku' }, { header: 'Código de barras', key: 'barcode' }, { header: 'Producto', key: 'name' },
    { header: 'Costo', key: 'costo' }, { header: 'Precio', key: 'precio' }, { header: 'Stock mínimo', key: 'stockMinimo' }, { header: 'Existencia', key: 'currentStock' }
  ];
  productSheet.addRows(data.products);
  productSheet.getColumn('costo').numFmt = '$#,##0.00'; productSheet.getColumn('precio').numFmt = '$#,##0.00'; styleSheet(productSheet);

  const contactSheet = workbook.addWorksheet('Contactos');
  contactSheet.columns = [
    { header: 'Nombre', key: 'name' }, { header: 'Tipo', key: 'type' }, { header: 'RFC / ID fiscal', key: 'taxId' },
    { header: 'Correo', key: 'email' }, { header: 'Teléfono', key: 'phone' }
  ];
  contactSheet.addRows(data.contacts); styleSheet(contactSheet);

  const invoiceSheet = workbook.addWorksheet('Facturas');
  invoiceSheet.columns = [
    { header: 'Folio', key: 'number' }, { header: 'Fecha', key: 'issuedAt' }, { header: 'Cliente', key: 'customer' },
    { header: 'Estado', key: 'status' }, { header: 'Subtotal', key: 'subtotal' }, { header: 'Impuestos', key: 'impuestos' }, { header: 'Total', key: 'total' }
  ];
  invoiceSheet.addRows(data.invoices.map((invoice) => ({ ...invoice, customer: invoice.customer.name, status: invoice.status ?? 'pending' })));
  invoiceSheet.getColumn('issuedAt').numFmt = 'dd/mm/yyyy';
  ['subtotal', 'impuestos', 'total'].forEach((column) => { invoiceSheet.getColumn(column).numFmt = '$#,##0.00'; });
  styleSheet(invoiceSheet);

  await workbook.xlsx.write(res);
  res.end();
}

export async function streamTenantPdf(tenantId: string, res: Response) {
  const data = await getExportData(tenantId);
  const doc = new PDFDocument({
    size: 'A4',
    margin: 44,
    bufferPages: true,
    info: { Title: `Reporte Nodara ERP - ${data.company}`, Author: 'Nodara ERP' }
  });
  doc.pipe(res);
  const contentTop = 112;
  const contentBottom = doc.page.height - doc.page.margins.bottom - 34;
  const ensureSpace = (height = 50) => {
    if (doc.y + height > contentBottom) {
      doc.addPage();
      doc.y = contentTop;
    }
  };
  const title = (value: string) => { ensureSpace(45); doc.moveDown().fillColor('#E08934').fontSize(15).text(value); doc.moveDown(0.4); };
  const line = (columns: string[], widths: number[], header = false) => {
    ensureSpace(24);
    const y = doc.y;
    let x = 44;
    if (header) doc.rect(44, y - 4, 507, 20).fill('#E08934');
    doc.fillColor(header ? '#FFFFFF' : '#18201F').fontSize(8);
    columns.forEach((value, index) => { doc.text(value, x + 4, y, { width: (widths[index] ?? 80) - 8, ellipsis: true }); x += widths[index] ?? 80; });
    doc.y = y + 20;
    if (!header) doc.moveTo(44, doc.y - 3).lineTo(551, doc.y - 3).strokeColor('#E2E7E1').stroke();
  };

  doc.y = contentTop;
  doc.fillColor('#18201F').fontSize(18).text(data.company);
  doc.fillColor('#547064').fontSize(9).text(`Exportación generada el ${new Date().toLocaleString('es-MX')}`);
  title('Resumen');
  doc.fillColor('#18201F').fontSize(10).text(`Productos: ${data.products.length}   Contactos: ${data.contacts.length}   Facturas: ${data.invoices.length}`);
  doc.text(`Ventas no canceladas: ${currency.format(data.invoices.filter((item) => item.status !== 'cancelled').reduce((sum, item) => sum + item.total, 0))}`);

  title('Productos e inventario');
  line(['SKU', 'Producto', 'Existencia', 'Precio'], [100, 227, 80, 100], true);
  data.products.forEach((product) => line([product.sku, product.name, String(product.currentStock), currency.format(product.precio)], [100, 227, 80, 100]));

  title('Contactos');
  line(['Nombre', 'Tipo', 'Correo', 'Teléfono'], [170, 75, 172, 90], true);
  data.contacts.forEach((contact) => line([contact.name, contact.type ?? 'Cliente', contact.email ?? '', contact.phone ?? ''], [170, 75, 172, 90]));

  title('Facturas');
  line(['Folio', 'Fecha', 'Cliente', 'Estado', 'Total'], [95, 72, 165, 75, 100], true);
  data.invoices.forEach((invoice) => line([
    invoice.number,
    new Date(invoice.issuedAt).toLocaleDateString('es-MX'),
    invoice.customer.name,
    invoice.status === 'paid' ? 'Pagada' : invoice.status === 'cancelled' ? 'Cancelada' : 'Pendiente',
    currency.format(invoice.total)
  ], [95, 72, 165, 75, 100]));

  const pages = doc.bufferedPageRange();
  for (let pageIndex = pages.start; pageIndex < pages.start + pages.count; pageIndex += 1) {
    doc.switchToPage(pageIndex);
    const contentY = doc.y;
    doc.image(nodaraLogoPath, 44, 31, { fit: [150, 45] });
    doc.fillColor('#E08934').font('Helvetica-Bold').fontSize(12).text('NODARA ERP', 350, 47, {
      width: 201,
      align: 'right',
      lineBreak: false
    });
    doc.moveTo(44, 91).lineTo(551, 91).lineWidth(2).strokeColor('#E08934').stroke();
    doc.fillColor('#547064').font('Helvetica').fontSize(8).text(
      `Nodara ERP · Página ${pageIndex - pages.start + 1}`,
      44,
      doc.page.height - 34,
      { width: 507, align: 'right', lineBreak: false }
    );
    doc.y = contentY;
  }
  doc.end();
}
