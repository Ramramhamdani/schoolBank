"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { ArrowUpRight, ArrowDownLeft, CreditCard, Wallet, TrendingUp } from "lucide-react"
import { AccountSummary } from "@/components/account-summary"
import { RecentTransactions } from "@/components/recent-transactions"
import { SpendingChart } from "@/components/spending-chart"

export function DashboardPage() {
  // Mock data - in a real app, this would come from your API
  const [accounts] = useState([
    {
      id: "1",
      name: "Main Account",
      balance: 5750.85,
      accountNumber: "****4567",
      type: "Checking",
    },
    {
      id: "2",
      name: "Savings Account",
      balance: 12340.42,
      accountNumber: "****7890",
      type: "Savings",
    },
    {
      id: "3",
      name: "Investment Account",
      balance: 34567.1,
      accountNumber: "****2345",
      type: "Investment",
    },
  ])

  return (
    <div className="container mx-auto py-6">
      <h1 className="mb-6 text-3xl font-bold">Welcome back, John</h1>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Balance</CardTitle>
            <Wallet className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              $
              {accounts
                .reduce((sum, account) => sum + account.balance, 0)
                .toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </div>
            <p className="text-xs text-muted-foreground">+2.5% from last month</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Income</CardTitle>
            <ArrowDownLeft className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$4,350.00</div>
            <p className="text-xs text-muted-foreground">+5.2% from last month</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Expenses</CardTitle>
            <ArrowUpRight className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$2,150.00</div>
            <p className="text-xs text-muted-foreground">-3.1% from last month</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Investments</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$34,567.10</div>
            <p className="text-xs text-muted-foreground">+12.3% from last month</p>
          </CardContent>
        </Card>
      </div>

      <div className="mt-6 grid gap-6 md:grid-cols-2 lg:grid-cols-7">
        <Tabs defaultValue="accounts" className="col-span-4">
          <TabsList>
            <TabsTrigger value="accounts">Accounts</TabsTrigger>
            <TabsTrigger value="cards">Cards</TabsTrigger>
          </TabsList>
          <TabsContent value="accounts" className="space-y-4">
            <AccountSummary accounts={accounts} />
          </TabsContent>
          <TabsContent value="cards" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Your Cards</CardTitle>
                <CardDescription>Manage your payment cards</CardDescription>
              </CardHeader>
              <CardContent className="grid gap-4">
                <div className="rounded-xl bg-gradient-to-r from-violet-500 to-purple-500 p-6 text-white">
                  <div className="flex flex-col gap-4">
                    <div className="flex items-center justify-between">
                      <p className="text-sm opacity-80">Modern Bank</p>
                      <CreditCard className="h-6 w-6" />
                    </div>
                    <p className="text-xl font-medium">**** **** **** 4567</p>
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-xs opacity-80">Card Holder</p>
                        <p className="text-sm font-medium">John Doe</p>
                      </div>
                      <div>
                        <p className="text-xs opacity-80">Expires</p>
                        <p className="text-sm font-medium">12/25</p>
                      </div>
                    </div>
                  </div>
                </div>
                <Button variant="outline" className="w-full">
                  <CreditCard className="mr-2 h-4 w-4" />
                  Add New Card
                </Button>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
        <Card className="col-span-3">
          <CardHeader>
            <CardTitle>Recent Transactions</CardTitle>
            <CardDescription>Your recent financial activity</CardDescription>
          </CardHeader>
          <CardContent>
            <RecentTransactions />
          </CardContent>
          <CardFooter>
            <Button variant="outline" className="w-full" asChild>
              <a href="/transactions/history">View All Transactions</a>
            </Button>
          </CardFooter>
        </Card>
      </div>

      <div className="mt-6">
        <Card>
          <CardHeader>
            <CardTitle>Spending Analysis</CardTitle>
            <CardDescription>Your spending patterns for the last 30 days</CardDescription>
          </CardHeader>
          <CardContent>
            <SpendingChart />
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
