import { useGetStats } from "@/api/hooks/statistics/useGetStats";
import CostChart from "@/components/statistics/CostChart";
import TransactionsPieChart from "@/components/statistics/TransactionPieChart";
import { StatisticData } from "@/types/stats/StatisticData";
import { useEffect, useLayoutEffect, useRef, useState } from "react";
import { gsap } from "gsap";
import TransactionsAmountAreaByStatusChart from "@/components/statistics/TransactionsAmountAreaByStatusChart";

const DashboardPage: React.FC = () => {
    const { getStats, loading, error } = useGetStats();

    const [stats, setStats] = useState<StatisticData | null>(null);

    const containerRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        fetchStats();
    }, []);

    const fetchStats = async (limit?: number) => {
        try {
            const stats = await getStats(limit);
            console.log("Fetched stats:", stats);
            setStats(stats.data);
        }
        catch (err) {
            console.error("Error fetching stats:", err);
        }
    };    

    useLayoutEffect(() => {
        if (!containerRef.current) return;

        const ctx = gsap.context(() => {
            const pie = document.querySelector('[data-anim="pie"]');
            const cost = document.querySelector('[data-anim="cost"]');
            const area = document.querySelector('[data-anim="area"]');

            gsap.set([pie, cost, area], { autoAlpha: 0 });

            gsap.timeline({ defaults: { ease: "power3.out" } })
                .fromTo(
                    pie,
                    { x: -60, autoAlpha: 0 },
                    { x: 0, autoAlpha: 1, duration: 0.7 }
                )
                .fromTo(
                    cost,
                    { x: 60, autoAlpha: 0 },
                    { x: 0, autoAlpha: 1, duration: 0.7 },
                    "-=0.45"
                )
                .fromTo(
                    area,
                    { y: 30, autoAlpha: 0 },
                    { y: 0, autoAlpha: 1, duration: 0.7 },
                    "-=0.35"
                );
        }, containerRef);

        return () => ctx.revert();
    }, [stats]);

    return (
        <div
            ref={containerRef}
            className="mt-[120px] flex flex-col gap-[12px] h-fit max-w-[1441px] w-full"
        >
            <div className="flex gap-[12px] w-full">
                <div data-anim="pie" className="w-full max-w-[40%]">
                    <TransactionsPieChart transactions={stats?.statusBreakdown || []} />
                </div>

                <div data-anim="cost" className="w-full">
                    <CostChart
                        cost={stats?.totalRevenue || 0}
                        numberOfTransactions={stats?.totalTransactions ?? 0}
                    />
                </div>
            </div>

            <div data-anim="area" className="w-full">
                <TransactionsAmountAreaByStatusChart onChangeLimit={fetchStats} transactions={stats?.recentTransactions || []} />
            </div>
        </div>
    );
};

export default DashboardPage;