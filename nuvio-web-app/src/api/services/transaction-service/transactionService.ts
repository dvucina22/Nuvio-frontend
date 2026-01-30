import { StatisticResponse } from "@/types/stats/StatisticResponse";
import { apiRequest, isApiSuccessResponse } from "./apiClient";

export async function getTransactionStats(limit?: number): Promise<StatisticResponse> {
  const response = await apiRequest<StatisticResponse>({
    method: "GET",
    url: "/statistics",
    params: limit ? { transactions: limit } : {},
  });

  if (!isApiSuccessResponse(response)) {
    throw new Error(response.message);
  }

  return response.data;
}
