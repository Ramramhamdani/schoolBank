"use client"

import { useState } from "react"
import Link from "next/link"
import { usePathname } from "next/navigation"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet"
import { LayoutDashboard, Receipt, History, User, LogOut, Menu, Building } from "lucide-react"

interface MobileNavProps {
  isLoggedIn: boolean
  userRole: string | null
}

export function MobileNav({ isLoggedIn, userRole }: MobileNavProps) {
  const pathname = usePathname()
  const [open, setOpen] = useState(false)

  const routes = [
    {
      href: "/",
      label: "Dashboard",
      icon: <LayoutDashboard className="mr-2 h-4 w-4" />,
      active: pathname === "/",
    },
    {
      href: "/transactions",
      label: "New Transaction",
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
      icon: <User className="mr-2 h-4 w-4" />,
      active: pathname === "/employee/customers",
    },
    {
      href: "/employee/atms",
      label: "ATMs",
      icon: <Building className="mr-2 h-4 w-4" />,
      active: pathname === "/employee/atms",
    },
    {
      href: "/profile",
      label: "Profile",
      icon: <User className="mr-2 h-4 w-4" />,
      active: pathname === "/profile",
    },
  ]

  if (!isLoggedIn) {
    return null
  }

  const currentRoutes = userRole === "employee" ? employeeRoutes : routes

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button variant="ghost" size="icon" className="md:hidden">
          <Menu className="h-5 w-5" />
          <span className="sr-only">Toggle menu</span>
        </Button>
      </SheetTrigger>
      <SheetContent side="right">
        <div className="flex flex-col gap-6 py-6">
          <Link href="/" className="flex items-center gap-2" onClick={() => setOpen(false)}>
            <span className="font-bold">Modern Bank</span>
          </Link>
          <nav className="flex flex-col gap-4">
            {currentRoutes.map((route) => (
              <Link
                key={route.href}
                href={route.href}
                onClick={() => setOpen(false)}
                className={cn(
                  "flex items-center text-sm font-medium transition-colors hover:text-primary",
                  route.active ? "text-primary" : "text-muted-foreground",
                )}
              >
                {route.icon}
                {route.label}
              </Link>
            ))}
            {userRole !== "employee" && (
              <Link
                href="/atms"
                onClick={() => setOpen(false)}
                className={cn(
                  "flex items-center text-sm font-medium transition-colors hover:text-primary",
                  pathname === "/atms" ? "text-primary" : "text-muted-foreground",
                )}
              >
                <Building className="mr-2 h-4 w-4" />
                ATMs
              </Link>
            )}
            <Button variant="ghost" size="sm" className="justify-start">
              <LogOut className="mr-2 h-4 w-4" />
              Logout
            </Button>
          </nav>
        </div>
      </SheetContent>
    </Sheet>
  )
}
