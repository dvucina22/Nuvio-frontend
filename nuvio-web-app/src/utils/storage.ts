export const STORAGE_KEYS = {
  AUTH_TOKEN: 'authToken',
  USER_DATA: 'userData',
  REFRESH_TOKEN: 'refreshToken',
} as const;

class StorageService {
  setItem<T>(key: string, value: T): boolean {
    try {
      const jsonValue = JSON.stringify(value);
      localStorage.setItem(key, jsonValue);
      return true;
    } catch (error) {
      console.error(`Error storing ${key}:`, error);
      return false;
    }
  }

  getItem<T>(key: string): T | null {
    try {
      const jsonValue = localStorage.getItem(key);
      return jsonValue != null ? JSON.parse(jsonValue) : null;
    } catch (error) {
      console.error(`Error retrieving ${key}:`, error);
      return null;
    }
  }

  removeItem(key: string): boolean {
    try {
      localStorage.removeItem(key);
      return true;
    } catch (error) {
      console.error(`Error removing ${key}:`, error);
      return false;
    }
  }

  clear(): boolean {
    try {
      localStorage.clear();
      return true;
    } catch (error) {
      console.error('Error clearing storage:', error);
      return false;
    }
  }

  setAuthToken(token: string): boolean {
    return this.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
  }

  getAuthToken(): string | null {
    return this.getItem<string>(STORAGE_KEYS.AUTH_TOKEN);
  }

  setUserData<T>(userData: T): boolean {
    return this.setItem(STORAGE_KEYS.USER_DATA, userData);
  }

  getUserData<T>(): T | null {
    return this.getItem<T>(STORAGE_KEYS.USER_DATA);
  }

  clearAuth(): void {
    this.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    this.removeItem(STORAGE_KEYS.USER_DATA);
    this.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
  }
}

export default new StorageService();