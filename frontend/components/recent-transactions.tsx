import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { cn } from "@/lib/utils"

export function RecentTransactions() {
  // Mock data - in a real app, this would come from your API
  const transactions = [
    {
      id: "1",
      name: "Amazon",
      description: "Online Shopping",
      amount: -79.99,
      date: "Today",
      icon: "A",
    },
    {
      id: "2",
      name: "Salary",
      description: "Payroll",
      amount: 2500.0,
      date: "Yesterday",
      icon: "S",
    },
    {
      id: "3",
      name: "Starbucks",
      description: "Coffee Shop",
      amount: -5.25,
      date: "Yesterday",
      icon: "S",
    },
    {
      id: "4",
      name: "Transfer to Savings",
      description: "Internal Transfer",
      amount: -500.0,
      date: "Jun 1, 2025",
      icon: "T",
    },
    {
      id: "5",
      name: "Netflix",
      description: "Subscription",
      amount: -14.99,
      date: "May 31, 2025",
      icon: "N",
    },
  ]

  return (
    <div className="space-y-4">
      {transactions.map((transaction) => (
        <div key={transaction.id} className="flex items-center gap-4">
          <Avatar className="h-9 w-9">
            <AvatarImage src={`/placeholder.svg?text=${transaction.icon}`} alt={transaction.name} />
            <AvatarFallback>{transaction.icon}</AvatarFallback>
          </Avatar>
          <div className="flex-1">
            <p className="text-sm font-medium leading-none">{transaction.name}</p>
            <p className="text-sm text-muted-foreground">{transaction.description}</p>
          </div>
          <div className="text-right">
            <p className={cn("text-sm font-medium", transaction.amount > 0 ? "text-green-500" : "")}>
              {transaction.amount > 0 ? "+" : ""}${Math.abs(transaction.amount).toFixed(2)}
            </p>
            <p className="text-xs text-muted-foreground">{transaction.date}</p>
          </div>
        </div>
      ))}
    </div>
  )
}
