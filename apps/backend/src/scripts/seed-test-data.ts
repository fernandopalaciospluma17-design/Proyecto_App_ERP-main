import mongoose from 'mongoose';
import { connectDatabase, disconnectDatabase } from '../config/database.js';

import { UserModel } from '../modules/auth/user.model.js';
import { ContactModel } from '../modules/contacts/contact.model.js';
import { ProductModel } from '../modules/inventory/product.model.js';
import { StockMovementModel } from '../modules/inventory/stock-movement.model.js';
import { StockBalanceModel } from '../modules/inventory/stock-balance.model.js';
import { InvoiceModel } from '../modules/sales/invoice.model.js';
import { PurchaseModel } from '../modules/purchases/purchase.model.js';
import { ProjectModel } from '../modules/projects/project.model.js';
import { CashMovementModel } from '../modules/finance/cash-movement.model.js';
import dns from 'node:dns';
dns.setServers(['1.1.1.1', '8.8.8.8']);

/*
 * ============================================================
 * NODARA ERP - DATOS DE PRUEBA
 * ============================================================
 *
 * Este script genera datos ficticios para pruebas.
 *
 * Todos los registros utilizan el tenant del usuario:
 *
 *     admin@nodara.local
 *
 * Los datos de prueba utilizan el prefijo:
 *
 *     TEST-
 *
 * Este script NO crea usuarios nuevos.
 * Tampoco modifica contrasenas.
 * ============================================================
 */

const COUNTS = {
  contacts: 1000,
  products: 1000,
  invoices: 1500,
  purchases: 500,
  stockMovements: 2000,
  projects: 100,
  cashMovements: 1000
};

const TEST_PREFIX = 'TEST-';

function randomInt(min: number, max: number): number {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomFloat(
  min: number,
  max: number,
  decimals = 2
): number {
  const value = Math.random() * (max - min) + min;
  return Number(value.toFixed(decimals));
}

function randomItem<T>(items: T[]): T {
  if (items.length === 0) {
    throw new Error('No hay elementos disponibles para seleccionar.');
  }

  return items[Math.floor(Math.random() * items.length)]!;
}

function randomDate(daysBack = 365): Date {
  const now = Date.now();
  const offset = randomInt(
    0,
    daysBack * 24 * 60 * 60 * 1000
  );

  return new Date(now - offset);
}

function chunk<T>(items: T[], size = 500): T[][] {
  const result: T[][] = [];

  for (let i = 0; i < items.length; i += size) {
    result.push(items.slice(i, i + size));
  }

  return result;
}

async function insertInChunks(
  model: mongoose.Model<any>,
  documents: any[],
  size = 500
): Promise<void> {
  for (const batch of chunk(documents, size)) {
    await model.insertMany(batch, {
      ordered: false
    });
  }
}

async function main(): Promise<void> {
  console.log('');
  console.log('==============================================');
  console.log('       NODARA ERP - TEST DATA SEED');
  console.log('==============================================');
  console.log('');

  await connectDatabase();

  console.log('MongoDB conectado.');

  /*
   * ============================================================
   * 1. OBTENER TENANT DEL ADMINISTRADOR
   * ============================================================
   */

  const admin = await UserModel.findOne({
    email: 'admin@nodara.local'
  })
    .select('+passwordHash')
    .lean();

  if (!admin) {
    throw new Error(
      'No se encontro admin@nodara.local. Ejecuta primero el seed del administrador.'
    );
  }

  const tenantId = admin.tenantId;
  const createdBy = String(admin._id);

  console.log(
    'Tenant utilizado: ' + String(tenantId)
  );

  console.log(
    'Usuario administrador: ' + String(admin.email)
  );

  console.log('');

  /*
   * ============================================================
   * 2. CONTACTOS
   * ============================================================
   */

  console.log(
    'Generando ' +
      String(COUNTS.contacts) +
      ' contactos...'
  );

  const contacts: Array<{
  tenantId: string;
  name: string;
  type: 'Cliente' | 'Proveedor';
  taxId: string;
  email: string;
  phone: string;
}> = [];

for (let i = 1; i <= COUNTS.contacts; i++) {
  const isSupplier = i % 5 === 0;

  contacts.push({
    tenantId,
    name: TEST_PREFIX + 'Contacto ' + i,
    type: isSupplier ? 'Proveedor' : 'Cliente',
    taxId: 'TST' + String(i).padStart(9, '0'),
    email: 'test.contact.' + i + '@nodara.local',
    phone: '246' + String(1000000 + i).slice(-7)
  });
}

  await insertInChunks(ContactModel, contacts);

  /*
   * ============================================================
   * 3. PRODUCTOS
   * ============================================================
   */

  console.log(
    'Generando ' +
      String(COUNTS.products) +
      ' productos...'
  );

  const products = [];

  for (let i = 1; i <= COUNTS.products; i++) {
    const costo = randomFloat(50, 5000);

    const precio = Number(
      (
        costo *
        randomFloat(1.15, 1.8)
      ).toFixed(2)
    );

    products.push({
      tenantId,
      sku:
        TEST_PREFIX +
        'SKU-' +
        String(i).padStart(5, '0'),

      barcode:
        '750TEST' +
        String(i).padStart(8, '0'),

      name:
        TEST_PREFIX +
        'Producto ' +
        i,

      imageUrl: '',
      hasImage: false,
      costo,
      precio,
      stockMinimo: randomInt(5, 50)
    });
  }

  await insertInChunks(ProductModel, products);

  /*
   * Recuperar productos de prueba.
   */

  const productDocs = await ProductModel.find({
    tenantId,
    sku: {
      $regex: '^' + TEST_PREFIX + 'SKU-'
    }
  })
    .select('_id sku name costo precio')
    .lean();

  if (productDocs.length < COUNTS.products) {
    throw new Error(
      'Solo se encontraron ' +
        productDocs.length +
        ' productos de prueba.'
    );
  }

  /*
   * ============================================================
   * 4. BALANCES DE INVENTARIO
   * ============================================================
   */

  console.log(
    'Generando ' +
      String(productDocs.length) +
      ' balances de inventario...'
  );

  const stockBalances = productDocs.map((product) => ({
    tenantId,
    productId: String(product._id),
    currentStock: randomInt(0, 500)
  }));

  await insertInChunks(
    StockBalanceModel,
    stockBalances
  );

  /*
   * ============================================================
   * 5. PROYECTOS / OBRAS
   * ============================================================
   */

  console.log(
    'Generando ' +
      String(COUNTS.projects) +
      ' proyectos...'
  );

  const projectStatuses = [
    'planned',
    'active',
    'completed',
    'paused'
  ] as const;

  const projects = [];

  for (let i = 1; i <= COUNTS.projects; i++) {
    const status = randomItem([
      ...projectStatuses
    ]);

    let progress = 0;

    if (status === 'completed') {
      progress = 100;
    } else if (status === 'planned') {
      progress = randomInt(0, 10);
    } else {
      progress = randomInt(10, 95);
    }

    projects.push({
      tenantId,

      name:
        TEST_PREFIX +
        'Obra ' +
        i,

      client:
        TEST_PREFIX +
        'Cliente Proyecto ' +
        i,

      description:
        'Proyecto de prueba generado automaticamente para Nodara ERP. Registro ' +
        i +
        '.',

      status,
      budget: randomFloat(50000, 5000000),
      progress,

      dueAt: new Date(
        Date.now() +
          randomInt(15, 365) *
            24 *
            60 *
            60 *
            1000
      )
    });
  }

  await insertInChunks(
    ProjectModel,
    projects
  );

  /*
   * ============================================================
   * 6. VENTAS / FACTURAS
   * ============================================================
   */

  console.log(
    'Generando ' +
      String(COUNTS.invoices) +
      ' ventas...'
  );

  const clientContacts =
    await ContactModel.find({
      tenantId,
      type: 'Cliente',
      name: {
        $regex: '^' + TEST_PREFIX
      }
    })
      .select('_id name taxId')
      .lean();

  if (clientContacts.length === 0) {
    throw new Error(
      'No se encontraron clientes de prueba.'
    );
  }

  const invoices = [];

  for (
    let i = 1;
    i <= COUNTS.invoices;
    i++
  ) {
    const customer =
      randomItem(clientContacts);

    const itemCount = randomInt(1, 4);
    const items = [];

    for (
      let j = 0;
      j < itemCount;
      j++
    ) {
      const product =
        randomItem(productDocs);

      items.push({
        productId: String(product._id),
        sku: product.sku,
        description: product.name,
        quantity: randomFloat(1, 20),
        unitPrice: product.precio,
        taxRate: 16
      });
    }

    const subtotal = Number(
      items
        .reduce(
          (sum, item) =>
            sum +
            item.quantity *
              item.unitPrice,
          0
        )
        .toFixed(2)
    );

    const impuestos = Number(
      (subtotal * 0.16).toFixed(2)
    );

    const total = Number(
      (subtotal + impuestos).toFixed(2)
    );

    const status = randomItem([
      'pending',
      'paid',
      'cancelled'
    ] as const);

    invoices.push({
      tenantId,

      number:
        TEST_PREFIX +
        'FAC-' +
        String(i).padStart(6, '0'),

      customer: {
        id: String(customer._id),
        name: customer.name,
        taxId: customer.taxId
      },

      items,
      subtotal,
      impuestos,
      total,
      issuedAt: randomDate(365),
      status,

      paidAt:
        status === 'paid'
          ? randomDate(300)
          : null
    });
  }

  await insertInChunks(
    InvoiceModel,
    invoices,
    200
  );

  /*
   * ============================================================
   * 7. COMPRAS
   * ============================================================
   */

  console.log(
    'Generando ' +
      String(COUNTS.purchases) +
      ' compras...'
  );

  const supplierContacts =
    await ContactModel.find({
      tenantId,
      type: 'Proveedor',
      name: {
        $regex: '^' + TEST_PREFIX
      }
    })
      .select('_id name')
      .lean();

  if (supplierContacts.length === 0) {
    throw new Error(
      'No se encontraron proveedores de prueba.'
    );
  }

  const purchases = [];

  for (
    let i = 1;
    i <= COUNTS.purchases;
    i++
  ) {
    const supplier =
      randomItem(supplierContacts);

    const itemCount = randomInt(1, 4);
    const items = [];

    for (
      let j = 0;
      j < itemCount;
      j++
    ) {
      const product =
        randomItem(productDocs);

      items.push({
        productId: String(product._id),
        sku: product.sku,
        description: product.name,
        quantity: randomInt(1, 100),
        unitCost: product.costo
      });
    }

    const total = Number(
      items
        .reduce(
          (sum, item) =>
            sum +
            item.quantity *
              item.unitCost,
          0
        )
        .toFixed(2)
    );

    const status = randomItem([
      'draft',
      'ordered',
      'received',
      'cancelled'
    ] as const);

    purchases.push({
      tenantId,

      number:
        TEST_PREFIX +
        'COMP-' +
        String(i).padStart(6, '0'),

      supplier: {
        id: String(supplier._id),
        name: supplier.name
      },

      items,
      total,
      status,

      expectedAt: new Date(
        Date.now() +
          randomInt(1, 90) *
            24 *
            60 *
            60 *
            1000
      ),

      receivedAt:
        status === 'received'
          ? randomDate(180)
          : null
    });
  }

  await insertInChunks(
    PurchaseModel,
    purchases,
    200
  );

  /*
   * ============================================================
   * 8. MOVIMIENTOS DE INVENTARIO
   * ============================================================
   */

  console.log(
    'Generando ' +
      String(COUNTS.stockMovements) +
      ' movimientos de inventario...'
  );

  const stockMovements = [];

  const movementTypes = [
    'ENTRADA',
    'SALIDA',
    'AJUSTE',
    'TRANSFERENCIA'
  ] as const;

  for (
    let i = 1;
    i <= COUNTS.stockMovements;
    i++
  ) {
    const product =
      randomItem(productDocs);

    const type =
      randomItem([
        ...movementTypes
      ]);

    stockMovements.push({
      tenantId,
      productId: String(product._id),
      type,
      quantity: randomInt(1, 100),

      referenceId:
        TEST_PREFIX +
        'MOV-' +
        String(i).padStart(7, '0'),

      occurredAt: randomDate(365)
    });
  }

  await insertInChunks(
    StockMovementModel,
    stockMovements,
    500
  );

  /*
   * ============================================================
   * 9. MOVIMIENTOS FINANCIEROS
   * ============================================================
   */

  console.log(
    'Generando ' +
      String(COUNTS.cashMovements) +
      ' movimientos financieros...'
  );

  const categories = [
    'Materiales',
    'Nomina',
    'Transporte',
    'Servicios',
    'Ventas',
    'Renta',
    'Equipo',
    'Operacion',
    'Otros'
  ];

  const cashMovements = [];

  for (
    let i = 1;
    i <= COUNTS.cashMovements;
    i++
  ) {
    const type = randomItem([
      'income',
      'expense'
    ] as const);

    cashMovements.push({
      tenantId,
      type,
      category: randomItem(categories),

      concept:
        TEST_PREFIX +
        'Movimiento financiero ' +
        i,

      amount: randomFloat(
        100,
        100000
      ),

      occurredAt: randomDate(365),
      createdBy,
      sourceType: 'test-seed',

      sourceId:
        TEST_PREFIX +
        'CASH-' +
        String(i).padStart(7, '0')
    });
  }

  await insertInChunks(
    CashMovementModel,
    cashMovements,
    500
  );

  /*
   * ============================================================
   * 10. RESUMEN
   * ============================================================
   */

  console.log('');
  console.log('==============================================');
  console.log('             SEED COMPLETADO');
  console.log('==============================================');
  console.log('');

  console.log(
    'Contactos:              ' +
      contacts.length
  );

  console.log(
    'Productos:              ' +
      products.length
  );

  console.log(
    'Balances inventario:    ' +
      stockBalances.length
  );

  console.log(
    'Proyectos:              ' +
      projects.length
  );

  console.log(
    'Ventas:                 ' +
      invoices.length
  );

  console.log(
    'Compras:                ' +
      purchases.length
  );

  console.log(
    'Movimientos inventario: ' +
      stockMovements.length
  );

  console.log(
    'Movimientos financieros:' +
      cashMovements.length
  );

  const total =
    contacts.length +
    products.length +
    stockBalances.length +
    projects.length +
    invoices.length +
    purchases.length +
    stockMovements.length +
    cashMovements.length;

  console.log('');
  console.log(
    'TOTAL DE REGISTROS: ' +
      total
  );

  console.log('');
  console.log(
    'Tenant: ' +
      String(tenantId)
  );

  console.log(
    'Prefijo de prueba: ' +
      TEST_PREFIX
  );

  console.log('');
  console.log(
    'Los registros pueden identificarse mediante TEST-.'
  );

  console.log('');
}

main()
  .catch((error) => {
    console.error('');
    console.error('==============================================');
    console.error('             ERROR EN EL SEED');
    console.error('==============================================');
    console.error('');
    console.error(error);
    process.exitCode = 1;
  })
  .finally(async () => {
    await disconnectDatabase();
  });