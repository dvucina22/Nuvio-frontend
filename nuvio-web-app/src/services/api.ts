import ENV from '../config/env';
import { API_ENDPOINTS, HTTP_STATUS } from '../constants/api';
import storage from '../utils/storage';
import { ApiResponse, LoginCredentials, AuthResponse, User } from '../types';

interface RequestOptions extends RequestInit {
  includeAuth?: boolean;
  service?: 'account' | 'catalog' | 'transaction';
}

class ApiService {
  private getServiceURL(service: string = 'account'): string {
    switch (service) {
      case 'account':
        return ENV.ACCOUNT_SERVICE_URL;
      case 'catalog':
        return ENV.CATALOG_SERVICE_URL;
      case 'transaction':
        return ENV.TRANSACTION_SERVICE_URL;
      default:
        return ENV.ACCOUNT_SERVICE_URL;
    }
  }

  private getHeaders(includeAuth: boolean = true): HeadersInit {
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };

    if (includeAuth) {
      const token = storage.getAuthToken();
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
    }

    return headers;
  }

  private async request<T = any>(
    endpoint: string,
    options: RequestOptions = {}
  ): Promise<ApiResponse<T>> {
    const { includeAuth = true, service = 'account', ...fetchOptions } = options;

    try {
      const baseURL = this.getServiceURL(service);
      const headers = this.getHeaders(includeAuth);

      const response = await fetch(`${baseURL}${endpoint}`, {
        ...fetchOptions,
        headers: {
          ...headers,
          ...fetchOptions.headers,
        },
        credentials: 'include',
        mode: 'cors',
      });

      const data = await response.json();

      if (!response.ok) {
        return this.handleError(response, data);
      }

      return { success: true, data };
    } catch (error) {
      return {
        success: false,
        error: error instanceof Error ? error.message : 'Network error occurred',
      };
    }
  }

  private handleError(response: Response, data: any): ApiResponse {
    const errorMessage = data.message || 'An error occurred';

    switch (response.status) {
      case HTTP_STATUS.UNAUTHORIZED:
        storage.clearAuth();
        return {
          success: false,
          error: errorMessage,
          shouldLogout: true,
        };
      case HTTP_STATUS.FORBIDDEN:
        return {
          success: false,
          error: 'You do not have permission to perform this action',
        };
      case HTTP_STATUS.NOT_FOUND:
        return {
          success: false,
          error: 'The requested resource was not found',
        };
      case HTTP_STATUS.INTERNAL_SERVER_ERROR:
        return {
          success: false,
          error: 'Server error. Please try again later.',
        };
      default:
        return { success: false, error: errorMessage };
    }
  }
  
  async login(credentials: LoginCredentials): Promise<ApiResponse<AuthResponse>> {
    return this.request<AuthResponse>(API_ENDPOINTS.ACCOUNT.LOGIN, {
      method: 'POST',
      body: JSON.stringify(credentials),
      includeAuth: false,
      service: 'account',
    });
  }

  async register(userData: any): Promise<ApiResponse<AuthResponse>> {
    return this.request<AuthResponse>(API_ENDPOINTS.ACCOUNT.REGISTER, {
      method: 'POST',
      body: JSON.stringify(userData),
      includeAuth: false,
      service: 'account',
    });
  }

  async getUserProfile(): Promise<ApiResponse<User>> {
    return this.request<User>(API_ENDPOINTS.ACCOUNT.PROFILE, {
      service: 'account',
    });
  }

  async updateUserProfile(userData: Partial<User>): Promise<ApiResponse<{ user: User }>> {
    return this.request<{ user: User }>(API_ENDPOINTS.ACCOUNT.UPDATE_PROFILE, {
      method: 'PUT',
      body: JSON.stringify(userData),
      service: 'account',
    });
  }

  async getProducts(params?: any): Promise<ApiResponse<any[]>> {
    const queryString = params ? `?${new URLSearchParams(params)}` : '';
    return this.request(API_ENDPOINTS.CATALOG.PRODUCTS + queryString, {
      service: 'catalog',
    });
  }

  async getProductById(id: string): Promise<ApiResponse<any>> {
    return this.request(API_ENDPOINTS.CATALOG.PRODUCT_BY_ID.replace(':id', id), {
      service: 'catalog',
    });
  }

  async getCategories(): Promise<ApiResponse<any[]>> {
    return this.request(API_ENDPOINTS.CATALOG.CATEGORIES, {
      service: 'catalog',
    });
  }

  async searchProducts(query: string): Promise<ApiResponse<any[]>> {
    return this.request(`${API_ENDPOINTS.CATALOG.SEARCH}?q=${encodeURIComponent(query)}`, {
      service: 'catalog',
    });
  }

  async getOrders(): Promise<ApiResponse<any[]>> {
    return this.request(API_ENDPOINTS.TRANSACTION.ORDERS, {
      service: 'transaction',
    });
  }

  async getOrderById(id: string): Promise<ApiResponse<any>> {
    return this.request(API_ENDPOINTS.TRANSACTION.ORDER_BY_ID.replace(':id', id), {
      service: 'transaction',
    });
  }

  async createOrder(orderData: any): Promise<ApiResponse<any>> {
    return this.request(API_ENDPOINTS.TRANSACTION.CREATE_ORDER, {
      method: 'POST',
      body: JSON.stringify(orderData),
      service: 'transaction',
    });
  }

  async getPaymentHistory(): Promise<ApiResponse<any[]>> {
    return this.request(API_ENDPOINTS.TRANSACTION.PAYMENT_HISTORY, {
      service: 'transaction',
    });
  }

  async processPayment(paymentData: any): Promise<ApiResponse<any>> {
    return this.request(API_ENDPOINTS.TRANSACTION.PAYMENTS, {
      method: 'POST',
      body: JSON.stringify(paymentData),
      service: 'transaction',
    });
  }
}

const apiService = new ApiService();
export default apiService;