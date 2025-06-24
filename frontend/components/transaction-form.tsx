"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { AlertCircle, CheckCircle2, Search } from "lucide-react"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { apiService } from "@/services/api"

export function TransactionForm() {
  const router = useRouter()
  const [isLoading, setIsLoading] = useState(false)
  const [status, setStatus] = useState<"idle" | "success" | "error">("idle")
  const [message, setMessage] = useState("")
  const [accounts, setAccounts] = useState([])
  const [searchResults, setSearchResults] = useState([])

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const data = await apiService.getCustomerAccounts()
        setAccounts(data)
      } catch (error) {
        console.error("Failed to fetch accounts:", error)
      }
    }

    fetchAccounts()
  }, [])

  async function searchCustomer(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const formData = new FormData(event.currentTarget)
    const firstName = formData.get("searchFirstName") as string
    const lastName = formData.get("searchLastName") as string

    try {
      const results = await apiService.searchCustomerByName(firstName, lastName)
      setSearchResults(results)
    } catch (error) {
      console.error("Failed to search customers:", error)
    }
  }

  async function onSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsLoading(true)
    setStatus("idle")
    setMessage("")

    const formData = new FormData(event.currentTarget)
    const transferData = {
      fromAccountId: formData.get("fromAccount") as string,
      toIban: formData.get("toIban") as string,
      amount: Number.parseFloat(formData.get("amount") as string),
      description: formData.get("description") as string,
    }

    try {
      await apiService.transferFunds(transferData)
      setStatus("success")
      setMessage("Transfer completed successfully!")

      setTimeout(() => {
        router.push("/transactions/history")
      }, 2000)
    } catch (error) {
      setStatus("error")
      setMessage("Transfer failed. Check your limits and account balance.")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Customer Search */}
      <Card>
        <CardHeader>
          <CardTitle>Find Customer IBAN</CardTitle>
          <CardDescription>Search for another customer's IBAN by their name</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={searchCustomer} className="space-y-4">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="searchFirstName">First Name</Label>
                <Input id="searchFirstName" name="searchFirstName" placeholder="John" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="searchLastName">Last Name</Label>
                <Input id="searchLastName" name="searchLastName" placeholder="Doe" />
              </div>
            </div>
            <Button type="submit">
              <Search className="mr-2 h-4 w-4" />
              Search Customer
            </Button>
          </form>

          {searchResults.length > 0 && (
            <div className="mt-4 space-y-2">
              <Label>Search Results:</Label>
              {searchResults.map((customer: any) => (
                <div key={customer.id} className="rounded border p-2 text-sm">
                  <strong>
                    {customer.firstName} {customer.lastName}
                  </strong>
                  <br />
                  Checking IBAN: {customer.checkingIban}
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Transfer Form */}
      <form onSubmit={onSubmit} className="space-y-6">
        {status === "success" && (
          <Alert>
            <CheckCircle2 className="h-4 w-4" />
            <AlertTitle>Success!</AlertTitle>
            <AlertDescription>{message}</AlertDescription>
          </Alert>
        )}

        {status === "error" && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertTitle>Error</AlertTitle>
            <AlertDescription>{message}</AlertDescription>
          </Alert>
        )}

        <div className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="fromAccount">From Account</Label>
            <Select name="fromAccount" disabled={isLoading} required>
              <SelectTrigger>
                <SelectValue placeholder="Select your account" />
              </SelectTrigger>
              <SelectContent>
                {accounts.map((account: any) => (
                  <SelectItem key={account.id} value={account.id}>
                    {account.type} - {account.iban} (€{account.balance.toFixed(2)})
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="toIban">Recipient IBAN</Label>
            <Input id="toIban" name="toIban" placeholder="NL91ABNA0417164300" disabled={isLoading} required />
          </div>

          <div className="space-y-2">
            <Label htmlFor="amount">Amount (€)</Label>
            <Input
              id="amount"
              name="amount"
              type="number"
              step="0.01"
              min="0.01"
              placeholder="0.00"
              disabled={isLoading}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Description (Optional)</Label>
            <Textarea
              id="description"
              name="description"
              placeholder="Add a note about this transfer"
              disabled={isLoading}
            />
          </div>
        </div>

        <div className="flex justify-end">
          <Button type="submit" disabled={isLoading}>
            {isLoading ? "Processing..." : "Complete Transfer"}
          </Button>
        </div>
      </form>
    </div>
  )
}
