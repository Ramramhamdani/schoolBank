import { PendingApprovals } from "@/components/pending-approvals"

export default function PendingApprovalsPage() {
  return (
    <div className="container mx-auto py-6">
      <h1 className="mb-6 text-3xl font-bold">Pending Customer Approvals</h1>
      <PendingApprovals />
    </div>
  )
}
