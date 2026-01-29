import { apiRequest, isApiSuccessResponse } from "./apiClient";
import { User } from "@/types/user/User";

export async function getUsers(): Promise<User[]> {
  const response = await apiRequest<User[]>({
    method: "GET",
    url: "/users"
  });

  if (!isApiSuccessResponse(response)) {
    throw new Error(response.message);
  }

  return response.data;
}
