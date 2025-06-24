"use client"

import type React from "react"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { AlertCircle, CheckCircle2, CreditCard } from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { apiService } from "@/services/api"

export function ATMInterface() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [status, setStatus] = useState<"idle" | "success" | "error">("idle")
  const [message, setMessage] = useState("")
  const [userAccount, setUserAccount] = useState<any>(null)

  async function handleLogin(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsLoading(true)
    setStatus("idle")

    const formData = new FormData(event.currentTarget)
    const email = formData.get("email") as string
    const password = formData.get("password") as string

    try {
      const response = await apiService.atmLogin(email, password)
      setUserAccount(response.account)
      setIsLoggedIn(true)
      setStatus("success")
      setMessage("Successfully logged in to ATM")
    } catch (error) {
      setStatus("error")
      setMessage("Invalid credentials")
    } finally {
      setIsLoading(false)
    }
  }

  async function handleWithdraw(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsLoading(true)
    setStatus("idle")

    const formData = new FormData(event.currentTarget)
    const amount = Number.parseFloat(formData.get("amount") as string)

    try {
      await apiService.atmWithdraw(amount, userAccount.id)
      setStatus("success")
      setMessage(`Successfully withdrew €${amount.toFixed(2)}`)
      // Update account balance
      setUserAccount((prev: any) => ({ ...prev, balance: prev.balance - amount }))
    } catch (error) {
      setStatus("error")
      setMessage("Withdrawal failed. Check your limits and balance.")
    } finally {
      setIsLoading(false)
    }
  }

  async function handleDeposit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsLoading(true)
    setStatus("idle")

    const formData = new FormData(event.currentTarget)
    const amount = Number.parseFloat(formData.get("amount") as string)

    try {
      await apiService.atmDeposit(amount, userAccount.id)
      setStatus("success")
      setMessage(`Successfully deposited €${amount.toFixed(2)}`)
      // Update account balance
      setUserAccount((prev: any) => ({ ...prev, balance: prev.balance + amount }))
    } catch (error) {
      setStatus("error")
      setMessage("Deposit failed. Please try again.")
    } finally {
      setIsLoading(false)
    }
  }

  if (!isLoggedIn) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-blue-100 dark:bg-blue-900">
              <CreditCard className="h-6 w-6 text-blue-600 dark:text-blue-400" />
            </div>
            <CardTitle className="text-2xl font-bold">ATM Login</CardTitle>
            <CardDescription>Enter your credentials to access ATM services</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleLogin} className="space-y-4">
              {status === "error" && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>{message}</AlertDescription>
                </Alert>
              )}

              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input id="email" name="email" type="email" required disabled={isLoading} />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <Input id="password" name="password" type="password" required disabled={isLoading} />
              </div>

              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "Logging in..." : "Login to ATM"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl font-bold">ATM Services</CardTitle>
          <CardDescription>Account Balance: €{userAccount?.balance?.toFixed(2) || "0.00"}</CardDescription>
        </CardHeader>
        <CardContent>
          {status !== "idle" && (
            <Alert variant={status === "success" ? "default" : "destructive"} className="mb-4">
              {status === "success" ? <CheckCircle2 className="h-4 w-4" /> : <AlertCircle className="h-4 w-4" />}
              <AlertDescription>{message}</AlertDescription>
            </Alert>
          )}

          <Tabs defaultValue="withdraw" className="w-full">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="withdraw">Withdraw</TabsTrigger>
              <TabsTrigger value="deposit">Deposit</TabsTrigger>
            </TabsList>

            <TabsContent value="withdraw">
              <form onSubmit={handleWithdraw} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="withdraw-amount">Amount (€)</Label>
                  <Input
                    id="withdraw-amount"
                    name="amount"
                    type="number"
                    step="0.01"
                    min="0.01"
                    max={userAccount?.balance}
                    required
                    disabled={isLoading}
                  />
                </div>
                <Button type="submit" className="w-full" disabled={isLoading}>
                  {isLoading ? "Processing..." : "Withdraw"}
                </Button>
              </form>
            </TabsContent>

            <TabsContent value="deposit">
              <form onSubmit={handleDeposit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="deposit-amount">Amount (€)</Label>
                  <Input
                    id="deposit-amount"
                    name="amount"
                    type="number"
                    step="0.01"
                    min="0.01"
                    required
                    disabled={isLoading}
                  />
                </div>
                <Button type="submit" className="w-full" disabled={isLoading}>
                  {isLoading ? "Processing..." : "Deposit"}
                </Button>
              </form>
            </TabsContent>
          </Tabs>

          <Button variant="outline" className="mt-4 w-full" onClick={() => setIsLoggedIn(false)}>
            Logout
          </Button>
        </CardContent>
      </Card>
    </div>
  )
}
