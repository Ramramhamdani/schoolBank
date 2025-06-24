"use client"

import { useState, useEffect } from "react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Search, Download } from "lucide-react"
import { apiService } from "@/services/api"

export function AllTransactions() {
  const [transactions, setTransactions] = useState([])
  const [filteredTransactions, setFilteredTransactions] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState("")

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const data = await apiService.getAllTransactions()
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

  useEffect(() => {
    const filtered = transactions.filter(
      (transaction: any) =>
        transaction.fromAccount.toLowerCase().includes(searchTerm.toLowerCase()) ||
        transaction.toAccount.toLowerCase().includes(searchTerm.toLowerCase()) ||
        transaction.initiatedBy.toLowerCase().includes(searchTerm.toLowerCase()) ||
        transaction.description?.toLowerCase().includes(searchTerm.toLowerCase()),
    )
    setFilteredTransactions(filtered)
  }, [searchTerm, transactions])

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>Transaction Search</CardTitle>
          <CardDescription>Search all system transactions</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Search by IBAN, user, or description..."
                className="pl-8"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <Button variant="outline">
              <Download className="mr-2 h-4 w-4" />
              Export
            </Button>
          </div>
        </CardContent>
      </Card>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Timestamp</TableHead>
              <TableHead>From Account</TableHead>
              <TableHead>To Account</TableHead>
              <TableHead>Amount</TableHead>
              <TableHead>Type</TableHead>
              <TableHead>Initiated By</TableHead>
              <TableHead>Description</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredTransactions.length > 0 ? (
              filteredTransactions.map((transaction: any) => (
                <TableRow key={transaction.id}>
                  <TableCell className="font-medium">{new Date(transaction.timestamp).toLocaleString()}</TableCell>
                  <TableCell className="font-mono text-sm">{transaction.fromAccount}</TableCell>
                  <TableCell className="font-mono text-sm">{transaction.toAccount}</TableCell>
                  <TableCell className="font-semibold">€{transaction.amount.toFixed(2)}</TableCell>
                  <TableCell>
                    <Badge variant="outline">{transaction.type}</Badge>
                  </TableCell>
                  <TableCell>
                    <Badge variant={transaction.initiatedBy.includes("@") ? "default" : "secondary"}>
                      {transaction.initiatedBy.includes("@") ? "Customer" : "Employee"}
                    </Badge>
                  </TableCell>
                  <TableCell>{transaction.description || "-"}</TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={7} className="h-24 text-center">
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
