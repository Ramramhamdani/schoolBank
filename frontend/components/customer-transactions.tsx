"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Filter } from "lucide-react"
import { cn } from "@/lib/utils"
import { apiService } from "@/services/api"

export function CustomerTransactions() {
  const [transactions, setTransactions] = useState([])
  const [filteredTransactions, setFilteredTransactions] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [filters, setFilters] = useState({
    startDate: "",
    endDate: "",
    minAmount: "",
    maxAmount: "",
    iban: "",
    searchTerm: "",
  })

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const data = await apiService.getCustomerTransactions()
        setTransactions(data)
        setFilteredTransactions(data)
      } catch (error) {
        console.error("Failed to fetch transactions:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchTransactions()
  }, [])

  const applyFilters = async () => {
    try {
      const data = await apiService.filterTransactions(filters)
      setFilteredTransactions(data)
    } catch (error) {
      console.error("Failed to filter transactions:", error)
    }
  }

  const handleFilterChange = (key: string, value: string) => {
    setFilters((prev) => ({ ...prev, [key]: value }))
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>Transaction Filters</CardTitle>
          <CardDescription>Filter transactions by date, amount, or IBAN</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            <div className="space-y-2">
              <Label htmlFor="start-date">Start Date</Label>
              <Input
                id="start-date"
                type="date"
                value={filters.startDate}
                onChange={(e) => handleFilterChange("startDate", e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="end-date">End Date</Label>
              <Input
                id="end-date"
                type="date"
                value={filters.endDate}
                onChange={(e) => handleFilterChange("endDate", e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="iban">IBAN</Label>
              <Input
                id="iban"
                placeholder="NL91ABNA0417164300"
                value={filters.iban}
                onChange={(e) => handleFilterChange("iban", e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="min-amount">Min Amount (€)</Label>
              <Input
                id="min-amount"
                type="number"
                step="0.01"
                value={filters.minAmount}
                onChange={(e) => handleFilterChange("minAmount", e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="max-amount">Max Amount (€)</Label>
              <Input
                id="max-amount"
                type="number"
                step="0.01"
                value={filters.maxAmount}
                onChange={(e) => handleFilterChange("maxAmount", e.target.value)}
              />
            </div>
            <div className="flex items-end">
              <Button onClick={applyFilters} className="w-full">
                <Filter className="mr-2 h-4 w-4" />
                Apply Filters
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Date</TableHead>
              <TableHead>From/To</TableHead>
              <TableHead>Description</TableHead>
              <TableHead className="text-right">Amount</TableHead>
              <TableHead>Type</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredTransactions.length > 0 ? (
              filteredTransactions.map((transaction: any) => (
                <TableRow key={transaction.id}>
                  <TableCell className="font-medium">{new Date(transaction.timestamp).toLocaleDateString()}</TableCell>
                  <TableCell>
                    <div className="text-sm">
                      <div>From: {transaction.fromAccount}</div>
                      <div>To: {transaction.toAccount}</div>
                    </div>
                  </TableCell>
                  <TableCell>{transaction.description || "Transfer"}</TableCell>
                  <TableCell
                    className={cn("text-right font-medium", transaction.amount > 0 ? "text-green-500" : "text-red-500")}
                  >
                    €{Math.abs(transaction.amount).toFixed(2)}
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{transaction.type}</Badge>
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
