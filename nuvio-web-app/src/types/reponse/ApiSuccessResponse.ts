export type ApiSuccessResponse<T> = {
    success: boolean;
    data: T;
    status: number;
    statusText: string;
};