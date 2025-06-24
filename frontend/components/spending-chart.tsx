"use client"

import { useTheme } from "next-themes"
import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"

export function SpendingChart() {
  const { theme } = useTheme()

  // Mock data - in a real app, this would come from your API
  const data = [
    {
      name: "Groceries",
      amount: 450,
    },
    {
      name: "Dining",
      amount: 300,
    },
    {
      name: "Transport",
      amount: 200,
    },
    {
      name: "Shopping",
      amount: 278,
    },
    {
      name: "Utilities",
      amount: 189,
    },
    {
      name: "Entertainment",
      amount: 239,
    },
    {
      name: "Health",
      amount: 180,
    },
    {
      name: "Other",
      amount: 120,
    },
  ]

  const textColor = theme === "dark" ? "#f8fafc" : "#0f172a"
  const gridColor = theme === "dark" ? "#334155" : "#e2e8f0"

  return (
    <div className="h-[300px] w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke={gridColor} />
          <XAxis dataKey="name" stroke={textColor} fontSize={12} tickLine={false} axisLine={false} />
          <YAxis
            stroke={textColor}
            fontSize={12}
            tickLine={false}
            axisLine={false}
            tickFormatter={(value) => `$${value}`}
          />
          <Tooltip
            formatter={(value) => [`$${value}`, "Amount"]}
            contentStyle={{
              backgroundColor: theme === "dark" ? "#1e293b" : "#ffffff",
              color: textColor,
              border: `1px solid ${gridColor}`,
            }}
          />
          <Legend />
          <Bar dataKey="amount" fill="#8884d8" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}
