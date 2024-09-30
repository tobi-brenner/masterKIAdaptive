import type { NextAuthConfig } from 'next-auth';

export interface User {
  id?: string
  name?: string | null
  email?: string | null
  image?: string | null
  role?: string | null
}
export interface AdapterUser {
  role?: string | null
}
export const authConfig = {
  secret: "3VQS/Jqt+FhUQpkUzeRpXzGwTrXjSsGu4ZPDCojsPfE=",
  pages: {
    signIn: '/login',
  },
  providers: [
    // added later in session.ts since it requires bcrypt which is only compatible with Node.js
    // while this file is also used in non-Node.js environments
  ],
  callbacks: {
    authorized({ auth, request: { nextUrl } }) {
      const isLoggedIn = !!auth?.user;
      const isOnDashboard = nextUrl.pathname.startsWith('/dashboard') || nextUrl.pathname.startsWith('/learn');
      if (isOnDashboard) {
        if (isLoggedIn) return true;
        return false; // Redirect unauthenticated users to login page
      } else if (isLoggedIn) {
        return Response.redirect(new URL('/dashboard', nextUrl));
      }
      return true;
    },
    jwt: async ({ token, user,account, profile, session }) => {
      console.log("USER JWT ", user);
      if (user) {
        token.uid = user.id;
        token.role = user.role;
      }
      return token;
    },
    session: async ({ session, token }) => {
      if(token.role) {
        // @ts-ignore
        session.user.id = token.sub
        // @ts-ignore
        session.user.role = token.role;
      }
      return session;
    },
  },
} satisfies NextAuthConfig;

