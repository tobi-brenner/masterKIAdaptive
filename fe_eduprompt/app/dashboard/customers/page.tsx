import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Customers',
};

export default async function Page() {

  return (
    <main>
      <p>hi</p>
      {/*<CustomersTable customers={customers} />*/}
    </main>
  );
}
