
import { fetchLatestInvoices } from '@/app/lib/data';
export default async function LatestInvoices() {
  const latestInvoices = await fetchLatestInvoices();

  return (
      <div></div>

  );
}
