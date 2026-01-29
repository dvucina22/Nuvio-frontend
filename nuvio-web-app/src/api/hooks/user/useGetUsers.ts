import { getUsers } from "@/api/services/account-service/userService";
import { useState } from "react";

export function useGetUsers() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function getAllUsers() {
        setLoading(true);
        setError(null);

        try {
            const users = await getUsers();
            return users;
        } catch (err: any) {
            setError(err.message || "An error occurred while fetching users.");
            throw err;
        } finally {
            setLoading(false);
        }
    }

    return {
        getAllUsers,
        loading,
        error,
    };
}