import { useGetStats } from "@/api/hooks/statistics/useGetStats";
import { useEffect } from "react";

const DashboardPage: React.FC = () => {
    const {getStats, loading, error } = useGetStats();

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const stats = await getStats();
                console.log("Fetched stats:", stats);
            }
            catch(err) {
                console.error("Error fetching stats:", err);
            }
        };

        fetchStats();
    }, []);

    return (
    <div className="p-6">
    </div>
    )
}

export default DashboardPage;