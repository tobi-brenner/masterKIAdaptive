import NextAuth from 'next-auth';
import Credentials from 'next-auth/providers/credentials';
import bcrypt from 'bcrypt';
import { sql } from '@vercel/postgres';
import { z } from 'zod';
import type { User } from '@/app/lib/definitions';
import { authConfig } from './auth.config';
import {BE_BASE_URL} from "./constants";
async function getUser(email: string): Promise<User | undefined> {
  try {
    const user = await sql<User>`SELECT * FROM users WHERE email=${email}`;
    return user.rows[0];
  } catch (error) {
    console.error('Failed to fetch user:', error);
    throw new Error('Failed to fetch user.');
  }
}


// Assuming `loginUser` is defined in another file
async function loginUser(email: string, password: string): Promise<User | null> {
  try {
    const response = await fetch(`${BE_BASE_URL}/users/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
      console.error('Login failed:', response.statusText);
      return null; // Return null on failed network request
    }

    const user: User = await response.json();
    console.log(user, "user");
    return user || null;
  } catch (error) {
    console.error('Failed to log in user:', error);
    return null;
  }
}

export const { handlers, signIn, signOut, auth} = NextAuth({
  ...authConfig,
  providers: [
    Credentials({
      async authorize(credentials) {
        console.log(credentials);
        const parsedCredentials = z
          .object({ email: z.string().email(), password: z.string().min(6) })
          .safeParse(credentials);
        console.log(parsedCredentials);

        if (parsedCredentials.success) {
          console.log(parsedCredentials, "success");
          const { email, password } = parsedCredentials.data;

          const user = await loginUser(email, password);
          console.log(user, "logged in user");
          if(user) user.name = user?.firstName + " " + user?.lastName;
          // if(user) user.role = "student";
          console.log(user, "with role");

          return user;
          // initial vercel postgresdb
          // const user = await getUser(email);
          // if (!user) return null;
          //
          // const passwordsMatch = await bcrypt.compare(password, user.password);
          // if (passwordsMatch) return user;
        }

        console.log('Invalid credentials');
        return null;
      },
    }),
  ],
});