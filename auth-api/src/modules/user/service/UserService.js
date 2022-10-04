import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import UserRepository from "../repository/UserRepository.js";
import {  SUCCESS, INTERNAL_SERVER_ERROR, BAD_REQUEST, FORBIDDEN,  } from "../../../config/constants/HttpStatus.js";
import UserException from "../exception/UserException.js";
import { API_SECRET } from "../../../config/constants/secrets.js";
class UserService {
  async findByEmail(req) {
    try {
      const { email } = req.params;
      const { authUser } = req;
      this.validateRequestData(email);
      let user = await UserRepository.findByEmail(email);

      this.validateUserNotFound(user);
      this.validateAuthenticatedUser(user, authUser);
       
      return {
        status: SUCCESS,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
        }
      };
    } catch (error) {
      return {
        status: error.status ? error.status : INTERNAL_SERVER_ERROR,
        message: error.message,
      };
    }
  }

  async getAccessToken(req) {
    try {
      const { email, password } = req.body;
      this.validateAcessTokenData(email, password);
      let user = await UserRepository.findByEmail(email);
      this.validateUserNotFound(user);

      await this.validatePassword(password, user.password);
      let authUser = { id: user.id, name: user.name, email: user.email };
      const acessToken = jwt.sign({ authUser }, API_SECRET, {
        expiresIn: "1d",
      });

      return {
        status: SUCCESS,
        acessToken,
      };

    } catch (error) {
      return {
        status: error.status ? error.status : INTERNAL_SERVER_ERROR,
        message: error.message,
      };
    }
  }

  validateRequestData(email) {
    if (!email) {
      throw new UserException(
        BAD_REQUEST,
        "User email was not informed."
      );
    }
  }

  validateUserNotFound(user) {
    if (!user) {
      throw new UserException(
        BAD_REQUEST,
        "User user was not found."
      );
    }
  }

  async validatePassword(password, hashPassword) {
    if (!(await bcrypt.compare(password, hashPassword))) {
      throw new UserException(
        UNAUTHORIZED,
        "Password doesn't match."
      );
    }
  }

  validateAcessTokenData(email, password) {
    if (!email || !password) {
      throw new UserException(
        UNAUTHORIZED,
        "Email and password must be informed."
      );
    }
  }
 
  validateAuthenticatedUser(user, authUser) {
    if (!authUser || user.id !== authUser.id) {
      throw new UserException(
        FORBIDDEN,
        "You cannout see this user data."
      )
    }
  }
}

export default new UserService();
