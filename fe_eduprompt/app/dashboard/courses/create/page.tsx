import Breadcrumbs from '@/app/ui/invoices/breadcrumbs';
import { Metadata } from 'next';
import {auth} from "@/auth";
import Form from "@/app/ui/courses/create-form";

export const metadata: Metadata = {
    title: 'Edit Course',
};

export default async function Page({ params }: { params: { id: string } }) {
    const id = params.id;
    const session = await auth()
    console.log(session,"sessionihhiihhihi");
    if(session?.user.role == "teacher" && session?.user.id) {
        return (
            <main>
                <Breadcrumbs
                    breadcrumbs={[
                        {label: 'Courses', href: '/dashboard/courses'},
                        {
                            label: 'Edit Course',
                            href: `/dashboard/invoices/${id}/edit`,
                            active: true,
                        },
                    ]}
                />
                <div>
                    {/*<Suspense fallback={<CardsSkeleton />}>*/}
                        <Form profUserId={session.user.id}/>
                    {/*</Suspense>*/}
                </div>
            </main>
        )
    } else {
        return (
            <main>
                <Breadcrumbs
                    breadcrumbs={[
                        {label: 'Courses', href: '/dashboard/courses'},
                        {
                            label: 'Edit Course',
                            href: `/dashboard/invoices/${id}/edit`,
                            active: true,
                        },
                    ]}
                />
            </main>
        );
    }


}
