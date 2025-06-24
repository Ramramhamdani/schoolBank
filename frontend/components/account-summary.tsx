import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ArrowRight } from "lucide-react"
import Link from "next/link"

interface Account {
  id: string
  name: string
  balance: number
  accountNumber: string
  type: string
}

interface AccountSummaryProps {
  accounts: Account[]
}

export function AccountSummary({ accounts }: AccountSummaryProps) {
  return (
    <div className="space-y-4">
      {accounts.map((account) => (
        <Card key={account.id}>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium">{account.name}</p>
                <p className="text-sm text-muted-foreground">
                  {account.type} • {account.accountNumber}
                </p>
              </div>
              <p className="text-xl font-bold">
                ${account.balance.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </p>
            </div>
          </CardContent>
          <CardFooter className="border-t bg-muted/50 px-6 py-3">
            <div className="flex w-full items-center justify-between">
              <p className="text-xs text-muted-foreground">Available balance</p>
              <Button variant="ghost" size="sm" asChild>
                <Link href="/transactions">
                  <span className="mr-2">Transfer</span>
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </Button>
            </div>
          </CardFooter>
        </Card>
      ))}
    </div>
  )
}
