import { StatusBreakdown } from "./StatusBreakdown";

export interface StatisticData {
    totalRevenue: number;
    totalTransactions: number;
    statusBreakdown: StatusBreakdown[];
    averageTransactionValue: number;
}