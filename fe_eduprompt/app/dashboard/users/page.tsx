// import { fetchFilteredCustomers, fetchUsers } from '@/app/lib/data';
import CustomersTable from '@/app/ui/customers/table';
import { Metadata } from 'next';
import UsersTable from "@/app/ui/users/table";
import Search from "@/app/ui/search";
import {fetchUsers} from "@/app/lib/api/user/user";

export const metadata: Metadata = {
  title: 'Users',
};

export default async function Page({
  searchParams,
}: {
  searchParams?: {
    query?: string;
    page?: string;
  };
}) {
  const query = searchParams?.query || '';

  // const customers = await fetchFilteredCustomers(query);
  const users = await fetchUsers(query);
  console.log("Users", users);

  return (
    <main>
      {/*<CustomersTable customers={users} />*/}
      {/*<Search placeholder="Search Users..." />*/}
      <UsersTable users={users} />
    </main>
  );
}
