"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { LayoutDashboard, Receipt, History, User, LogOut, Users, Clock, CreditCard } from "lucide-react"

interface MainNavProps {
  isLoggedIn: boolean
  userRole?: string
}

export function MainNav({ isLoggedIn, userRole }: MainNavProps) {
  const pathname = usePathname()

  const customerRoutes = [
    {
      href: "/",
      label: "Dashboard",
      icon: <LayoutDashboard className="mr-2 h-4 w-4" />,
      active: pathname === "/",
    },
    {
      href: "/transactions",
      label: "New Transfer",
      icon: <Receipt className="mr-2 h-4 w-4" />,
      active: pathname === "/transactions",
    },
    {
      href: "/transactions/history",
      label: "History",
      icon: <History className="mr-2 h-4 w-4" />,
      active: pathname === "/transactions/history",
    },
    {
      href: "/profile",
      label: "Profile",
      icon: <User className="mr-2 h-4 w-4" />,
      active: pathname === "/profile",
    },
  ]

  const employeeRoutes = [
    {
      href: "/",
      label: "Dashboard",
      icon: <LayoutDashboard className="mr-2 h-4 w-4" />,
      active: pathname === "/",
    },
    {
      href: "/employee/customers",
      label: "Customers",
      icon: <Users className="mr-2 h-4 w-4" />,
      active: pathname === "/employee/customers",
    },
    {
      href: "/employee/pending-approvals",
      label: "Approvals",
      icon: <Clock className="mr-2 h-4 w-4" />,
      active: pathname === "/employee/pending-approvals",
    },
    {
      href: "/employee/transactions",
      label: "All Transactions",
      icon: <CreditCard className="mr-2 h-4 w-4" />,
      active: pathname === "/employee/transactions",
    },
  ]

  if (!isLoggedIn) {
    return (
      <Link href="/" className="flex items-center space-x-2">
        <span className="font-bold">Modern Bank</span>
      </Link>
    )
  }

  const routes = userRole === "employee" ? employeeRoutes : customerRoutes

  return (
    <div className="flex gap-6 md:gap-10">
      <Link href="/" className="hidden items-center space-x-2 md:flex">
        <span className="font-bold">Modern Bank</span>
      </Link>
      <nav className="hidden gap-6 md:flex">
        {routes.map((route) => (
          <Link
            key={route.href}
            href={route.href}
            className={cn(
              "flex items-center text-sm font-medium transition-colors hover:text-primary",
              route.active ? "text-primary" : "text-muted-foreground",
            )}
          >
            {route.label}
          </Link>
        ))}
        <div className="flex items-center gap-4">
          <Link href="/atm" className="text-sm font-medium text-muted-foreground hover:text-primary">
            ATM
          </Link>
          <Button variant="ghost" size="sm" className="text-sm font-medium">
            <LogOut className="mr-2 h-4 w-4" />
            Logout
          </Button>
        </div>
      </nav>
    </div>
  )
}
