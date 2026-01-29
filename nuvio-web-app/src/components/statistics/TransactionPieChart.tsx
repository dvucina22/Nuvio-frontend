import { Legend, Pie, PieChart } from "recharts"
import { StatusBreakdown } from "@/types/stats/StatusBreakdown"

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
  type ChartConfig,
} from "@/components/ui/chart"

interface TransactionsPieChartProps {
  transactions: StatusBreakdown[];
}

const TransactionsPieChart: React.FC<TransactionsPieChartProps> = ({ transactions }) => {
  const chartData = transactions.map((transaction, index) => ({
    status: transaction.status,
    count: transaction.count,
    fill: `var(--chart-${(index % 5) + 1})`,
  }))

  const chartConfig = transactions.reduce((config, transaction, index) => {
    config[transaction.status] = {
      label: transaction.status,
      color: `var(--chart-${(index % 5) + 1})`,
    }
    return config
  }, { count: { label: "Count" } } as ChartConfig)

  const legendPayload = chartData.map((d) => ({
    value: d.status,
    type: "square" as const,
    color: d.fill,
  }))

  return (
    <Card className="flex flex-col w-full">
      <CardHeader className="items-center pb-0">
        <CardTitle>Transaction status</CardTitle>
        <CardDescription>Status breakdown</CardDescription>
      </CardHeader>
      <CardContent className="flex-1 pb-0">
        <ChartContainer
          config={chartConfig}
          className="[&_.recharts-pie-label-text]:fill-foreground mx-auto aspect-square max-h-[250px] pb-0"
        >
          <PieChart>
            <ChartTooltip content={<ChartTooltipContent hideLabel />} />

            <Pie
              data={chartData}
              dataKey="count"
              label
              nameKey="status"
            />

            <Legend
              payload={legendPayload}
              verticalAlign="bottom"
              align="center"
              wrapperStyle={{ padding: 12 }}
              formatter={(value) => (
                <span className="text-[12px] text-muted-foreground">{value}</span>
              )}
            />
          </PieChart>
        </ChartContainer>
      </CardContent>
    </Card>
  )
}

export default TransactionsPieChart