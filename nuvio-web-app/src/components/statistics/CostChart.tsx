import { Card } from "../ui/card";

interface CostChartProps {
    cost: number;
    numberOfTransactions: number;
}

const CostChart: React.FC<CostChartProps> = ({ cost, numberOfTransactions }) => {
    return (
        <Card className="flex flex-col gap-[20px] justify-between py-[68px] px-8 w-full h-full">
            <div className="flex flex-col gap-[12px]">
                <p>Total revenue</p>
                <h2 className="text-3xl font-bold">{cost.toFixed(2)}€</h2>
            </div>
            <div className="flex flex-col gap-[12px]">
                <p>Total transactions</p>
                <h2 className="text-3xl font-bold">{numberOfTransactions}</h2>
            </div>
        </Card>
    );
}


export default CostChart;