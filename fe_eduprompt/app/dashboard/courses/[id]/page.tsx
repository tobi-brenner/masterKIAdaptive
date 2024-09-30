import Breadcrumbs from '@/app/ui/invoices/breadcrumbs';
import { notFound } from 'next/navigation';
import { Metadata } from 'next';
import {auth} from "@/auth";
import Form from "@/app/ui/courses/create-form";
import {CardsSkeleton} from "@/app/ui/skeletons";
import {Suspense} from "react";
import {fetchCoursesById} from "@/app/lib/api/courses/course";
import EditForm from "@/app/ui/courses/edit-form";
import {Toaster} from "@/components/ui/toaster";

export const metadata: Metadata = {
    title: 'Edit Course',
};

export default async function Page({ params }: { params: { id: string } }) {
    const courseId = params.id;
    const session = await auth();
    const course = await fetchCoursesById(courseId);
    if(session?.user.role == "teacher" && session?.user.id) {
        return (
            <main>
                <Breadcrumbs
                    breadcrumbs={[
                        {label: 'Courses', href: '/dashboard/courses'},
                        {
                            label: 'Edit Course',
                            href: `/dashboard/courses/${courseId}`,
                            active: true,
                        },
                    ]}
                />
                <div>
                    {/*<Suspense fallback={<CardsSkeleton />}>*/}
                        <EditForm profUserId={session.user.id} course={course}/>
                    {/*</Suspense>*/}
                </div>
            </main>
        )
    } else if(session?.user.id) {
        return (
            <main>
                {/*<Breadcrumbs*/}
                {/*    breadcrumbs={[*/}
                {/*        {label: 'Courses', href: '/dashboard/courses'},*/}
                {/*        {*/}
                {/*            label: 'Edit Course',*/}
                {/*            href: `/dashboard/invoices/${courseId}/edit`,*/}
                {/*            active: true,*/}
                {/*        },*/}
                {/*    ]}*/}
                {/*/>*/}

                <div>
                    {/*<Suspense fallback={<CardsSkeleton />}>*/}
                    <Form profUserId={session.user.id}/>
                    {/*</Suspense>*/}
                </div>
            </main>
        );
    }


}
