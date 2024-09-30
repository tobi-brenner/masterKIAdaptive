import NextAuth from "next-auth"

declare module "next-auth" {
    /**
     * Extends the built-in session.user type
     */
    interface User {
        id?: string
        name?: string | null
        email?: string | null
        image?: string | null
        role?: string | null;
    }

    /**
     * Extends the built-in session type
     */
    interface Session {
        user: User;
    }
}
