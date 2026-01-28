interface CostChartProps {
    cost: number;
}

const CostChart: React.FC<CostChartProps> = ({ cost }) => {
    return (
        <div>
            <h2>Total Cost: ${cost.toFixed(2)}</h2>
        </div>
    );
}


export default CostChart;