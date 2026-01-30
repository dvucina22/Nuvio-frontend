"use client"

import * as React from "react"
import { CartesianGrid, XAxis, YAxis, Line, LineChart, ResponsiveContainer } from "recharts"

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import {
  ChartContainer,
  ChartLegend,
  ChartLegendContent,
  ChartTooltip,
  ChartTooltipContent,
  type ChartConfig,
} from "@/components/ui/chart"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Transaction } from "@/types/stats/Transaction"

interface TransactionsAmountAreaByStatusChartProps {
  transactions: Transaction[]
  onChangeLimit?: (limit: number | undefined) => void
}

const chartConfig = {
  APPROVED: {
    label: "Approved",
    color: "var(--chart-1)",
  },
} satisfies ChartConfig

export default function TransactionsAmountAreaByStatusChart({ 
  transactions,
  onChangeLimit,
}: TransactionsAmountAreaByStatusChartProps) {
  const [limit, setLimit] = React.useState<string>("20")

  const handleLimitChange = (value: string) => {
    setLimit(value)
    if (onChangeLimit) {
      onChangeLimit(value === "all" ? undefined : parseInt(value))
    }
  }

  const processedData = React.useMemo(() => {
    if (!transactions || transactions.length === 0) return []

    const approvedTransactions = transactions
      .filter(transaction => transaction.status === "APPROVED")
      .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())

    return approvedTransactions.map((transaction) => ({
      date: transaction.createdAt,
      APPROVED: transaction.amount,
      type: transaction.type,
      id: transaction.id,
    }))
  }, [transactions])

  const filteredData = React.useMemo(() => {
    if (processedData.length === 0) return []

    if (limit === "all") {
      return processedData
    }

    const limitNum = parseInt(limit)
    return processedData.slice(-limitNum)
  }, [processedData, limit])

  return (
    <Card className="w-full">
      <CardHeader className="flex items-center gap-2 space-y-0 border-b py-5 sm:flex-row">
        <div className="grid flex-1 gap-1">
          <CardTitle>Approved transaction amounts</CardTitle>
          <CardDescription>
            Showing the most recent approved transactions
          </CardDescription>
        </div>
        <Select value={limit} onValueChange={handleLimitChange}>
          <SelectTrigger
            className="w-[160px] rounded-lg sm:ml-auto"
            aria-label="Select transaction limit"
          >
            <SelectValue placeholder="Last 20" />
          </SelectTrigger>
          <SelectContent className="rounded-xl">
            <SelectItem value="10" className="rounded-lg">
              Last 10
            </SelectItem>
            <SelectItem value="20" className="rounded-lg">
              Last 20
            </SelectItem>
            <SelectItem value="50" className="rounded-lg">
              Last 50
            </SelectItem>
            <SelectItem value="100" className="rounded-lg">
              Last 100
            </SelectItem>
          </SelectContent>
        </Select>
      </CardHeader>
      <CardContent className="px-2 pt-4 sm:px-6 sm:pt-6">
        {filteredData.length === 0 ? (
          <div className="flex h-[250px] items-center justify-center text-muted-foreground">
            No transaction data available
          </div>
        ) : (
          <ChartContainer
            config={chartConfig}
            className="aspect-auto h-[250px] w-full"
          >
            <LineChart data={filteredData}>
              <CartesianGrid vertical={false} strokeDasharray="3 3" />
              <XAxis
                dataKey="date"
                tickLine={false}
                axisLine={false}
                tickMargin={8}
                minTickGap={32}
                tickFormatter={(value) => {
                  const date = new Date(value)
                  return date.toLocaleDateString("en-US", {
                    month: "short",
                    day: "numeric",
                  })
                }}
              />
              <YAxis
                tickLine={false}
                axisLine={false}
                tickMargin={8}
                tickFormatter={(value) => `$${value.toLocaleString()}`}
              />
              <ChartTooltip
                cursor={false}
                content={
                  <ChartTooltipContent
                    labelFormatter={(value) => {
                      return new Date(value).toLocaleDateString("en-US", {
                        month: "short",
                        day: "numeric",
                        year: "numeric",
                        hour: "2-digit",
                        minute: "2-digit",
                      })
                    }}
                    formatter={(value) => `$${Number(value).toLocaleString()}`}
                    indicator="dot"
                  />
                }
              />
              <Line
                dataKey="APPROVED"
                type="monotone"
                stroke="var(--color-APPROVED)"
                strokeWidth={2}
                dot={{ r: 4, fill: "var(--color-APPROVED)" }}
                activeDot={{ r: 6 }}
                connectNulls={false}
              />
              <ChartLegend content={<ChartLegendContent />} />
            </LineChart>
          </ChartContainer>
        )}
      </CardContent>
    </Card>
  )
}