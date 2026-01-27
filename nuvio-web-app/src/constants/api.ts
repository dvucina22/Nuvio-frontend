
export const API_ENDPOINTS = {
  ACCOUNT: {
    LOGIN: '/login',
    REGISTER: '/register',
    PROFILE: '/user/profile',
    UPDATE_PROFILE: '/user/profile',
  },
  
  CATALOG: {
    PRODUCTS: '/products',
    PRODUCT_BY_ID: '/products/:id',
    CATEGORIES: '/categories',
    SEARCH: '/search',
  },
  
  TRANSACTION: {
    ORDERS: '/orders',
    ORDER_BY_ID: '/orders/:id',
    CREATE_ORDER: '/orders',
    PAYMENTS: '/payments',
    PAYMENT_HISTORY: '/payments/history',
  },
} as const;

export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
} as const;

export enum HttpStatus {
  OK = 200,
  CREATED = 201,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  INTERNAL_SERVER_ERROR = 500,
}