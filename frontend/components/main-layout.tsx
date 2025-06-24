"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { MainNav } from "@/components/main-nav"
import { MobileNav } from "@/components/mobile-nav"
import { ThemeToggle } from "@/components/theme-toggle"
import { usePathname } from "next/navigation"

export function MainLayout({ children }: { children: React.ReactNode }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [userRole, setUserRole] = useState<string | null>(null) // Default to null or a specific role
  const pathname = usePathname()

  useEffect(() => {
    // In a real app, you would check for authentication status here
    // and decode the JWT to get the user role.
    // For demo purposes, we'll consider the user logged in except on login/register pages
    const loggedIn = !["/login", "/register"].includes(pathname)
    setIsLoggedIn(loggedIn)

    // Simulate fetching user role from a token or API
    if (loggedIn) {
      // Replace with actual logic to get user role
      const role = "admin" // Or "user", etc.  Could also be null if no role.
      setUserRole(role)
    } else {
      setUserRole(null)
    }
  }, [pathname])

  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-40 border-b bg-background">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-6 md:gap-10">
            <MainNav isLoggedIn={isLoggedIn} userRole={userRole} />
          </div>
          <div className="flex items-center gap-4">
            <ThemeToggle />
            <MobileNav isLoggedIn={isLoggedIn} userRole={userRole} />
          </div>
        </div>
      </header>
      <main className="flex-1">{children}</main>
      <footer className="border-t py-6">
        <div className="container flex flex-col items-center justify-between gap-4 md:flex-row">
          <p className="text-center text-sm text-muted-foreground md:text-left">
            &copy; {new Date().getFullYear()} Modern Bank. All rights reserved.
          </p>
          <div className="flex items-center gap-4">
            <a href="#" className="text-sm text-muted-foreground hover:underline">
              Terms
            </a>
            <a href="#" className="text-sm text-muted-foreground hover:underline">
              Privacy
            </a>
            <a href="#" className="text-sm text-muted-foreground hover:underline">
              Contact
            </a>
          </div>
        </div>
      </footer>
    </div>
  )
}
