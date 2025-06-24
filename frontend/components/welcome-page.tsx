import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Clock, CheckCircle } from "lucide-react"

interface WelcomePageProps {
  user: {
    firstName: string
    lastName: string
    email: string
  }
}

export function WelcomePage({ user }: WelcomePageProps) {
  return (
    <div className="container mx-auto py-12">
      <div className="flex min-h-[60vh] items-center justify-center">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-orange-100 dark:bg-orange-900">
              <Clock className="h-6 w-6 text-orange-600 dark:text-orange-400" />
            </div>
            <CardTitle className="text-2xl">Welcome, {user.firstName}!</CardTitle>
            <CardDescription>Your account is pending approval</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="text-center text-sm text-muted-foreground">
              <p>Thank you for registering with Modern Bank.</p>
              <p className="mt-2">
                Your account registration has been received and is currently being reviewed by our team.
              </p>
            </div>

            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <CheckCircle className="h-4 w-4 text-green-500" />
                <span className="text-sm">Registration completed</span>
              </div>
              <div className="flex items-center gap-3">
                <Clock className="h-4 w-4 text-orange-500" />
                <span className="text-sm">Account approval pending</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-4 w-4 rounded-full border-2 border-muted" />
                <span className="text-sm text-muted-foreground">Bank accounts creation</span>
              </div>
            </div>

            <div className="mt-6 rounded-lg bg-muted p-4">
              <p className="text-sm">
                <strong>What happens next?</strong>
              </p>
              <p className="mt-1 text-sm text-muted-foreground">
                An employee will review your application and create your checking and savings accounts. You'll receive
                an email notification once your accounts are ready.
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
