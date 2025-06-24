import { redirect } from "next/navigation"
import { CustomerDashboard } from "@/components/customer-dashboard"
import { EmployeeDashboard } from "@/components/employee-dashboard"
import { WelcomePage } from "@/components/welcome-page"

export default function Home() {
  // In a real application, you would get this from JWT token or API call
  const user = {
    isAuthenticated: true,
    role: "customer", // "customer" | "employee"
    hasApprovedAccount: true, // Only for customers
    email: "john.doe@example.com",
    firstName: "John",
    lastName: "Doe",
  }

  // if (!user.isAuthenticated) {
  //   redirect("/login")
  // }

  if (user.role === "customer" && !user.hasApprovedAccount) {
    return <WelcomePage user={user} />
  }

  if (user.role === "employee") {
    return <EmployeeDashboard />
  }

  return <CustomerDashboard />
}
