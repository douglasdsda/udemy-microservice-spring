const env = process.env;

export const MONGO_DB_URL = env.MONGO_DB_URL 
  //  ? env.MONGO_DB_URL : "mongodb://admin:123456@localhost:27017/sales"
   ? env.MONGO_DB_URL : 'mongodb://root:123456@localhost:27017'
  // ? env.MONGO_DB_URL : "mongodb://localhost:27017/sales"
 
export const API_SECRET = env.API_SECRET
  ? env.API_SECRET
  : "YXUtYXBpLXNlY3JldC1kZXY9MTIzNDU2";

  export const RABBIT_MQ_URL = env.RABBIT_MQ_URL
  ? env.RABBIT_MQ_URL
  : "amqp://localhost:5672";

export const PRODUCT_API_URL = env.PRODUCT_API_URL
  ? env.PRODUCT_API_URL
  : "http://localhost:8081/api/product";