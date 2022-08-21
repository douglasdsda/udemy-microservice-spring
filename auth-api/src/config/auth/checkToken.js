import jwt from "jsonwebtoken";
import { promisify } from "util";

import AuthException from "./AuthException.js";

import * as secrets from "../contants/secrets.js";
import * as httpStatus from "../contants/httpStatus.js";

const bearer = "bearer ";

export default async (req, res, next) => {
  try {
    const { authorization } = req.headers;
    if (!authorization) {
      throw new AuthException(
        httpStatus.UNAUTHORIZED,
        "Authorization token was not informed."
      );
    }
    let acessToken = authorization;
    if (acessToken.toLowerCase().includes(bearer))
      acessToken = acessToken.split(" ")[1];

    if (!acessToken)
      throw new AuthException(
        httpStatus.UNAUTHORIZED,
        "Access token was not informed."
      );

    const decoded = await promisify(jwt.verify)(acessToken, secrets.API_SECRET);

    req.authUser = decoded.authUser;
    return next();
  } catch (err) {
    const status = err.status ? err.status : httpStatus.INTERNAL_SERVER_ERROR;
    return res.status(status).json({
      status,
      message: err.message,
    });
  }
};
