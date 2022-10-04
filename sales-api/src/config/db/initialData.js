import Order from "../../modules/sales/model/Order.js"

export async function createInitialData(){

  await Order.collection.drop();

  await Order.create({
      products: [
        {
          productId: 1001,
          quantity: 2
        },
        {
          productId: 1002,
          quantity: 2
        },
        {
          productId: 1001,
          quantity: 1
        },
      ],
      user: {
        id: "fdsfdsfsdfds",
        name: "John Doe",
        email: "john@example.com",
      },
      status: 'APPROVED',
      createdAt: new Date(),
      updatedAt: new Date(),
  });

  await Order.create({
    products: [
      {
        productId: 1001,
        quantity: 4
      },
      {
        productId: 1003,
        quantity: 2
      }
    ],
    user: {
      id: "hgfhgf230gfdig9fdkjgfdg",
      name: "John Doe 2",
      email: "john2@example.com",
    },
    status: 'REJECTED',
    createdAt: new Date(),
    updatedAt: new Date(),
});

let initialData = await Order.find();

 
console.info(`
Initial data was created: ${JSON.stringify(initialData, undefined, 4)}`)

}