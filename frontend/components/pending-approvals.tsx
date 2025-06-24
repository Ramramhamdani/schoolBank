"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { CheckCircle, Clock } from "lucide-react"
import { apiService } from "@/services/api"

export function PendingApprovals() {
  const [pendingCustomers, setPendingCustomers] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [selectedCustomer, setSelectedCustomer] = useState<any>(null)
  const [limits, setLimits] = useState({
    absoluteLimit: "",
    dailyLimit: "",
  })

  useEffect(() => {
    const fetchPendingApprovals = async () => {
      try {
        const data = await apiService.getPendingApprovals()
        setPendingCustomers(data)
      } catch (error) {
        console.error("Failed to fetch pending approvals:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchPendingApprovals()
  }, [])

  const handleApprove = async () => {
    if (!selectedCustomer) return

    try {
      await apiService.approveCustomer(selectedCustomer.id, {
        absoluteLimit: Number.parseFloat(limits.absoluteLimit),
        dailyLimit: Number.parseFloat(limits.dailyLimit),
      })

      // Remove approved customer from list
      setPendingCustomers((prev) => prev.filter((customer: any) => customer.id !== selectedCustomer.id))
      setSelectedCustomer(null)
      setLimits({ absoluteLimit: "", dailyLimit: "" })
    } catch (error) {
      console.error("Failed to approve customer:", error)
    }
  }

  return (
    <div className="space-y-4">
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>BSN</TableHead>
              <TableHead>Phone</TableHead>
              <TableHead>Registration Date</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {pendingCustomers.length > 0 ? (
              pendingCustomers.map((customer: any) => (
                <TableRow key={customer.id}>
                  <TableCell className="font-medium">
                    {customer.firstName} {customer.lastName}
                  </TableCell>
                  <TableCell>{customer.email}</TableCell>
                  <TableCell>{customer.bsn}</TableCell>
                  <TableCell>{customer.phoneNumber}</TableCell>
                  <TableCell>{new Date(customer.registrationDate).toLocaleDateString()}</TableCell>
                  <TableCell>
                    <Badge variant="secondary">
                      <Clock className="mr-1 h-3 w-3" />
                      Pending
                    </Badge>
                  </TableCell>
                  <TableCell>
                    <Dialog>
                      <DialogTrigger asChild>
                        <Button size="sm" onClick={() => setSelectedCustomer(customer)}>
                          <CheckCircle className="mr-1 h-4 w-4" />
                          Approve
                        </Button>
                      </DialogTrigger>
                      <DialogContent>
                        <DialogHeader>
                          <DialogTitle>Approve Customer</DialogTitle>
                          <DialogDescription>
                            Set account limits for {customer.firstName} {customer.lastName}
                          </DialogDescription>
                        </DialogHeader>
                        <div className="space-y-4">
                          <div className="space-y-2">
                            <Label htmlFor="absoluteLimit">Absolute Transfer Limit (€)</Label>
                            <Input
                              id="absoluteLimit"
                              type="number"
                              step="0.01"
                              value={limits.absoluteLimit}
                              onChange={(e) => setLimits((prev) => ({ ...prev, absoluteLimit: e.target.value }))}
                              placeholder="e.g., -1000.00"
                            />
                            <p className="text-xs text-muted-foreground">Minimum account balance (negative value)</p>
                          </div>
                          <div className="space-y-2">
                            <Label htmlFor="dailyLimit">Daily Transfer Limit (€)</Label>
                            <Input
                              id="dailyLimit"
                              type="number"
                              step="0.01"
                              value={limits.dailyLimit}
                              onChange={(e) => setLimits((prev) => ({ ...prev, dailyLimit: e.target.value }))}
                              placeholder="e.g., 5000.00"
                            />
                            <p className="text-xs text-muted-foreground">Maximum daily transfer amount</p>
                          </div>
                        </div>
                        <DialogFooter>
                          <Button onClick={handleApprove}>Approve & Create Accounts</Button>
                        </DialogFooter>
                      </DialogContent>
                    </Dialog>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={7} className="h-24 text-center">
                  No pending approvals.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  )
}
