import CardWrapper from '@/app/ui/dashboard/cards';
import RevenueChart from '@/app/ui/dashboard/revenue-chart';
import LatestInvoices from '@/app/ui/dashboard/latest-invoices';
import { lusitana } from '@/app/ui/fonts';
import { Suspense } from 'react';
import {
  RevenueChartSkeleton,
  LatestInvoicesSkeleton,
  CardsSkeleton,
} from '@/app/ui/skeletons';
import {auth} from "@/auth";
import CourseCardWrapper from "@/app/ui/courses/cards";
import PathCardWrapper from "@/app/ui/learn/path-cards";

export default async function Page() {
    const session = await auth();
  return (
      <main>
          <div className="flex justify-between items-center w-full">
              <h1 className={`${lusitana.className} mb-4 text-xl md:text-2xl`}>
                  Alle Kurse
              </h1>
          </div>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              <Suspense fallback={<CardsSkeleton/>}>
                  <CourseCardWrapper/>
              </Suspense>
          </div>
          <hr/>
          <div className="flex justify-between items-center w-full mt-3">
              <h1 className={`${lusitana.className} mb-4 text-xl md:text-2xl`}>
                  Eingeschriebene Kurse
              </h1>
          </div>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              <Suspense fallback={<CardsSkeleton/>}>
                  <PathCardWrapper userId={session?.user.id}/>
              </Suspense>
          </div>
          {/*<h1 className={`${lusitana.className} mb-4 text-xl md:text-2xl`}>*/}
          {/*  Dashboard*/}
          {/*</h1>*/}
          {/*<div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">*/}
          {/*  <Suspense fallback={<CardsSkeleton />}>*/}
          {/*    <CardWrapper />*/}
          {/*  </Suspense>*/}
          {/*</div>*/}
          {/*<div className="mt-6 grid grid-cols-1 gap-6 md:grid-cols-4 lg:grid-cols-8">*/}
          {/*  <Suspense fallback={<RevenueChartSkeleton />}>*/}
          {/*    <RevenueChart />*/}
          {/*  </Suspense>*/}
          {/*  <Suspense fallback={<LatestInvoicesSkeleton />}>*/}
          {/*    <LatestInvoices />*/}
          {/*  </Suspense>*/}
          {/*</div>*/}
      </main>
  );
}
