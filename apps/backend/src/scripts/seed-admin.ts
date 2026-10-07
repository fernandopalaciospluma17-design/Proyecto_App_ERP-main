import { connectDatabase, disconnectDatabase } from '../config/database.js';
import { env } from '../config/env.js';
import { TenantModel } from '../modules/auth/tenant.model.js';
import { UserModel } from '../modules/auth/user.model.js';
import { hashPassword, verifyPassword } from '../modules/auth/password.service.js';

const email = 'admin@nodara.local1';
const name = 'Administrador';

function isDuplicateKeyError(error: unknown): error is { code: number } {
  return typeof error === 'object' && error !== null && 'code' in error && error.code === 11000;
}

async function seedAdmin() {
  const password = process.env.SEED_ADMIN_PASSWORD;
  if (!password) throw new Error('Configura SEED_ADMIN_PASSWORD con la contraseña de prueba antes de ejecutar el seed.');

  if (env.NODE_ENV === 'production' && process.env.SEED_ADMIN_CONFIRM_PRODUCTION !== email) {
    throw new Error(`Seed bloqueado en producción. Para confirmar explícitamente, define SEED_ADMIN_CONFIRM_PRODUCTION=${email}.`);
  }

  if (env.NODE_ENV === 'production') {
    console.warn(`ADVERTENCIA: se creará o actualizará ${email} en producción. Es una cuenta de prueba; cambia su contraseña inmediatamente.`);
  }

  await connectDatabase();
  try {
    let user = await UserModel.findOne({ email }).select(
      '+passwordHash +verificationTokenHash +verificationExpiresAt'
    );
    let tenant = user && /^[a-f\d]{24}$/i.test(user.tenantId)
      ? await TenantModel.findById(user.tenantId)
      : null;

    if (!tenant) tenant = await TenantModel.create({ name: 'Nodara - Pruebas' });

    if (!user) {
      try {
        user = await UserModel.create({
          tenantId: String(tenant._id),
          email,
          name,
          passwordHash: await hashPassword(password),
          roles: ['admin'],
          isActive: true,
          emailVerifiedAt: new Date()
        });
      } catch (error) {
        if (!isDuplicateKeyError(error)) throw error;
        user = await UserModel.findOne({ email }).select(
          '+passwordHash +verificationTokenHash +verificationExpiresAt'
        );
        if (!user) throw error;
      }
    }

    let changed = false;
    if (user.tenantId !== String(tenant._id)) {
      user.tenantId = String(tenant._id);
      changed = true;
    }
    if (user.name !== name) {
      user.name = name;
      changed = true;
    }
    if (user.roles.length !== 1 || user.roles[0] !== 'admin') {
      user.roles = ['admin'];
      changed = true;
    }
    if (!user.isActive) {
      user.isActive = true;
      changed = true;
    }
    if (!user.emailVerifiedAt) {
      user.emailVerifiedAt = new Date();
      changed = true;
    }
    if (user.verificationTokenHash || user.verificationExpiresAt) {
      user.verificationTokenHash = null;
      user.verificationExpiresAt = null;
      changed = true;
    }
    if (!(await verifyPassword(password, user.passwordHash))) {
      user.passwordHash = await hashPassword(password);
      changed = true;
    }
    if (changed) await user.save();

    console.info(`Seed listo: ${email}`);
    console.info(`Usuario: ${user.id}; tenant: ${user.tenantId}; roles: ${user.roles.join(', ')}`);
    console.info(`Correo verificado: ${user.emailVerifiedAt?.toISOString()}; contraseña almacenada con scrypt.`);
    console.warn('La contraseña de esta cuenta de pruebas es conocida; cámbiala inmediatamente si se usa fuera de desarrollo.');
  } finally {
    await disconnectDatabase();
  }
}

seedAdmin().catch((error: unknown) => {
  console.error('No se pudo crear el administrador de pruebas:', error);
  process.exitCode = 1;
});
