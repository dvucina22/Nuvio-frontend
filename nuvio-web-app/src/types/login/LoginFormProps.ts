import { LoginResponse } from "./LoginResponse";

export interface LoginFormProps {
  onSubmit: (email: string, password: string) => Promise<LoginResponse>;
  loading: boolean;
}