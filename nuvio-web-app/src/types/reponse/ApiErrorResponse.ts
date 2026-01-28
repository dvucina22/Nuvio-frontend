export type ApiErrorResponse = {
    success: boolean;
    message: string;
    status: number;
    error?: unknown;
    timestamp: string;
};