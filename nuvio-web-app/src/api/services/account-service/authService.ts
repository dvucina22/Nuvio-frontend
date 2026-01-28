import { LoginCredentials } from "@/types/login/LoginCredentials";
import { apiRequest, isApiSuccessResponse } from "./apiClient";
import { LoginResponse } from "@/types/LoginResponse";

export async function login(credentials: LoginCredentials): Promise<LoginResponse> {
  const response = await apiRequest<LoginResponse>({
    method: "POST",
    url: "/login",
    data: credentials,
  });

  if (!isApiSuccessResponse(response)) {
    throw new Error(response.message);
  }

  return response.data;
}
