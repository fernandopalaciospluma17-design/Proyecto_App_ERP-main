import dns from 'node:dns';
dns.setServers(['1.1.1.1', '8.8.8.8']);

import { connectDatabase, disconnectDatabase } from '../config/database.js';
import { ContactModel } from '../modules/contacts/contact.model.js';
import { ProductModel } from '../modules/inventory/product.model.js';
import { StockMovementModel } from '../modules/inventory/stock-movement.model.js';
import { StockBalanceModel } from '../modules/inventory/stock-balance.model.js';
import { InvoiceModel } from '../modules/sales/invoice.model.js';
import { PurchaseModel } from '../modules/purchases/purchase.model.js';
import { ProjectModel } from '../modules/projects/project.model.js';
import { CashMovementModel } from '../modules/finance/cash-movement.model.js';

const P = /^TEST-/;

async function main() {
  await connectDatabase();

  // Los balances se borran antes que los productos, porque dependen de sus _id
  const ids = (await ProductModel.find({ sku: P }).select('_id').lean())
    .map((p) => String(p._id));
  await StockBalanceModel.deleteMany({ productId: { $in: ids } });

  const results = await Promise.all([
    ProductModel.deleteMany({ sku: P }),
    ContactModel.deleteMany({ name: P }),
    InvoiceModel.deleteMany({ number: P }),
    PurchaseModel.deleteMany({ number: P }),
    ProjectModel.deleteMany({ name: P }),
    StockMovementModel.deleteMany({ referenceId: P }),
    CashMovementModel.deleteMany({ sourceId: P })
  ]);

  console.log('Eliminados (productos, contactos, facturas, compras, proyectos, mov. inventario, mov. financieros):');
  console.log(results.map((r) => r.deletedCount));
}

main()
  .catch((e) => { console.error(e); process.exitCode = 1; })
  .finally(disconnectDatabase);