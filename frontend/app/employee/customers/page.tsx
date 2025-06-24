import { CustomerManagement } from "@/components/customer-management"

export default function CustomerManagementPage() {
  return (
    <div className="container mx-auto py-6">
      <h1 className="mb-6 text-3xl font-bold">Customer Management</h1>
      <CustomerManagement />
    </div>
  )
}
