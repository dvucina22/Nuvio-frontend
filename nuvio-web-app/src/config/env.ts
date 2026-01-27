interface EnvConfig {
  ACCOUNT_SERVICE_URL: string;
  CATALOG_SERVICE_URL: string;
  TRANSACTION_SERVICE_URL: string;
}

const config = {
  development: {
    ACCOUNT_SERVICE_URL: process.env.REACT_APP_ACCOUNT_URL || 'https://nuvio.lol:8001/api/accounts',
    CATALOG_SERVICE_URL: process.env.REACT_APP_CATALOG_URL || 'https://nuvio.lol/api/catalog',
    TRANSACTION_SERVICE_URL: process.env.REACT_APP_TRANSACTION_URL || 'https://nuvio.lol/api/transactions',
  },
  production: {
    ACCOUNT_SERVICE_URL: process.env.REACT_APP_ACCOUNT_URL || 'https://nuvio.lol:8001/api/accounts',
    CATALOG_SERVICE_URL: process.env.REACT_APP_CATALOG_URL || 'https://nuvio.lol/api/catalog',
    TRANSACTION_SERVICE_URL: process.env.REACT_APP_TRANSACTION_URL || 'https://nuvio.lol/api/transactions',
  },
};

const getEnvVars = (): EnvConfig => {
  const env = process.env.NODE_ENV || 'development';
  return config[env as keyof typeof config];
};

export default getEnvVars();