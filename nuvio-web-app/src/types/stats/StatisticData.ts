import { statusBreakdown } from "./StatusBreakdown";

export interface StatisticData {
    totalRevenue: number;
    totalTransactions: number;
    statusBreakdown: statusBreakdown[];
    averageTransactionValue: number;
}