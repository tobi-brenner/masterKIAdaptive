import {lusitana} from '@/app/ui/fonts';
import {Suspense} from 'react';
import {
    CardsSkeleton,
} from '@/app/ui/skeletons';
import PathCardWrapper from "@/app/ui/learn/path-cards";
import {auth} from "@/auth";
import Link from "next/link";

export default async function Page() {
    const session = await auth();
    return (
        <main>
            <div className="flex justify-between items-center w-full">
                <h1 className={`${lusitana.className} mb-4 text-xl md:text-2xl`}>
                    Meine Kurse
                </h1>
            </div>
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                <Suspense fallback={<CardsSkeleton/>}>
                    <PathCardWrapper userId={session?.user.id} />
                </Suspense>
            </div>
        </main>
    );
}
