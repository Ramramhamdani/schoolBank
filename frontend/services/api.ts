// API service layer for REST calls to Spring Boot backend

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api"

class ApiService {
  private getAuthHeaders() {
    const token = localStorage.getItem("jwt_token")
    return {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
    }
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`
    const config = {
      headers: this.getAuthHeaders(),
      ...options,
    }

    const response = await fetch(url, config)

    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`)
    }

    return response.json()
  }

  // Authentication
  async login(email: string, password: string) {
    return this.request("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    })
  }

  async register(customerData: any) {
    return this.request("/auth/register", {
      method: "POST",
      body: JSON.stringify(customerData),
    })
  }

  // Customer endpoints
  async getCustomerAccounts() {
    return this.request("/customer/accounts")
  }

  async getCustomerTransactions(accountId?: string) {
    const endpoint = accountId ? `/customer/transactions?accountId=${accountId}` : "/customer/transactions"
    return this.request(endpoint)
  }

  async transferFunds(transferData: any) {
    return this.request("/customer/transfer", {
      method: "POST",
      body: JSON.stringify(transferData),
    })
  }

  async searchCustomerByName(firstName: string, lastName: string) {
    return this.request(`/customer/search?firstName=${firstName}&lastName=${lastName}`)
  }

  async filterTransactions(filters: any) {
    const params = new URLSearchParams(filters).toString()
    return this.request(`/customer/transactions/filter?${params}`)
  }

  // ATM endpoints
  async atmLogin(email: string, password: string) {
    return this.request("/atm/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    })
  }

  async atmWithdraw(amount: number, accountId: string) {
    return this.request("/atm/withdraw", {
      method: "POST",
      body: JSON.stringify({ amount, accountId }),
    })
  }

  async atmDeposit(amount: number, accountId: string) {
    return this.request("/atm/deposit", {
      method: "POST",
      body: JSON.stringify({ amount, accountId }),
    })
  }

  // Employee endpoints
  async getAllCustomers() {
    return this.request("/employee/customers")
  }

  async getPendingApprovals() {
    return this.request("/employee/pending-approvals")
  }

  async approveCustomer(customerId: string, accountLimits: any) {
    return this.request(`/employee/approve-customer/${customerId}`, {
      method: "POST",
      body: JSON.stringify(accountLimits),
    })
  }

  async getAllTransactions() {
    return this.request("/employee/transactions")
  }

  async getCustomerTransactionsById(customerId: string) {
    return this.request(`/employee/customers/${customerId}/transactions`)
  }

  async employeeTransfer(transferData: any) {
    return this.request("/employee/transfer", {
      method: "POST",
      body: JSON.stringify(transferData),
    })
  }

  async updateAccountLimits(accountId: string, limits: any) {
    return this.request(`/employee/accounts/${accountId}/limits`, {
      method: "PUT",
      body: JSON.stringify(limits),
    })
  }

  async closeAccount(accountId: string) {
    return this.request(`/employee/accounts/${accountId}/close`, {
      method: "DELETE",
    })
  }
}

export const apiService = new ApiService()
