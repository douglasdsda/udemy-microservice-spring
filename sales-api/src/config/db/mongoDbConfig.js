import mongoose from 'mongoose';

import { MONGO_DB_URL } from '../constants/secrets.js'

export async function connectMongoDb(){
 mongoose.connect(MONGO_DB_URL) 
  .then(() => {
    console.info('Connectad:::');
  })
  .catch((err) => {
    console.error('Error:::', err);
  })

  mongoose.connection.on("connected", function(){
    console.info("The application connected to MongoDB successfully!")
  })

  mongoose.connection.on("error", function(){
    console.error("The application error")
  })

 
}