import { useGetStats } from "@/api/hooks/statistics/useGetStats";
import TransactionsPieChart from "@/components/statistics/TransactionPieChart";
import { StatisticData } from "@/types/stats/StatisticData";
import { useEffect, useState } from "react";


const DashboardPage: React.FC = () => {
    const {getStats, loading, error } = useGetStats();
    const [stats, setStats] = useState<StatisticData | null>(null);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const stats = await getStats();
                console.log("Fetched stats:", stats);
                setStats(stats.data);
            }
            catch(err) {
                console.error("Error fetching stats:", err);
            }
        };

        fetchStats();
    }, []);

    return (
    <div className="p-6 mt-[97px]">
        <TransactionsPieChart transactions={stats?.statusBreakdown || []} />
    </div>
    )
}

export default DashboardPage;