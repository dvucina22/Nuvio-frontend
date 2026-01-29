import { StatusBreakdown } from "./StatusBreakdown";
import { Transaction } from "./Transaction";

export interface StatisticData {
    totalRevenue: number;
    totalTransactions: number;
    statusBreakdown: StatusBreakdown[];
    averageTransactionValue: number;
    recentTransactions: Transaction[];
}