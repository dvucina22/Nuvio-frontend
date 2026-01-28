import { getTransactionStats } from "@/api/services/transaction-service/transactionService";
import { StatisticResponse } from "@/types/stats/StatisticResponse";
import { useState } from "react";

export function useGetStats() {
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const getStats = async (): Promise<StatisticResponse> => {
        setLoading(true);
        setError(null);

        try {
            const response = await getTransactionStats();
            return response;
        } catch (err) {
            const errorMessage = err instanceof Error ? err.message : 'Login failed';
            setError(errorMessage);
            throw err;
        } finally {
            setLoading(false);
        }
    }

    return { getStats, loading, error };

}