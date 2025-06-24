"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Eye, Settings, X } from "lucide-react"
import { apiService } from "@/services/api"

export function CustomerManagement() {
  const [customers, setCustomers] = useState([])
  const [selectedCustomer, setSelectedCustomer] = useState<any>(null)
  const [customerTransactions, setCustomerTransactions] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchCustomers = async () => {
      try {
        const data = await apiService.getAllCustomers()
        setCustomers(data)
      } catch (error) {
        console.error("Failed to fetch customers:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchCustomers()
  }, [])

  const viewCustomerTransactions = async (customerId: string) => {
    try {
      const transactions = await apiService.getCustomerTransactionsById(customerId)
      setCustomerTransactions(transactions)
    } catch (error) {
      console.error("Failed to fetch customer transactions:", error)
    }
  }

  const updateAccountLimits = async (accountId: string, limits: any) => {
    try {
      await apiService.updateAccountLimits(accountId, limits)
      // Refresh customer data
      const data = await apiService.getAllCustomers()
      setCustomers(data)
    } catch (error) {
      console.error("Failed to update account limits:", error)
    }
  }

  const closeAccount = async (accountId: string) => {
    try {
      await apiService.closeAccount(accountId)
      // Refresh customer data
      const data = await apiService.getAllCustomers()
      setCustomers(data)
    } catch (error) {
      console.error("Failed to close account:", error)
    }
  }

  return (
    <div className="space-y-4">
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Accounts</TableHead>
              <TableHead>Total Balance</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {customers.length > 0 ? (
              customers.map((customer: any) => (
                <TableRow key={customer.id}>
                  <TableCell className="font-medium">
                    {customer.firstName} {customer.lastName}
                  </TableCell>
                  <TableCell>{customer.email}</TableCell>
                  <TableCell>{customer.accounts?.length || 0}</TableCell>
                  <TableCell>
                    €{customer.accounts?.reduce((sum: number, acc: any) => sum + acc.balance, 0).toFixed(2) || "0.00"}
                  </TableCell>
                  <TableCell>
                    <Badge variant={customer.approved ? "default" : "secondary"}>
                      {customer.approved ? "Active" : "Pending"}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => {
                              setSelectedCustomer(customer)
                              viewCustomerTransactions(customer.id)
                            }}
                          >
                            <Eye className="mr-1 h-4 w-4" />
                            View
                          </Button>
                        </DialogTrigger>
                        <DialogContent className="max-w-4xl">
                          <DialogHeader>
                            <DialogTitle>
                              {customer.firstName} {customer.lastName} - Details
                            </DialogTitle>
                            <DialogDescription>Customer information and account management</DialogDescription>
                          </DialogHeader>

                          <Tabs defaultValue="accounts" className="w-full">
                            <TabsList>
                              <TabsTrigger value="accounts">Accounts</TabsTrigger>
                              <TabsTrigger value="transactions">Transactions</TabsTrigger>
                              <TabsTrigger value="transfer">Employee Transfer</TabsTrigger>
                            </TabsList>

                            <TabsContent value="accounts" className="space-y-4">
                              {customer.accounts?.map((account: any) => (
                                <Card key={account.id}>
                                  <CardHeader>
                                    <CardTitle className="text-lg">{account.type} Account</CardTitle>
                                    <CardDescription>IBAN: {account.iban}</CardDescription>
                                  </CardHeader>
                                  <CardContent>
                                    <div className="grid grid-cols-2 gap-4">
                                      <div>
                                        <Label>Balance</Label>
                                        <p className="text-lg font-semibold">€{account.balance.toFixed(2)}</p>
                                      </div>
                                      <div>
                                        <Label>Daily Limit</Label>
                                        <p>€{account.dailyLimit}</p>
                                      </div>
                                      <div>
                                        <Label>Absolute Limit</Label>
                                        <p>€{account.absoluteLimit}</p>
                                      </div>
                                      <div>
                                        <Label>Status</Label>
                                        <Badge variant={account.active ? "default" : "destructive"}>
                                          {account.active ? "Active" : "Closed"}
                                        </Badge>
                                      </div>
                                    </div>
                                    <div className="mt-4 flex gap-2">
                                      <Button size="sm" variant="outline">
                                        <Settings className="mr-1 h-4 w-4" />
                                        Update Limits
                                      </Button>
                                      {account.active && (
                                        <Button
                                          size="sm"
                                          variant="destructive"
                                          onClick={() => closeAccount(account.id)}
                                        >
                                          <X className="mr-1 h-4 w-4" />
                                          Close Account
                                        </Button>
                                      )}
                                    </div>
                                  </CardContent>
                                </Card>
                              ))}
                            </TabsContent>

                            <TabsContent value="transactions" className="space-y-4">
                              <div className="rounded-md border">
                                <Table>
                                  <TableHeader>
                                    <TableRow>
                                      <TableHead>Date</TableHead>
                                      <TableHead>From/To</TableHead>
                                      <TableHead>Amount</TableHead>
                                      <TableHead>Type</TableHead>
                                    </TableRow>
                                  </TableHeader>
                                  <TableBody>
                                    {customerTransactions.length > 0 ? (
                                      customerTransactions.map((transaction: any) => (
                                        <TableRow key={transaction.id}>
                                          <TableCell>{new Date(transaction.timestamp).toLocaleDateString()}</TableCell>
                                          <TableCell>
                                            <div className="text-sm">
                                              <div>From: {transaction.fromAccount}</div>
                                              <div>To: {transaction.toAccount}</div>
                                            </div>
                                          </TableCell>
                                          <TableCell>€{transaction.amount.toFixed(2)}</TableCell>
                                          <TableCell>
                                            <Badge variant="outline">{transaction.type}</Badge>
                                          </TableCell>
                                        </TableRow>
                                      ))
                                    ) : (
                                      <TableRow>
                                        <TableCell colSpan={4} className="h-24 text-center">
                                          No transactions found.
                                        </TableCell>
                                      </TableRow>
                                    )}
                                  </TableBody>
                                </Table>
                              </div>
                            </TabsContent>

                            <TabsContent value="transfer" className="space-y-4">
                              <EmployeeTransferForm customerId={customer.id} />
                            </TabsContent>
                          </Tabs>
                        </DialogContent>
                      </Dialog>
                    </div>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={6} className="h-24 text-center">
                  No customers found.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  )
}

function EmployeeTransferForm({ customerId }: { customerId: string }) {
  const [isLoading, setIsLoading] = useState(false)
  const [status, setStatus] = useState<"idle" | "success" | "error">("idle")
  const [message, setMessage] = useState("")

  async function onSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsLoading(true)
    setStatus("idle")

    const formData = new FormData(event.currentTarget)
    const transferData = {
      fromIban: formData.get("fromIban") as string,
      toIban: formData.get("toIban") as string,
      amount: Number.parseFloat(formData.get("amount") as string),
      description: formData.get("description") as string,
    }

    try {
      await apiService.employeeTransfer(transferData)
      setStatus("success")
      setMessage("Transfer completed successfully!")
    } catch (error) {
      setStatus("error")
      setMessage("Transfer failed. Please check the details.")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Employee Transfer</CardTitle>
        <CardDescription>Transfer funds between customer accounts</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} className="space-y-4">
          {status !== "idle" && (
            <div
              className={`p-3 rounded ${status === "success" ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"}`}
            >
              {message}
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="fromIban">From IBAN</Label>
            <Input id="fromIban" name="fromIban" placeholder="NL91ABNA0417164300" required disabled={isLoading} />
          </div>

          <div className="space-y-2">
            <Label htmlFor="toIban">To IBAN</Label>
            <Input id="toIban" name="toIban" placeholder="NL91ABNA0417164300" required disabled={isLoading} />
          </div>

          <div className="space-y-2">
            <Label htmlFor="amount">Amount (€)</Label>
            <Input id="amount" name="amount" type="number" step="0.01" min="0.01" required disabled={isLoading} />
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Description</Label>
            <Input id="description" name="description" placeholder="Employee transfer" disabled={isLoading} />
          </div>

          <Button type="submit" disabled={isLoading}>
            {isLoading ? "Processing..." : "Execute Transfer"}
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}
