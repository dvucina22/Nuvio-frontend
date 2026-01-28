import { login } from "@/api/services/account-service/authService";
import { LoginResponse } from "@/types/LoginResponse";
import { PasswordHasher } from "@/utils/passwordHasher";
import { useState } from "react";

export function useLogin() {
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const loginUser = async (email: string, password: string): Promise<LoginResponse> => {
        setLoading(true);
        setError(null);

        try {
            const passwordHash = await PasswordHasher.transformPassword(password, email);
            const response = await login({ email, password: passwordHash     });
            return response;
        } catch (err) {
            const errorMessage = err instanceof Error ? err.message : 'Login failed';
            setError(errorMessage);
            throw err;
        } finally {
            setLoading(false);
        }
    }

    return { loginUser, loading, error };

}