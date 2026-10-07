import mongoose from 'mongoose';
import { env } from './env.js';
import dns from 'node:dns';
dns.setServers(['1.1.1.1', '8.8.8.8']);

export async function connectDatabase():Promise<void>{ await mongoose.connect(env.MONGODB_URI,{serverSelectionTimeoutMS:10000, retryWrites:true}); }
export async function disconnectDatabase():Promise<void>{ await mongoose.disconnect(); }
