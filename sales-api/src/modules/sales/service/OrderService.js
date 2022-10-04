import OrderRepository from "../repository/OrderRepository.js";
import { sendMessageToProductStockUpdateQueue } from "../../product/rabbitmq/productStockUpdateSender.js";
import { ACCEPTED, PENDING, REJECTED } from "../status/OrderStatus.js";
import OrderException from "../exception/OrderException.js";
import { BAD_REQUEST, SUCCESS } from "../../../config/constants/HttpStatus.js";

class OrderService {
  async createOrder() {
    try {
      let orderData = req.body;
      this.validateOrderData(orderData);
      const { authUser } = req;
      const { authorization } = req.headers;
      let order = this.createInitialData(orderData, authUser);
      await this.validateProductsTock(order);
      let createdOrder = await OrderRepository.save(order);
      // sendMessageToProductStockUpdateQueue(createdOrder.products);
      this.sendMessage(createdOrder);
      return {
        status: SUCCESS,
        createdOrder,
      };
    } catch (err) {
      return {
        status: err.status ? err.status : INTERNAL_SERVER_ERROR,
        message: err.message,
      };
    }
  }

  createInitialData(orderData, authUser) {
    return {
      status: PENDING,
      user: authUser,
      createdAt: new Date(),
      updatedAt: new Date(),
      products: orderData,
    };
  }

  async updateOrder(orderMessage) {
    try {
      const order = JSON.parse(orderMessage);
      if (order.salesId && order.status) {
        let existingOrder = await OrderRepository.findById(order.salesId);
        if (order.status && order.status !== existingOrder.status) {
          existingOrder.status = order.status;
          await OrderRepository.save(existingOrder);
        }
      } else {
        console.warn("The order message was not complete.");
      }
    } catch (err) {
      console.error("Clould not parse order message from queue.");
      console.error(err.message);
    }
  }

  validateOrderData() {
    if (!data || !data.products)
      OrderException(BAD_REQUEST, "The products must be informed.");
  }

  async validateProductsTock(order) {
    let stockIsOut = true;
    if (stockIsOut) {
      throw new OrderException(
        BAD_REQUEST,
        "The stock is out for the products."
      );
    }
  }
  sendMessage(createdOrder) {
    const message = {
      salesId: createdOrder.id,
      products:createdOrder.products
    }
    sendMessageToProductStockUpdateQueue(message);
  }
}

export default new OrderService();
