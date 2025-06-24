"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Search, Download, Filter } from "lucide-react"
import { cn } from "@/lib/utils"

export function TransactionHistory() {
  const [searchTerm, setSearchTerm] = useState("")

  // Mock data - in a real app, this would come from your API
  const transactions = [
    {
      id: "TX123456",
      date: "Jun 1, 2025",
      description: "Salary Deposit",
      category: "Income",
      amount: 2500.0,
      status: "completed",
    },
    {
      id: "TX123457",
      date: "May 31, 2025",
      description: "Netflix Subscription",
      category: "Entertainment",
      amount: -14.99,
      status: "completed",
    },
    {
      id: "TX123458",
      date: "May 30, 2025",
      description: "Grocery Store",
      category: "Groceries",
      amount: -78.52,
      status: "completed",
    },
    {
      id: "TX123459",
      date: "May 29, 2025",
      description: "Transfer to Savings",
      category: "Transfer",
      amount: -500.0,
      status: "completed",
    },
    {
      id: "TX123460",
      date: "May 28, 2025",
      description: "Gas Station",
      category: "Transportation",
      amount: -45.23,
      status: "completed",
    },
    {
      id: "TX123461",
      date: "May 27, 2025",
      description: "Restaurant Payment",
      category: "Dining",
      amount: -62.75,
      status: "completed",
    },
    {
      id: "TX123462",
      date: "May 26, 2025",
      description: "Online Shopping",
      category: "Shopping",
      amount: -129.99,
      status: "completed",
    },
    {
      id: "TX123463",
      date: "May 25, 2025",
      description: "Utility Bill",
      category: "Utilities",
      amount: -85.0,
      status: "completed",
    },
    {
      id: "TX123464",
      date: "May 24, 2025",
      description: "Mobile Phone Bill",
      category: "Utilities",
      amount: -55.0,
      status: "completed",
    },
    {
      id: "TX123465",
      date: "May 23, 2025",
      description: "Interest Payment",
      category: "Income",
      amount: 12.37,
      status: "completed",
    },
  ]

  const filteredTransactions = transactions.filter(
    (transaction) =>
      transaction.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      transaction.category.toLowerCase().includes(searchTerm.toLowerCase()) ||
      transaction.id.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-4 sm:flex-row">
        <div className="relative flex-1">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search transactions..."
            className="pl-8"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex gap-2">
          <Select>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="All Categories" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Categories</SelectItem>
              <SelectItem value="income">Income</SelectItem>
              <SelectItem value="transfer">Transfer</SelectItem>
              <SelectItem value="shopping">Shopping</SelectItem>
              <SelectItem value="utilities">Utilities</SelectItem>
              <SelectItem value="dining">Dining</SelectItem>
            </SelectContent>
          </Select>
          <Button variant="outline" size="icon">
            <Filter className="h-4 w-4" />
            <span className="sr-only">Filter</span>
          </Button>
          <Button variant="outline" size="icon">
            <Download className="h-4 w-4" />
            <span className="sr-only">Download</span>
          </Button>
        </div>
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Date</TableHead>
              <TableHead>Description</TableHead>
              <TableHead>Category</TableHead>
              <TableHead className="text-right">Amount</TableHead>
              <TableHead>Status</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredTransactions.length > 0 ? (
              filteredTransactions.map((transaction) => (
                <TableRow key={transaction.id}>
                  <TableCell className="font-medium">{transaction.date}</TableCell>
                  <TableCell>{transaction.description}</TableCell>
                  <TableCell>{transaction.category}</TableCell>
                  <TableCell className={cn("text-right font-medium", transaction.amount > 0 ? "text-green-500" : "")}>
                    {transaction.amount > 0 ? "+" : ""}${Math.abs(transaction.amount).toFixed(2)}
                  </TableCell>
                  <TableCell>
                    <Badge variant={transaction.status === "completed" ? "outline" : "secondary"}>
                      {transaction.status}
                    </Badge>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} className="h-24 text-center">
                  No transactions found.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  )
}
