import {lusitana} from '@/app/ui/fonts';
import {Suspense} from 'react';
import {
    CardsSkeleton,
} from '@/app/ui/skeletons';
import CourseCardWrapper from "@/app/ui/courses/cards";
import {auth} from "@/auth";
import Link from "next/link";

export default async function Page() {
    const session = await auth();
    return (
        <main>
            <div className="flex justify-between items-center w-full">
                <h1 className={`${lusitana.className} mb-4 text-xl md:text-2xl`}>
                    Kurse
                </h1>
                {session &&
                <Link
                    href="courses/create"
                    className="ml-auto mt-4 rounded-md bg-blue-500 px-4 py-2 text-sm text-white transition-colors hover:bg-blue-400"
                >
                    Neuer Kurs
                </Link>
                }
            </div>
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                <Suspense fallback={<CardsSkeleton/>}>
                    <CourseCardWrapper/>
                </Suspense>
            </div>
        </main>
    );
}
